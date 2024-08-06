package probono.gcc.school.model.dto;

import jakarta.persistence.Column;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubjectRequestDTO {

    private String name;

    private boolean isElective;

}
