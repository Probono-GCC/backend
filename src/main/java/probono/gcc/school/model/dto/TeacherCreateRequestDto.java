package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class TeacherCreateRequestDto {

    private String name;
    private String loginId;
    private String loginPw;

}
