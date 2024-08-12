package probono.gcc.school.model.dto.course;

import lombok.Data;
import probono.gcc.school.model.dto.ClassResponse;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.entity.Classes;

@Data
public class CourseResponse {

  private long courseId;
  private ClassResponse classResponse;
  private SubjectResponseDTO subjectResponseDTO;
}
