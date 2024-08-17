package probono.gcc.school.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;
import probono.gcc.school.model.enums.Status;

@Repository
public interface ClassRepository extends JpaRepository<Classes, Long> {

  boolean existsByYearAndGradeAndSection(int year, Grades grade, Sections section);

  List<Classes> findByStatusAndYear(Status status, int year);

  Page<Classes> findByStatusAndYear(Status status, int year, Pageable pageable);
}
