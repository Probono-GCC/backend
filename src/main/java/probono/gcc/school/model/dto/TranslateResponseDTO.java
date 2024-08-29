package probono.gcc.school.model.dto;

import lombok.Data;

@Data
public class TranslateResponseDTO {
  private String translatedText;
  private String status;
  private String message;

}
