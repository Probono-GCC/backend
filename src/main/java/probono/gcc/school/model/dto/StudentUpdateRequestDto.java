package probono.gcc.school.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentUpdateRequestDto {
    @NotNull
    private String login_id;

    @NotNull
    private String name;

    @NotNull
    private Integer serial_number;

    @NotNull
    private Grades grade;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private Sex sex;

    private String phone_num;

    private String father_phone_num;

    private String mother_phone_num;

    private String guardians_phone_num;
}
