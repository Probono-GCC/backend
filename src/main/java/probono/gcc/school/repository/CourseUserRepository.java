package probono.gcc.school.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.CourseUser;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.entity.Users;

@Repository
public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {

  boolean existsByCourseIdAndUsername(Course course, Users user);

  List<CourseUser> findByCourseId(Course course);
}
