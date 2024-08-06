package probono.gcc.school.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import probono.gcc.school.model.enums.Grades;

@Data
public class StudentCreateRequestDto {
    @NotNull
    private String login_id;

    @NotNull
    private String login_pw;

    @NotNull
    private String name;

    @NotNull
    private Integer serial_number;

    @NotNull
    private Grades grade;
}
