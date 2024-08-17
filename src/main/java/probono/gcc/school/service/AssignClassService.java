package probono.gcc.school.service;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.dto.classes.AssignClassResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AssignClassService {

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final UserRepository userRepository;
  private final ClassRepository classRepository;
  @Transactional
  public AssignClassResponseDTO assignUser(Long classId, String username) {
    // Find the user by loginId
    Users user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchElementException("Teacher not found with ID: " + username));

    // Find the class by classId
    Classes assignedClass = classRepository.findById(classId)
        .orElseThrow(() -> new NoSuchElementException("Class not found with ID: " + classId));

    // Initialize associated notices (if needed for any reason)
    Hibernate.initialize(assignedClass.getNotice());

    // Assign the class to the user
    user.addClass(assignedClass);

    // Save the updated teacher entity
    Users updatedUser = userRepository.save(user);

    // Create the response DTO
    AssignClassResponseDTO assignedUserDTO = mapToAssignResponseDTO(assignedClass,updatedUser);

    return assignedUserDTO;
  }
  @Transactional
  public AssignClassResponseDTO mapToAssignResponseDTO(Classes classes,Users user) {
    AssignClassResponseDTO assignClassResponseDTO = new AssignClassResponseDTO();
    assignClassResponseDTO.setClassId(classes.getClassId());
    assignClassResponseDTO.setYear(classes.getYear());
    assignClassResponseDTO.setGrade(classes.getGrade());
    assignClassResponseDTO.setSection(classes.getSection());
    if(user.getRole()== Role.ROLE_TEACHER)
      assignClassResponseDTO.setTeacher(teacherService.mapToResponseDTO(user));
    else if(user.getRole()==Role.ROLE_STUDENT)
      assignClassResponseDTO.setStudent(studentService.mapToResponseDTO(user));

    return assignClassResponseDTO;


  }



}