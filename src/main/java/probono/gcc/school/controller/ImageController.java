package probono.gcc.school.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import probono.gcc.school.exception.S3Exception;
import probono.gcc.school.model.dto.ImageRequestDTO;
import probono.gcc.school.model.dto.ImageResponseDTO;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.service.ImageService;
import probono.gcc.school.service.S3ImageService;

@RestController
@AllArgsConstructor
public class ImageController {

  private final ImageService imageService;
  private ModelMapper modelMapper;

  private S3ImageService s3ImageService;

  @PostMapping("/profile/images")
  public ResponseEntity<?> createProfileImage(@RequestPart("image") MultipartFile image) {
    try {
      String profileImageUrl = s3ImageService.upload(image);
      ImageRequestDTO requestDto = new ImageRequestDTO();
      requestDto.setImagePath(profileImageUrl);
      // set other fields of requestDto as necessary

      ImageResponseDTO imageResponse = imageService.createProfileImage(requestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(imageResponse);
    } catch (S3Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to upload image: " + e.getMessage());
    }
  }

  @PostMapping("/notice/images")
  public ResponseEntity<ImageResponseDTO> saveNoticeImage(
      @RequestPart("image") MultipartFile image, Long noticeId) {
    String imagePath = s3ImageService.upload(image);

//    ImageRequestDTO requestDto = new ImageRequestDTO();
//    requestDto.setImagePath(profileImageUrl);
    // set other fields of requestDto as necessary

    ImageResponseDTO imageResponse = imageService.saveNoticeImage(imagePath, noticeId);
    return ResponseEntity.status(HttpStatus.CREATED).body(imageResponse);
  }

  //이미지 생성
//  @PostMapping("/profile/images")
//  public ResponseEntity<ImageResponseDTO> createProfileImage(
//      @RequestBody ImageRequestDTO requestDto) {
//    ImageResponseDTO image = imageService.createProfileImage(requestDto);
//    return ResponseEntity.status(HttpStatus.CREATED).body(image);
//
//  }

  //프로필 이미지 목록 조회
  @GetMapping("/profile/images")
  public ResponseEntity<List<ImageResponseDTO>> getAllProfileImages() {
    List<ImageResponseDTO> images = imageService.findAllProfileImages();
    return ResponseEntity.ok(images);
  }


  // 이미지 한 장 조회
  @GetMapping("profile/images/{id}")
  public ResponseEntity<ImageResponseDTO> getOneProfileImage(@PathVariable Long id) {
    ImageResponseDTO image = imageService.findOneImage(id);
    return ResponseEntity.ok(image);
  }

  // 이미지 삭제
  @DeleteMapping("profile/images/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Image deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "Image not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })

  public ResponseEntity<?> deleteProfileImage(@PathVariable Long id) {
    try {
      // 삭제 시 이미지 정보 조회 및 삭제
      Long deletedImageId = imageService.deleteProfileImage(id);
      Image deletedImage = imageService.findById(deletedImageId);

      // 이미지 삭제 후 S3에서 삭제
      s3ImageService.deleteImageFromS3(deletedImage.getImagePath());

      // Image 엔티티를 DTO로 변환하여 응답 반환
      ImageResponseDTO responseDto = modelMapper.map(deletedImage, ImageResponseDTO.class);
      return ResponseEntity.ok(responseDto);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
    } catch (S3Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to delete image from S3: " + ex.getMessage());
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image");
    }
  }

  //notice crud 작업한 뒤 수정할 것
//  @PostMapping("/post/images")
//  public ResponseEntity<ImageResponseDTO> createPostImage(
//      @RequestBody ImageRequestDTO requestDto) {
//
//    ImageResponseDTO image = imageService.createProfileImage(requestDto);
//    return ResponseEntity.status(HttpStatus.CREATED).body(image);
//
//  }


}