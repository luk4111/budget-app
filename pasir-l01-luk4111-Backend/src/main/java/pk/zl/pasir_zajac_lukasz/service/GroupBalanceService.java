package pk.zl.pasir_zajac_lukasz.service;

import org.springframework.stereotype.Service;
import pk.zl.pasir_zajac_lukasz.dto.BalanceDTO;
import pk.zl.pasir_zajac_lukasz.model.Debt;
import pk.zl.pasir_zajac_lukasz.model.Transaction;
import pk.zl.pasir_zajac_lukasz.model.TransactionType;
import pk.zl.pasir_zajac_lukasz.model.User;
import pk.zl.pasir_zajac_lukasz.repository.DebtRepository;
import pk.zl.pasir_zajac_lukasz.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupBalanceService {

    private final TransactionRepository transactionRepository;
    private final DebtRepository debtRepository;
    private final CurrentUserService currentUserService;

    public GroupBalanceService(
            TransactionRepository transactionRepository,
            DebtRepository debtRepository,
            CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.debtRepository = debtRepository;
        this.currentUserService = currentUserService;
    }

    public BalanceDTO calculateFinalBalance(Float days) {
        User currentUser = currentUserService.getCurrentUser();

        List<Transaction> userTransactions;
        if (days != null && days > 0) {
            LocalDateTime threshold = LocalDateTime.now().minusSeconds((long) (days * 24 * 60 * 60));
            userTransactions = transactionRepository.findAllByUserAndTimestampGreaterThanEqual(currentUser, threshold);
        } else {
            userTransactions = transactionRepository.findByUser(currentUser);
        }

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double groupBalances = 0.0;
        List<Debt> allDebts = debtRepository.findAll();

        for (Debt debt : allDebts) {

            if (debt.getDebtor().getId().equals(currentUser.getId())) {
                groupBalances -= debt.getAmount();
            }

            if (debt.getCreditor().getId().equals(currentUser.getId())) {
                groupBalances += debt.getAmount();
            }
        }

        double finalBalance = (income - expense) + groupBalances;

        return new BalanceDTO(income, expense, finalBalance);
    }
}