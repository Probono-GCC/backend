package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TeacherRequestDto {
    private Long id;
    private String login_id;
    private String name;
    private String login_pw;

}
