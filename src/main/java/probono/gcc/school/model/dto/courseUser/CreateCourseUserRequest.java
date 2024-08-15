package probono.gcc.school.model.dto.courseUser;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourseUserRequest {

  @NotNull
  private long courseId;

  @NotNull
  private String username;
}
