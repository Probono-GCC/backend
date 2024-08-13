package probono.gcc.school.model.dto.courseUser;

import lombok.Data;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.users.UserResponse;

@Data
public class CourseUserResponse {

  private long courseUserId;
  private UserResponse user;
  private CourseResponse course;
}
