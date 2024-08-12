package probono.gcc.school.model.dto.courseUser;

import lombok.Data;
import probono.gcc.school.model.dto.StudentDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Users;

@Data
public class CourseUserResponse {

  private long courseUserId;
  private StudentDTO user; // 학생인지 유저인지 확인필요
  /***
   * 학생인지 유저인지 확인필요
   */
  private CourseResponse course;
}
