package probono.gcc.school.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.ImageRequestDTO;
import probono.gcc.school.model.dto.ImageResponseDTO;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ImageRepository;
import probono.gcc.school.repository.SubjectRepository;

@Service
@AllArgsConstructor
public class ImageService {
  private ModelMapper modelMapper;
  private ImageRepository imageRepository;

  public ImageResponseDTO createProfileImage(ImageRequestDTO requestDto) {
//    Notice notice = noticeRepository.findById(requestDto.getNoticeId())
//        .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));

    Image image = new Image();
    image.setStatus(Status.ACTIVE);
    image.setCreatedChargeId(1L);
    image.setImagePath(requestDto.getImagePath());


    Image savedImage = imageRepository.save(image);

    return modelMapper.map(savedImage, ImageResponseDTO.class);
  }

  public ImageResponseDTO createPostImage(ImageRequestDTO requestDto) {
    //notice crud 작업한 뒤 수정
    //    Notice notice = noticeRepository.findById(requestDto.getNoticeId())
    //        .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));

    Image image = new Image();
    image.setStatus(Status.ACTIVE);
    image.setCreatedChargeId(1L);
    image.setImagePath(requestDto.getImagePath());

    Image savedImage = imageRepository.save(image);

    return modelMapper.map(savedImage, ImageResponseDTO.class);
  }


}
