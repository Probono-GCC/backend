package probono.gcc.school.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.entity.Users;

@Component
@AllArgsConstructor
public class StudentMapper {
  private ModelMapper modelMapper;
  public StudentResponseDTO mapToResponseDTO(Users student) {
    // Create a new StudentResponseDTO instance
    StudentResponseDTO responseDto = new StudentResponseDTO();

    // Set fields directly from the student entity
    responseDto.setUsername(student.getUsername());
    responseDto.setName(student.getName());
    responseDto.setSerialNumber(student.getSerialNumber());
    responseDto.setGrade(student.getGrade());
    responseDto.setBirth(student.getBirth());
    responseDto.setSex(student.getSex());
    responseDto.setPhoneNum(student.getPhoneNum());
    responseDto.setFatherPhoneNum(student.getFatherPhoneNum());
    responseDto.setMotherPhoneNum(student.getMotherPhoneNum());
    responseDto.setGuardiansPhoneNum(student.getGuardiansPhoneNum());
    responseDto.setPwAnswer(student.getPwAnswer());
    responseDto.setRole(student.getRole());
    responseDto.setStatus(student.getStatus());
    responseDto.setCreatedAt(student.getCreatedAt());
    responseDto.setUpdatedAt(student.getUpdatedAt());
    responseDto.setCreatedChargeId(student.getCreatedChargeId());
    responseDto.setUpdatedChargeId(student.getUpdatedChargeId());

    // Map the class entity (Classes) to ClassResponse if the class is assigned
    if (student.getClassId() != null) {
      ClassResponse classResponse = modelMapper.map(student.getClassId(), ClassResponse.class);
      responseDto.setClassResponse(classResponse);
    }

    // Map the image entity (Image) to ImageResponseDTO if the image is assigned
    if (student.getImageId() != null) {
      CreateImageResponseDTO imageResponse = modelMapper.map(student.getImageId(), CreateImageResponseDTO.class);
      responseDto.setImageResponseDTO(imageResponse);
    }

    return responseDto;
  }

}
