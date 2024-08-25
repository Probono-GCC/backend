package probono.gcc.school.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import probono.gcc.school.model.enums.NoticeType;

@Data
public class UpdateNoticeRequest {

  @NotNull
  private String title;

  @NotNull
  private String content;

  private List<Long> maintainImageList;

  private List<MultipartFile> imageList = new ArrayList<>();
}
