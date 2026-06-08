package pk.zl.pasir_zajac_lukasz.controller;

import pk.zl.pasir_zajac_lukasz.dto.BalanceDTO;
import pk.zl.pasir_zajac_lukasz.model.Transaction;
import pk.zl.pasir_zajac_lukasz.dto.TransactionDTO;
import pk.zl.pasir_zajac_lukasz.service.TransactionService;
import pk.zl.pasir_zajac_lukasz.service.GroupBalanceService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import java.util.List;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;
    private final GroupBalanceService groupBalanceService;

    public TransactionGraphQLController(TransactionService transactionService, GroupBalanceService groupBalanceService) {
        this.transactionService = transactionService;
        this.groupBalanceService = groupBalanceService;
    }

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @MutationMapping
    public Transaction addTransaction(@Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @MutationMapping
    public Transaction updateTransaction(@Argument Long id, @Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(id, transactionDTO);
    }

    @MutationMapping
    public Boolean deleteTransaction(@Argument Long id) {
        transactionService.deleteTransaction(id);
        return true;
    }

    @QueryMapping
    public BalanceDTO userBalance(@Argument Float days) {
        return groupBalanceService.calculateFinalBalance(days);
    }
}