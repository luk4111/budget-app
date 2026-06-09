package pk.zl.pasir_zajac_lukasz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.zl.pasir_zajac_lukasz.model.Group;
import pk.zl.pasir_zajac_lukasz.model.User;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMemberships_User(User user);
}