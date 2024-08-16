package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.enums.Status;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  Optional<Subject> findByName(String name);

  List<Subject> findAllByStatus(Status status);
}