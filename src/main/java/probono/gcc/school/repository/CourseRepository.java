package probono.gcc.school.repository;

import java.util.List;
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

  List<Course> findByStatus(Status status);
}
