package probono.gcc.school.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  Optional<Subject> findByName(String name);

}