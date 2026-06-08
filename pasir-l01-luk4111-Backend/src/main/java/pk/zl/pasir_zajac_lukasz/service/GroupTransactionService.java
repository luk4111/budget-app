package pk.zl.pasir_zajac_lukasz.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pk.zl.pasir_zajac_lukasz.config.GroupNotificationHandler;
import pk.zl.pasir_zajac_lukasz.dto.GroupTransactionDTO;
import pk.zl.pasir_zajac_lukasz.dto.GroupNotificationDTO;
import pk.zl.pasir_zajac_lukasz.model.Debt;
import pk.zl.pasir_zajac_lukasz.model.Group;
import pk.zl.pasir_zajac_lukasz.model.Membership;
import pk.zl.pasir_zajac_lukasz.model.User;
import pk.zl.pasir_zajac_lukasz.repository.DebtRepository;
import pk.zl.pasir_zajac_lukasz.repository.GroupRepository;
import pk.zl.pasir_zajac_lukasz.repository.MembershipRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupTransactionService {
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final MembershipService membershipService;
    private final GroupNotificationHandler notificationHandler;

    public GroupTransactionService(
            GroupRepository groupRepository,
            MembershipRepository membershipRepository,
            DebtRepository debtRepository,
            MembershipService membershipService,
            GroupNotificationHandler notificationHandler) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.membershipService = membershipService;
        this.notificationHandler = notificationHandler;
    }

    public void addGroupTransaction(GroupTransactionDTO transactionDTO, User currentUser) {
        Group group = groupRepository.findById(transactionDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono Grupy"));

        membershipService.assertCurrentUserIsGroupMember(group.getId());

        List<Membership> members = membershipRepository.findByGroupId(group.getId());
        List<Membership> selectedMembers = selectParticipants(transactionDTO, members, currentUser);

        if (selectedMembers.isEmpty()) {
            throw new IllegalStateException("Grupa nie ma członków, nie można dodać transakcji.");
        }

        double amountPerUser = transactionDTO.getAmount() / selectedMembers.size();
        boolean expense = "EXPENSE".equals(transactionDTO.getType());

        for (Membership member : selectedMembers) {
            User otherUser = member.getUser();

            if (!otherUser.getId().equals(currentUser.getId())) {
                Debt debt = new Debt();
                debt.setDebtor(expense ? otherUser : currentUser);
                debt.setCreditor(expense ? currentUser : otherUser);
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(transactionDTO.getTitle());
                debtRepository.save(debt);

                // Przygotowanie wiadomości tekstowej dla powiadomienia na frontendzie
                String msgText = String.format("%s dodał wydatek \"%s\" w grupie %s. Twoja część: %.2f zł.",
                        currentUser.getEmail(), transactionDTO.getTitle(), group.getName(), amountPerUser);

                // Utworzenie obiektu DTO dopasowanego strukturalnie do formatu frontendu
                GroupNotificationDTO notification = new GroupNotificationDTO(
                        "GROUP_EXPENSE_ADDED",
                        group.getId(),
                        group.getName(),
                        transactionDTO.getTitle(),
                        transactionDTO.getAmount(),
                        amountPerUser,
                        currentUser.getEmail(),
                        msgText
                );

                // Wysłanie wiadomości push w czasie rzeczywistym bezpośrednio do sesji dłużnika
                notificationHandler.sendNotificationToUser(otherUser.getEmail(), notification);
            }
        }
    }

    private List<Membership> selectParticipants(GroupTransactionDTO transactionDTO, List<Membership> members, User currentUser) {
        List<Long> selectedUserIds = transactionDTO.getSelectedUserIds();
        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            return members;
        }
        Set<Long> uniqueSelectedUserIds = new HashSet<>(selectedUserIds);
        List<Membership> selectedMembers = members.stream()
                .filter(membership -> uniqueSelectedUserIds.contains(membership.getUser().getId()))
                .toList();
        if (selectedMembers.size() != uniqueSelectedUserIds.size()) {
            throw new IllegalStateException("Wszyscy wybrani uzytkownicy musza byc członkami grupy.");
        }
        boolean currentUserSelected = selectedMembers.stream()
                .anyMatch(membership -> membership.getUser().getId().equals(currentUser.getId()));
        if (!currentUserSelected) {
            throw new IllegalStateException("Aktualny uzytkownik musi byc uczestnikiem transakcji grupowej.");
        }
        if (selectedMembers.size() < 2) {
            throw new IllegalStateException("Transakcja grupowa wymaga co najmniej dwoch uczestnikow.");
        }
        return selectedMembers;
    }
}