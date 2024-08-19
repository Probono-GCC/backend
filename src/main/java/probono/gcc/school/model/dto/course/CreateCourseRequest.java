package probono.gcc.school.model.dto.course;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourseRequest {

  @NotNull
  private long subjectId;

  private Long classId;

}
