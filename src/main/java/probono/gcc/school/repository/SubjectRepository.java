package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.enums.Status;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

  Optional<Subject> findByName(String name);

  Page<Subject> findAllByStatus(Status status, Pageable pageable);

  List<Subject> findAllByStatusAndIsElective(Status status, boolean isElective);
}