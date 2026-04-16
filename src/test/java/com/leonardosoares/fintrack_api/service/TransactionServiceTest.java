package com.leonardosoares.fintrack_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.leonardosoares.fintrack_api.config.exception.ResourceNotFoundException;
import com.leonardosoares.fintrack_api.controller.dto.TransactionRequest;
import com.leonardosoares.fintrack_api.controller.dto.TransactionResponse;
import com.leonardosoares.fintrack_api.controller.dto.summary.SummaryResponse;
import com.leonardosoares.fintrack_api.mapper.TransactionMapper;
import com.leonardosoares.fintrack_api.model.Category;
import com.leonardosoares.fintrack_api.model.Transaction;
import com.leonardosoares.fintrack_api.model.User;
import com.leonardosoares.fintrack_api.model.enums.TransactionType;
import com.leonardosoares.fintrack_api.repository.CategoryRepository;
import com.leonardosoares.fintrack_api.repository.TransactionRepository;
import com.leonardosoares.fintrack_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private User mockedUser;
    private final String userEmail = "teste@email.com";

    @BeforeEach
    void setUp() {
        mockedUser = new User();
        mockedUser.setId(UUID.randomUUID());
        mockedUser.setEmail(userEmail);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEmail);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve criar transação com sucesso (buscando usuário e categoria)")
    void createTransaction_ShouldSaveAndReturnResponse() {
        
        mockSecurityContext(); 

        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);

        TransactionRequest request = new TransactionRequest(
                BigDecimal.valueOf(100.50),
                TransactionType.DESPESA,
                LocalDate.now(),
                "Mercado",
                categoryId
        );

        TransactionResponse expectedResponse = new TransactionResponse(
                UUID.randomUUID(), 
                BigDecimal.valueOf(100.50), 
                TransactionType.DESPESA,
                LocalDate.now(), 
                java.time.LocalDateTime.now(), 
                "Mercado", 
                categoryId,
                mockedUser.getId()             
        );

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockedUser));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100.50), response.amount());
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve retornar a transação quando o ID existir")
    void getTransactionById_WhenExists_ShouldReturnResponse() {
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        TransactionResponse expectedResponse = new TransactionResponse(
                transactionId, 
                BigDecimal.TEN, 
                TransactionType.RECEITA, 
                LocalDate.now(), 
                java.time.LocalDateTime.now(), 
                "Pix", 
                UUID.randomUUID(),
                mockedUser.getId()             
        );

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.getTransactionById(transactionId);

        assertNotNull(response);
        assertEquals(expectedResponse.id(), response.id());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a transação não for encontrada por ID")
    void getTransactionById_WhenDoesNotExist_ShouldThrowException() {

        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionById(transactionId);
        });

        assertEquals("Transaction not found!", exception.getMessage());
        verify(transactionMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve deletar a transação chamando o deleteById no repositório")
    void deleteTransaction_ShouldCallRepositoryDelete() {
        UUID transactionId = UUID.randomUUID();

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    @DisplayName("Deve calcular o resumo (saldo) corretamente")
    void getSummary_ShouldCalculateBalanceCorrectly() {
        mockSecurityContext();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockedUser));

        when(transactionRepository.sumByUserAndType(mockedUser, TransactionType.RECEITA))
                .thenReturn(BigDecimal.valueOf(5000));
        when(transactionRepository.sumByUserAndType(mockedUser, TransactionType.DESPESA))
                .thenReturn(BigDecimal.valueOf(2000));

        SummaryResponse summary = transactionService.getSummary();

        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(5000), summary.totalReceita());
        assertEquals(BigDecimal.valueOf(2000), summary.totalDespesas());
        assertEquals(BigDecimal.valueOf(3000), summary.saldo());
    }

    @Test
    @DisplayName("Deve calcular o resumo considerando nulos como zero")
    void getSummary_WithNullSums_ShouldTreatAsZero() {
        mockSecurityContext();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockedUser));

        when(transactionRepository.sumByUserAndType(mockedUser, TransactionType.RECEITA)).thenReturn(null);
        when(transactionRepository.sumByUserAndType(mockedUser, TransactionType.DESPESA)).thenReturn(null);

        SummaryResponse summary = transactionService.getSummary();

        assertNotNull(summary);
        assertEquals(BigDecimal.ZERO, summary.totalReceita()); 
        assertEquals(BigDecimal.ZERO, summary.totalDespesas()); 
        assertEquals(BigDecimal.ZERO, summary.saldo());
    }
}