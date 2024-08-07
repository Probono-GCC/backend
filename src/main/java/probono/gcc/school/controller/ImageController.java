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
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.ImageRequestDTO;
import probono.gcc.school.model.dto.ImageResponseDTO;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.service.ImageService;

@RestController
@AllArgsConstructor
public class ImageController {
  private final ImageService imageService;
  private ModelMapper modelMapper;
  //이미지 생성
  @PostMapping("/profile/images")
  public ResponseEntity<ImageResponseDTO> createProfileImage(
      @RequestBody ImageRequestDTO requestDto) {
    ImageResponseDTO image = imageService.createProfileImage(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(image);

  }

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
  public ResponseEntity<ImageResponseDTO> deleteProfileImage(@PathVariable Long id) {
    try {
      // 서비스에서 삭제 수행
      Long deletedImageId = imageService.deleteProfileImage(id);
      // 삭제된 Image를 조회 시, 존재하지 않을 경우 예외 처리
      Image deletedImage = imageService.findById(deletedImageId);
      // Image 엔티티를 DTO로 변환
      ImageResponseDTO responseDto = modelMapper.map(deletedImage, ImageResponseDTO.class);
      // 성공적인 삭제 후 응답 반환
      return ResponseEntity.ok(responseDto);
    } catch (IllegalArgumentException ex) {
      // 존재하지 않는 ID로 인한 예외 처리
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception ex) {
      // 다른 예외 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

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