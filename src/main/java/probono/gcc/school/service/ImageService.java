package probono.gcc.school.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.image.ImageRequestDTO;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ImageRepository;
import probono.gcc.school.repository.NoticeRepository;
import probono.gcc.school.repository.UserRepository;

@Service
@AllArgsConstructor
public class ImageService {

  private ModelMapper modelMapper;
  private ImageRepository imageRepository;

  private S3ImageService s3ImageService;

  private NoticeRepository noticeRepository;

  private UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

  public CreateImageResponseDTO createProfileImage(ImageRequestDTO requestDto) {

    Image image = new Image();
    image.setStatus(Status.ACTIVE);
    image.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());
    image.setImagePath(requestDto.getImagePath());

    imageRepository.save(image);

    String username = requestDto.getUsername();
    Optional<Users> usersOptional = userRepository.findByUsername(username);
    Users user = null;
    if (usersOptional.isPresent()) {
      user = usersOptional.get();
      user.setImageId(image);
    }

    return mapToCreateResponseDTO(image, username);
  }

  public CreateImageResponseDTO mapToCreateResponseDTO(Image image, String username) {

    CreateImageResponseDTO createImageResponseDTO = new CreateImageResponseDTO();
    createImageResponseDTO.setImageId(image.getImageId());
    createImageResponseDTO.setImagePath(image.getImagePath());
    createImageResponseDTO.setCreatedChargeId(image.getCreatedChargeId());
    createImageResponseDTO.setUsername(username);

    return createImageResponseDTO;

  }

//  public ImageResponseDTO saveNoticeImage(String imagePath, Long noticeId) {
//
//    Image image = new Image();
//    image.setImagePath(imagePath);
//    image.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());
//
//    Optional<Notice> findNotice = noticeRepository.findById(noticeId);
//    if (findNotice.isEmpty()) {
//      throw new IllegalArgumentException("NoticeId가 올바르지 않습니다.");
//    }
//    image.setNoticeId(findNotice.get());
//
//    Image savedImage = imageRepository.save(image);
//
//    return modelMapper.map(savedImage, ImageResponseDTO.class);
//  }

  public Image saveNoticeImage(String imagePath, Notice notice) {

    Image image = new Image();
    image.setImagePath(imagePath);
    image.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());

    System.out.print("notice id is setting");
    image.setNoticeId(notice);

    Image savedImage = imageRepository.save(image);

    return savedImage;
  }


  public List<CreateImageResponseDTO> findAllProfileImages() {
    try {
      List<Image> imageList = imageRepository.findAll();
      // stream과 mapper를 사용하여 리스트 변환
      return imageList.stream()
          .map(image -> modelMapper.map(image, CreateImageResponseDTO.class))
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Exception handling
      throw new RuntimeException("An error occurred while fetching images", e);
    }
  }


  public CreateImageResponseDTO findOneImage(Long id) {
    Image image = imageRepository.findById(id).orElseThrow(
        () -> new IllegalArgumentException("Fail to find image with ID: " + id)
    );
    return modelMapper.map(image, CreateImageResponseDTO.class);
  }

  public void deleteProfileImage(Long id) {
    Image image = imageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid id"));

    //s3에서 이미지 삭제 수행
    s3ImageService.deleteImageFromS3(image.getImagePath());

//    // 논리적 삭제 수행
//    image.setStatus(Status.INACTIVE);
//    // Dummy Data
//    image.setUpdatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());
//
//    // 엔티티를 저장하여 변경 사항을 데이터베이스에 반영
//    imageRepository.save(image);
    imageRepository.deleteById(id);

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
//    image.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());
//    image.setImagePath(requestDto.getImagePath());
//
//    Image savedImage = imageRepository.save(image);
//
//    return modelMapper.map(savedImage, ImageResponseDTO.class);
//  }

  public CreateImageResponseDTO mapToResponseDTO(Image image) {

    return CreateImageResponseDTO.builder()
        .imageId(image.getImageId())
        .imagePath(image.getImagePath())
        .createdChargeId(image.getCreatedChargeId())
        .build();
  }


}