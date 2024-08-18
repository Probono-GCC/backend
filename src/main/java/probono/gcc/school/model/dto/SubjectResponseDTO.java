package probono.gcc.school.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SubjectResponseDTO {

  private Long subjectId;

  private String name;

  private boolean isElective;
}
