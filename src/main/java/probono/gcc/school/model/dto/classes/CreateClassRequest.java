package probono.gcc.school.model.dto.classes;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;

@Data
public class CreateClassRequest {

  @NotNull
  private int year;
  @NotNull
  private Grades grade;
  @NotNull
  private Sections section;
}
