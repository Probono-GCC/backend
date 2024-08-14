package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Status;

public interface UserRepository extends JpaRepository<Users, Long> {

  boolean existsByLoginId(String loginId);

  Optional<Users> findByLoginId(String loginId);

  List<Users> findByStatus(Status status);

  Optional<Users> findByLoginIdAndStatus(String loginId,Status status);
}
