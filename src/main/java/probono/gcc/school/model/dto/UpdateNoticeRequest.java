package probono.gcc.school.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import probono.gcc.school.model.enums.NoticeType;

@Data
public class UpdateNoticeRequest {

  @NotNull
  private String title;

  @NotNull
  private String content;
}
