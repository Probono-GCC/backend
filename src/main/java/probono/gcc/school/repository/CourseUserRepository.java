package probono.gcc.school.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.CourseUser;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;

import probono.gcc.school.model.enums.Status;


@Repository
public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {


  List<CourseUser> findByCourseId(Course course);



  List<CourseUser> findByUsernameAndRole(Users username, Role role);

  Page<CourseUser> findByStatusAndRoleAndCourseId(Status status, Role role, Course course,
      Pageable pageable);

  List<CourseUser> findByCourseIdAndRoleAndStatus(Course findCourse, Role role, Status status);

  boolean existsByCourseIdAndUsernameAndStatus(Course findCourse, Users findUser, Status status);
}
