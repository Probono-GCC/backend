package probono.gcc.school.model.dto.courseUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.UserResponse;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CourseUserResponse {

  private long courseUserId;
  private UserResponse userResponse;
  private CourseResponse course;
}
