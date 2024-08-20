package probono.gcc.school.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.image.ImageResponseDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Users;
@Component
@AllArgsConstructor
public class TeacherMapper {
  private ModelMapper modelMapper;
  public TeacherResponseDTO mapToResponseDTO (Users savedTeacher){
    // Create a new TeacherResponseDTO instance
    TeacherResponseDTO responseDto = new TeacherResponseDTO();

    // Set fields directly from the savedTeacher entity
    responseDto.setUsername(savedTeacher.getUsername());
    responseDto.setRole(savedTeacher.getRole());
    responseDto.setName(savedTeacher.getName());
    responseDto.setBirth(savedTeacher.getBirth());
    responseDto.setSex(savedTeacher.getSex());
    responseDto.setPhoneNum(savedTeacher.getPhoneNum());
    responseDto.setPwAnswer(savedTeacher.getPwAnswer());
    responseDto.setStatus(savedTeacher.getStatus());
//    responseDto.setCreatedAt(savedTeacher.getCreatedAt());
//    responseDto.setUpdatedAt(savedTeacher.getUpdatedAt());
//    responseDto.setCreatedChargeId(savedTeacher.getCreatedChargeId());
//    responseDto.setUpdatedChargeId(savedTeacher.getUpdatedChargeId());

    // Map the class entity (Classes) to ClassResponse if the class is assigned
    if (savedTeacher.getClassId() != null) {
      ClassResponse classResponse = modelMapper.map(savedTeacher.getClassId(),
          ClassResponse.class);
      responseDto.setClassId(classResponse);
    }

    // Map the image entity (Image) to ImageResponseDTO if the image is assigned
    if (savedTeacher.getImageId() != null) {
    ImageResponseDTO imageResponse = modelMapper.map(savedTeacher.getImageId(),
          ImageResponseDTO.class);
      responseDto.setImageId(imageResponse);
    }

    return responseDto;
  }

}
