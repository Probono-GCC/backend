package probono.gcc.school.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateResponseDTO implements Serializable {
  private String translatedText;
  private Boolean status;
  private String message;
}
