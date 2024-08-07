package probono.gcc.school.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

  public ImageResponseDTO createProfileImage(ImageRequestDTO requestDto) {
//    Notice notice = noticeRepository.findById(requestDto.getNoticeId())
//        .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));

    Image image = new Image();
    image.setStatus(Status.ACTIVE);
    image.setCreatedChargeId(1L);
    image.setImagePath(requestDto.getImagePath());

    Image savedImage = imageRepository.save(image);
//    logger.info("Image createdAt from savedImage: {}", savedImage.getCreatedAt());
//    imageRepository.flush(); // 데이터베이스에 즉시 반영

//    // 데이터베이스에서 최신 createdAt 값을 재조회
//    Image reloadedImage = imageRepository.findById(savedImage.getImageId())
//        .orElseThrow(() -> new RuntimeException("Image not found"));
//
//    // Log the createdAt value
//    logger.info("Image createdAt from database: {}", reloadedImage.getCreatedAt());
    return modelMapper.map(savedImage, ImageResponseDTO.class);
  }


  public List<ImageResponseDTO> findAllProfileImages() {
    try {
      List<Image> imageList = imageRepository.findAll();
      // stream과 mapper를 사용하여 리스트 변환
      return imageList.stream()
          .map(image -> modelMapper.map(image, ImageResponseDTO.class))
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Exception handling
      throw new RuntimeException("An error occurred while fetching images", e);
    }
  }


  public ImageResponseDTO findOneImage(Long id) {
    Image image = imageRepository.findById(id).orElseThrow(
        () -> new IllegalArgumentException("Fail to find image with ID: " + id)
    );
    return modelMapper.map(image, ImageResponseDTO.class);
  }

  public Long deleteProfileImage(Long id) {
    Image image = imageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid id"));
    // 논리적 삭제 수행
    image.setStatus(Status.INACTIVE);
    // Dummy Data
    image.setUpdatedChargeId(2L);

    // 엔티티를 저장하여 변경 사항을 데이터베이스에 반영
    imageRepository.save(image);

    return image.getImageId();
  }

  public Image findById(Long id) {
    Image image = imageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid id"));

    return image;
  }

  //  public ImageResponseDTO createPostImage(ImageRequestDTO requestDto) {
//    //notice crud 작업한 뒤 수정
//    //    Notice notice = noticeRepository.findById(requestDto.getNoticeId())
//    //        .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));
//
//    Image image = new Image();
//    image.setStatus(Status.ACTIVE);
//    image.setCreatedChargeId(1L);
//    image.setImagePath(requestDto.getImagePath());
//
//    Image savedImage = imageRepository.save(image);
//
//    return modelMapper.map(savedImage, ImageResponseDTO.class);
//  }



}