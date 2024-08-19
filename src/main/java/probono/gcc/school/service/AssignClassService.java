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
import probono.gcc.school.mapper.StudentMapper;
import probono.gcc.school.mapper.TeacherMapper;
import probono.gcc.school.model.dto.classes.AssignClassResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AssignClassService {

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final UserRepository userRepository;
  private final ClassRepository classRepository;
  private final StudentMapper studentMapper;
  private final TeacherMapper teacherMapper;
  @PersistenceContext
  private EntityManager entityManager;

  private static final Logger logger = LoggerFactory.getLogger(AssignClassService.class);
  @Transactional
  public AssignClassResponseDTO assignUser(Long classId, String username) {

    Users user = userRepository.findByUsernameAndStatus(username,Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + username));

    Classes assignedClass = classRepository.findByClassIdAndStatus(classId,Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("Class not found with ID: " + classId));

    //이미 할당했는지 예외처리

    // Initialize associated notices (if needed for any reason)
    Hibernate.initialize(assignedClass.getNotice());

    user.addClass(assignedClass);
    entityManager.flush(); // 트랜잭션 내에서 영속성 컨텍스트 동기화
    logger.info("assignedClass.getUsers().size() : {}",assignedClass.getUsers().size());

    Users updatedUser = userRepository.save(user);
    return mapToAssignResponseDTO(assignedClass,updatedUser);
  }

  @Transactional
  public Object deleteAssignedUser(Long classId, String username) {

    Classes findClass = classRepository.findByClassIdAndStatus(classId,Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("Class not found with ID: " + classId));

    Users user=userRepository.findByUsernameAndClassIdAndStatus(username,findClass, Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("User not found with username and classId : " ));

    user.deleteClass(findClass);

    if(user.getRole()==Role.ROLE_ADMIN){
      return new IllegalStateException("Admin은 할당된 class가 없어 삭제할 수 없습니다.");
    }

    if(user.getRole()==Role.ROLE_STUDENT) {
      return studentMapper.mapToResponseDTO(user);
    }
    else{
      return teacherMapper.mapToResponseDTO(user);
    }
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