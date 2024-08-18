package probono.gcc.school.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  @PersistenceContext
  private EntityManager entityManager;

  private static final Logger logger = LoggerFactory.getLogger(AssignClassService.class);
  @Transactional
  public AssignClassResponseDTO assignUser(Long classId, String username) {

    Users user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchElementException("Teacher not found with ID: " + username));

    Classes assignedClass = classRepository.findById(classId)
        .orElseThrow(() -> new NoSuchElementException("Class not found with ID: " + classId));

    //이미 할당했는지 예외처리

    // Initialize associated notices (if needed for any reason)
    Hibernate.initialize(assignedClass.getNotice());

    user.addClass(assignedClass);
    entityManager.flush(); // 트랜잭션 내에서 영속성 컨텍스트 동기화
    logger.info("assignedClass.getUsers().size() : {}",assignedClass.getUsers().size());

    Users updatedUser = userRepository.save(user);
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