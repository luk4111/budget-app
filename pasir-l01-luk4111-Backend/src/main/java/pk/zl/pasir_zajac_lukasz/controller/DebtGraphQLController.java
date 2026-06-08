package pk.zl.pasir_zajac_lukasz.controller;

import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.zl.pasir_zajac_lukasz.dto.DebtDTO;
import pk.zl.pasir_zajac_lukasz.model.Debt;
import pk.zl.pasir_zajac_lukasz.service.DebtService;

import java.util.List;

@Controller
public class DebtGraphQLController {

    private final DebtService debtService;

    public DebtGraphQLController(DebtService debtService) {
        this.debtService = debtService;
    }

    @QueryMapping
    public List<Debt> groupDebts(@Argument Long groupId) {
        return debtService.getGroupDebts(groupId);
    }

    @MutationMapping
    public Debt createDebt(@Valid @Argument DebtDTO debtDTO) {
        return debtService.createDebt(debtDTO);
    }

    @MutationMapping
    public Boolean deleteDebt(@Argument Long debtId) {
        debtService.deleteDebt(debtId);
        return true;
    }
    @org.springframework.graphql.data.method.annotation.MutationMapping
    public Debt markDebtAsPaid(@org.springframework.graphql.data.method.annotation.Argument Long debtId) {
        return debtService.markDebtAsPaid(debtId);
    }

    @org.springframework.graphql.data.method.annotation.MutationMapping
    public Debt confirmDebtPayment(@org.springframework.graphql.data.method.annotation.Argument Long debtId) {
        return debtService.confirmDebtPayment(debtId);
    }

}