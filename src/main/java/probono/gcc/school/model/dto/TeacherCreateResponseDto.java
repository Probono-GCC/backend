package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TeacherCreateResponseDto {
    private Long id;
    private String loginId;
    private String name;
    private String loginPw;

    //private String re_type_pw;
}
