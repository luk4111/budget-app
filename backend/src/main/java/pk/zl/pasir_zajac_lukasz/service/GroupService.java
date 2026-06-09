package pk.zl.pasir_zajac_lukasz.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pk.zl.pasir_zajac_lukasz.dto.GroupDTO;
import pk.zl.pasir_zajac_lukasz.model.Group;
import pk.zl.pasir_zajac_lukasz.model.Membership;
import pk.zl.pasir_zajac_lukasz.model.User;
import pk.zl.pasir_zajac_lukasz.repository.DebtRepository;
import pk.zl.pasir_zajac_lukasz.repository.GroupRepository;
import pk.zl.pasir_zajac_lukasz.repository.MembershipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public Group createGroup(GroupDTO groupDTO) {
        User currentUser = currentUserService.getCurrentUser();

        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setOwner(currentUser);

        Group savedGroup = groupRepository.save(group);

        Membership membership = new Membership();
        membership.setGroup(savedGroup);
        membership.setUser(currentUser);
        membershipRepository.save(membership);

        return savedGroup;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<Group> getMyGroups() {
        User currentUser = currentUserService.getCurrentUser();
        return groupRepository.findByMemberships_User(currentUser);
    }

    @Transactional
    public boolean deleteGroup(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + id));

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Tylko właściciel może usunąć grupę");
        }

        debtRepository.deleteByGroupId(id);
        membershipRepository.deleteByGroupId(id);
        groupRepository.delete(group);
        return true;
    }
}