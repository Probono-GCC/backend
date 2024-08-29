package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.enums.Status;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  boolean existsByClassIdAndSubjectId(Classes findClass, Subject findSubject);

  List<Course> findByClassId(Classes findclass);

  List<Course> findBySubjectId(Subject subject);

  List<Course> findBySubjectIdAndStatus(Subject subject, Status status);

  Page<Course> findByStatus(Status status, Pageable pageable);

  Page<Course> findByStatusAndClassId(Status status, Classes classes, Pageable pageable);

  Optional<Object> findByIdAndStatus(Long courseId,Status status);
}
