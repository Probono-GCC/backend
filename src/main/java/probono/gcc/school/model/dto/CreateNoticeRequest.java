package probono.gcc.school.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Sections;

@Data
public class CreateNoticeRequest {

  @NotNull
  private String title;

  private String content;
  @NotNull
  private NoticeType type;

  private Long classId;

  private Long courseId;

  private List<MultipartFile> imageList;
}
