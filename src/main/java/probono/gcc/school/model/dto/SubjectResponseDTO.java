package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SubjectResponseDTO {

  private Long subjectId;

  private String name;

  private boolean isElective;
}
