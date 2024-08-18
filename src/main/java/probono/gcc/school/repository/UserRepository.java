package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;

public interface UserRepository extends JpaRepository<Users, Long> {

  boolean existsByUsername(String username);

  Optional<Users> findByUsername(String username);

  List<Users> findByStatus(Status status);

  Optional<Users> findByUsernameAndStatus(String username, Status status);

  Page<Users> findByStatusAndRole(Status status, Role role, Pageable pageable);


  List<Users> findByClassIdAndRoleAndStatus(Classes classId, Role role,Status status);

  boolean existsBySerialNumber(int serialNumber);

}
