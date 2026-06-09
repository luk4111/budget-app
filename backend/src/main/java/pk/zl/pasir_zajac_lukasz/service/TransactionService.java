package pk.zl.pasir_zajac_lukasz.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pk.zl.pasir_zajac_lukasz.dto.BalanceDTO;
import pk.zl.pasir_zajac_lukasz.dto.TransactionDTO;
import pk.zl.pasir_zajac_lukasz.model.Transaction;
import pk.zl.pasir_zajac_lukasz.model.TransactionType;
import pk.zl.pasir_zajac_lukasz.model.User;
import pk.zl.pasir_zajac_lukasz.repository.TransactionRepository;
import pk.zl.pasir_zajac_lukasz.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this. userRepository = userRepository;
    }

    public List<Transaction> getAllTransactions() {
        User user = getCurrentUser();
        return transactionRepository.findAllByUser(user);
    }

    public Transaction getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new org.springframework.security.access.AccessDeniedException("Nie masz dostępu do tej transakcji");
        }
        return transaction;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(getCurrentUser());
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new org.springframework.security.access.AccessDeniedException("Nie masz dostępu do tej transakcji");
        }
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
        transactionRepository.delete(transaction);
        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new org.springframework.security.access.AccessDeniedException("Nie masz dostępu do tej transakcji");
        }
    }
    public User getCurrentUser() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.security.access.AccessDeniedException("Użytkownik nie jest uwierzytelniony");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Nie znaleziono zalogowanego użytkownika: " + email));

    }
    public BalanceDTO getUserBalance(User user, Float days) {
        List<Transaction> userTransactions;

        if (days != null && days > 0) {
            LocalDateTime threshold = LocalDateTime.now().minusSeconds((long) (days * 24 * 60 * 60));
            userTransactions = transactionRepository.findAllByUserAndTimestampGreaterThanEqual(user, threshold);
        } else {
            userTransactions = transactionRepository.findByUser(user);
        }

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new BalanceDTO(income, expense, income - expense);
    }
}