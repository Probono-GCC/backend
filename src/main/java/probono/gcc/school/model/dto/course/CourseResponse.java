package probono.gcc.school.model.dto.course;

import lombok.Data;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.SubjectResponseDTO;

@Data
public class CourseResponse {

  private long courseId;
  private ClassResponse classResponse;
  private SubjectResponseDTO subjectResponseDTO;
}
