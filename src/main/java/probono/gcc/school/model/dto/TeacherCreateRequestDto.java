package probono.gcc.school.model.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TeacherCreateRequestDto {

    private String name;
    private String loginId;
    private String loginPw;

}
