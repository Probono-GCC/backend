package probono.gcc.school.model.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.SubjectResponseDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

  private long courseId;
  private ClassResponse classResponse;
  private SubjectResponseDTO subjectResponseDTO;
}
