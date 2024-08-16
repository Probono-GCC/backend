package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;

public interface UserRepository extends JpaRepository<Users, Long> {

  boolean existsByLoginId(String loginId);

  Optional<Users> findByLoginId(String loginId);

  List<Users> findByStatus(Status status);

  Optional<Users> findByLoginIdAndStatus(String loginId,Status status);

  List<Users> findByStatusAndRole(Status status, Role role);

  List<Users> findByClassIdAndRoleAndStatus(Classes classId, Role role,Status status);
}
