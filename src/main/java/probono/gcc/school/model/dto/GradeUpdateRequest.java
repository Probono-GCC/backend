package probono.gcc.school.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.enums.Grades;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GradeUpdateRequest {

  Grades grade;

}
