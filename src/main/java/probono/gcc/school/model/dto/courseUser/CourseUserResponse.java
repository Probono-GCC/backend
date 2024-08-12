package probono.gcc.school.model.dto.courseUser;

import lombok.Data;
import probono.gcc.school.model.dto.StudentDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.users.StudentResponse;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Users;

@Data
public class CourseUserResponse {

  private long courseUserId;
  private StudentResponse student;
  private CourseResponse course;
}
