package probono.gcc.school.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

  boolean existsByLoginId(String loginId);

  Optional<Users> findByLoginId(String loginId);



}
