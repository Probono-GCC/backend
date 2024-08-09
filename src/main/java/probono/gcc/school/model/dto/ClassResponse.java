package probono.gcc.school.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;

//@Setter
//@Getter
//@AllArgsConstructor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {

  private Long classId;
  private int year;

  private Grades grade;

  private Sections section;
}
