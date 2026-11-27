
package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.TransactionCreationDTO;
import com.ifpr.backend.dto.TransactionResponseDTO;
import com.ifpr.backend.dto.TransactionUpdateDTO;
import com.ifpr.backend.entity.Category;
import com.ifpr.backend.entity.Transaction;
import com.ifpr.backend.entity.TransactionType;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.entity.Wallet;
import com.ifpr.backend.exception.BusinessException;
import com.ifpr.backend.repository.CategoryRepository;
import com.ifpr.backend.repository.TransactionRepository;
import com.ifpr.backend.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(
                transactionRepository,
                walletRepository,
                categoryRepository
        );
    }

    @Test
    void shouldCreateExpenseTransactionForAuthenticatedUser() {

        User author = new User();
        author.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(author);

        Category category = new Category();
        category.setId(20L);
        category.setUser(author);
        category.setType(TransactionType.EXPENSE);

        LocalDate date = LocalDate.of(2026, 11, 26);

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "  Compra no supermercado  ",
                new BigDecimal("189.90"),
                TransactionType.EXPENSE,
                date,
                10L,
                20L
        );

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(categoryRepository.findByIdAndUser_Id(20L, 3L))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {

                    Transaction transaction = invocation.getArgument(0);
                    transaction.setId(100L);

                    return transaction;
                });

        Optional<TransactionResponseDTO> response =
                transactionService.create(dto, author);

        assertTrue(response.isPresent());
        assertEquals(100L, response.get().id());
        assertEquals(
                "Compra no supermercado",
                response.get().description()
        );
        assertEquals(
                new BigDecimal("189.90"),
                response.get().amount()
        );
        assertEquals(TransactionType.EXPENSE, response.get().type());
        assertEquals(date, response.get().transactionDate());
        assertEquals(10L, response.get().walletId());
        assertEquals(20L, response.get().categoryId());
        assertEquals(3L, response.get().createdById());
        assertNotNull(response.get().createdAt());

        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertSame(wallet, savedTransaction.getWallet());
        assertSame(category, savedTransaction.getCategory());
        assertSame(author, savedTransaction.getCreatedBy());

        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(20L, 3L);
    }

    @Test
    void shouldCreateIncomeTransaction() {

        User author = new User();
        author.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(author);

        Category category = new Category();
        category.setId(21L);
        category.setUser(author);
        category.setType(TransactionType.INCOME);

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Salário",
                new BigDecimal("3500.00"),
                TransactionType.INCOME,
                LocalDate.of(2026, 11, 26),
                10L,
                21L
        );

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(categoryRepository.findByIdAndUser_Id(21L, 3L))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {

                    Transaction transaction = invocation.getArgument(0);
                    transaction.setId(101L);

                    return transaction;
                });

        Optional<TransactionResponseDTO> response =
                transactionService.create(dto, author);

        assertTrue(response.isPresent());
        assertEquals(101L, response.get().id());
        assertEquals("Salário", response.get().description());
        assertEquals(
                new BigDecimal("3500.00"),
                response.get().amount()
        );
        assertEquals(TransactionType.INCOME, response.get().type());
        assertEquals(3L, response.get().createdById());

        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(21L, 3L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldNotCreateTransactionInAnotherUsersWallet() {

        User author = new User();
        author.setId(4L);

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra não autorizada",
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                10L,
                20L
        );

        when(walletRepository.findByIdAndOwner_Id(10L, 4L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.create(dto, author);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(10L, 4L);

        verifyNoInteractions(categoryRepository, transactionRepository);
    }

    @Test
    void shouldNotCreateTransactionWithAnotherUsersCategory() {

        User author = new User();
        author.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(author);

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra com categoria não autorizada",
                new BigDecimal("80.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                10L,
                30L
        );

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(categoryRepository.findByIdAndUser_Id(30L, 3L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.create(dto, author);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(30L, 3L);

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldRejectTransactionWithIncompatibleCategoryType() {

        User author = new User();
        author.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(author);

        Category category = new Category();
        category.setId(20L);
        category.setUser(author);
        category.setType(TransactionType.INCOME);

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
                new BigDecimal("189.90"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                10L,
                20L
        );

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(categoryRepository.findByIdAndUser_Id(20L, 3L))
                .thenReturn(Optional.of(category));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.create(dto, author)
        );

        assertEquals(
                "O tipo da transação deve corresponder ao tipo da categoria",
                exception.getMessage()
        );

        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(20L, 3L);

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldListTransactionsFromOwnedWallet() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        Category expenseCategory = new Category();
        expenseCategory.setId(20L);
        expenseCategory.setType(TransactionType.EXPENSE);

        Category incomeCategory = new Category();
        incomeCategory.setId(21L);
        incomeCategory.setType(TransactionType.INCOME);

        Transaction expense = new Transaction();
        expense.setId(100L);
        expense.setDescription("Supermercado");
        expense.setAmount(new BigDecimal("189.90"));
        expense.setType(TransactionType.EXPENSE);
        expense.setTransactionDate(LocalDate.of(2026, 11, 25));
        expense.setWallet(wallet);
        expense.setCategory(expenseCategory);
        expense.setCreatedBy(owner);
        expense.setCreatedAt(LocalDateTime.of(2026, 11, 25, 12, 0));

        Transaction income = new Transaction();
        income.setId(101L);
        income.setDescription("Salário");
        income.setAmount(new BigDecimal("3500.00"));
        income.setType(TransactionType.INCOME);
        income.setTransactionDate(LocalDate.of(2026, 11, 26));
        income.setWallet(wallet);
        income.setCategory(incomeCategory);
        income.setCreatedBy(owner);
        income.setCreatedAt(LocalDateTime.of(2026, 11, 26, 9, 0));

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(transactionRepository.findByWallet_Id(10L))
                .thenReturn(List.of(expense, income));

        Optional<List<TransactionResponseDTO>> response =
                transactionService.listOwnedByWallet(10L, owner);

        assertTrue(response.isPresent());
        assertEquals(2, response.get().size());

        TransactionResponseDTO first = response.get().get(0);

        assertEquals(100L, first.id());
        assertEquals("Supermercado", first.description());
        assertEquals(new BigDecimal("189.90"), first.amount());
        assertEquals(TransactionType.EXPENSE, first.type());
        assertEquals(10L, first.walletId());
        assertEquals(20L, first.categoryId());
        assertEquals(3L, first.createdById());

        TransactionResponseDTO second = response.get().get(1);

        assertEquals(101L, second.id());
        assertEquals("Salário", second.description());
        assertEquals(new BigDecimal("3500.00"), second.amount());
        assertEquals(TransactionType.INCOME, second.type());
        assertEquals(10L, second.walletId());
        assertEquals(21L, second.categoryId());

        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(transactionRepository).findByWallet_Id(10L);
    }

    @Test
    void shouldReturnEmptyListWhenOwnedWalletHasNoTransactions() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(transactionRepository.findByWallet_Id(10L))
                .thenReturn(List.of());

        Optional<List<TransactionResponseDTO>> response =
                transactionService.listOwnedByWallet(10L, owner);

        assertTrue(response.isPresent());
        assertTrue(response.get().isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(transactionRepository).findByWallet_Id(10L);
    }

    @Test
    void shouldNotListTransactionsFromAnotherUsersWallet() {

        User owner = new User();
        owner.setId(4L);

        when(walletRepository.findByIdAndOwner_Id(10L, 4L))
                .thenReturn(Optional.empty());

        Optional<List<TransactionResponseDTO>> response =
                transactionService.listOwnedByWallet(10L, owner);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(10L, 4L);

        verifyNoInteractions(transactionRepository, categoryRepository);
    }

    @Test
    void shouldFindTransactionFromOwnedWallet() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        Category category = new Category();
        category.setId(20L);
        category.setType(TransactionType.EXPENSE);

        LocalDate transactionDate =
                LocalDate.of(2026, 11, 26);

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 11, 26, 15, 30);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setDescription("Supermercado");
        transaction.setAmount(new BigDecimal("189.90"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setTransactionDate(transactionDate);
        transaction.setWallet(wallet);
        transaction.setCategory(category);
        transaction.setCreatedBy(owner);
        transaction.setCreatedAt(createdAt);

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 3L))
                .thenReturn(Optional.of(transaction));

        Optional<TransactionResponseDTO> response =
                transactionService.findOwnedById(100L, owner);

        assertTrue(response.isPresent());

        TransactionResponseDTO result = response.get();

        assertEquals(100L, result.id());
        assertEquals("Supermercado", result.description());
        assertEquals(new BigDecimal("189.90"), result.amount());
        assertEquals(TransactionType.EXPENSE, result.type());
        assertEquals(transactionDate, result.transactionDate());
        assertEquals(10L, result.walletId());
        assertEquals(20L, result.categoryId());
        assertEquals(3L, result.createdById());
        assertEquals(createdAt, result.createdAt());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 3L);

        verifyNoInteractions(walletRepository, categoryRepository);
    }

    @Test
    void shouldNotFindTransactionFromAnotherUsersWallet() {

        User owner = new User();
        owner.setId(4L);

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 4L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.findOwnedById(100L, owner);

        assertTrue(response.isEmpty());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 4L);

        verifyNoInteractions(walletRepository, categoryRepository);
    }

    @Test
    void shouldNotFindNonexistentTransaction() {

        User owner = new User();
        owner.setId(3L);

        when(transactionRepository.findByIdAndWallet_Owner_Id(999L, 3L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.findOwnedById(999L, owner);

        assertTrue(response.isEmpty());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(999L, 3L);

        verifyNoInteractions(walletRepository, categoryRepository);
    }

    @Test
    void shouldUpdateOwnedTransactionAndPreserveAuditFields() {

        User owner = new User();
        owner.setId(3L);

        User originalAuthor = new User();
        originalAuthor.setId(5L);

        Wallet originalWallet = new Wallet();
        originalWallet.setId(10L);
        originalWallet.setOwner(owner);

        Wallet destinationWallet = new Wallet();
        destinationWallet.setId(11L);
        destinationWallet.setOwner(owner);

        Category originalCategory = new Category();
        originalCategory.setId(20L);
        originalCategory.setType(TransactionType.EXPENSE);

        Category newCategory = new Category();
        newCategory.setId(21L);
        newCategory.setUser(owner);
        newCategory.setType(TransactionType.INCOME);

        LocalDateTime originalCreatedAt =
                LocalDateTime.of(2026, 11, 20, 10, 30);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setDescription("Supermercado");
        transaction.setAmount(new BigDecimal("189.90"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setTransactionDate(LocalDate.of(2026, 11, 20));
        transaction.setWallet(originalWallet);
        transaction.setCategory(originalCategory);
        transaction.setCreatedBy(originalAuthor);
        transaction.setCreatedAt(originalCreatedAt);

        LocalDate newDate = LocalDate.of(2026, 11, 26);

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "  Reembolso  ",
                new BigDecimal("120.00"),
                TransactionType.INCOME,
                newDate,
                11L,
                21L
        );

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 3L))
                .thenReturn(Optional.of(transaction));

        when(walletRepository.findByIdAndOwner_Id(11L, 3L))
                .thenReturn(Optional.of(destinationWallet));

        when(categoryRepository.findByIdAndUser_Id(21L, 3L))
                .thenReturn(Optional.of(newCategory));

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        Optional<TransactionResponseDTO> response =
                transactionService.updateOwned(100L, dto, owner);

        assertTrue(response.isPresent());

        TransactionResponseDTO result = response.get();

        assertEquals(100L, result.id());
        assertEquals("Reembolso", result.description());
        assertEquals(new BigDecimal("120.00"), result.amount());
        assertEquals(TransactionType.INCOME, result.type());
        assertEquals(newDate, result.transactionDate());
        assertEquals(11L, result.walletId());
        assertEquals(21L, result.categoryId());
        assertEquals(5L, result.createdById());
        assertEquals(originalCreatedAt, result.createdAt());

        assertSame(destinationWallet, transaction.getWallet());
        assertSame(newCategory, transaction.getCategory());
        assertSame(originalAuthor, transaction.getCreatedBy());
        assertEquals(originalCreatedAt, transaction.getCreatedAt());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 3L);
        verify(walletRepository).findByIdAndOwner_Id(11L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(21L, 3L);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldNotUpdateTransactionFromAnotherUsersWallet() {

        User owner = new User();
        owner.setId(4L);

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Alteração não autorizada",
                new BigDecimal("120.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                10L,
                20L
        );

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 4L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.updateOwned(100L, dto, owner);

        assertTrue(response.isEmpty());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 4L);

        verifyNoInteractions(walletRepository, categoryRepository);
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void shouldNotUpdateTransactionToAnotherUsersWallet() {

        User owner = new User();
        owner.setId(3L);

        Wallet originalWallet = new Wallet();
        originalWallet.setId(10L);
        originalWallet.setOwner(owner);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setDescription("Supermercado");
        transaction.setWallet(originalWallet);

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Movimentação indevida",
                new BigDecimal("120.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                30L,
                20L
        );

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 3L))
                .thenReturn(Optional.of(transaction));

        when(walletRepository.findByIdAndOwner_Id(30L, 3L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.updateOwned(100L, dto, owner);

        assertTrue(response.isEmpty());

        assertEquals("Supermercado", transaction.getDescription());
        assertSame(originalWallet, transaction.getWallet());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 3L);
        verify(walletRepository).findByIdAndOwner_Id(30L, 3L);

        verifyNoInteractions(categoryRepository);
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void shouldNotUpdateTransactionWithAnotherUsersCategory() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setDescription("Supermercado");
        transaction.setWallet(wallet);

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Alteração com categoria alheia",
                new BigDecimal("120.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                10L,
                30L
        );

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 3L))
                .thenReturn(Optional.of(transaction));

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(categoryRepository.findByIdAndUser_Id(30L, 3L))
                .thenReturn(Optional.empty());

        Optional<TransactionResponseDTO> response =
                transactionService.updateOwned(100L, dto, owner);

        assertTrue(response.isEmpty());
        assertEquals("Supermercado", transaction.getDescription());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 3L);
        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(30L, 3L);

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void shouldRejectUpdateWithIncompatibleCategoryType() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        Category category = new Category();
        category.setId(21L);
        category.setUser(owner);
        category.setType(TransactionType.INCOME);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setDescription("Supermercado");
        transaction.setWallet(wallet);

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra alterada",
                new BigDecimal("120.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 26),
                10L,
                21L
        );

        when(transactionRepository.findByIdAndWallet_Owner_Id(100L, 3L))
                .thenReturn(Optional.of(transaction));

        when(walletRepository.findByIdAndOwner_Id(10L, 3L))
                .thenReturn(Optional.of(wallet));

        when(categoryRepository.findByIdAndUser_Id(21L, 3L))
                .thenReturn(Optional.of(category));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.updateOwned(100L, dto, owner)
        );

        assertEquals(
                "O tipo da transação deve corresponder ao tipo da categoria",
                exception.getMessage()
        );

        assertEquals("Supermercado", transaction.getDescription());
        assertSame(wallet, transaction.getWallet());

        verify(transactionRepository)
                .findByIdAndWallet_Owner_Id(100L, 3L);
        verify(walletRepository).findByIdAndOwner_Id(10L, 3L);
        verify(categoryRepository).findByIdAndUser_Id(21L, 3L);

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }
}
