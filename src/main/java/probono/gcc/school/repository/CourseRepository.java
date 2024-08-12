package probono.gcc.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Subject;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  boolean existsByClassIdAndSubjectId(Classes findClass, Subject findSubject);

}
