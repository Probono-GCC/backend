package probono.gcc.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.StudentResponseDto;
import probono.gcc.school.model.dto.StudentUpdateRequestDto;
import probono.gcc.school.model.entity.Logs;
import probono.gcc.school.model.entity.Student;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentService {

  @Autowired
  private StudentRepository studentRepository;

  public StudentResponseDto createStudent(Student student) {
    validateDuplicateStudentLoginId(student); //아이디 중복 검증
    validateDuplicateStudentSN(student); //아이디 중복 검증

    Student savedStudent = studentRepository.save(student);
    return mapToResponseDto(savedStudent);
  }

  private void validateDuplicateStudentLoginId(Student student) {
    if (studentRepository.existsByLoginId(student.getLoginId())) {
      throw new IllegalStateException("이미 존재하는 아이디입니다.");
    }
  }

  private void validateDuplicateStudentSN(Student student) {
    if (studentRepository.existsBySerialNumber(student.getSerialNumber())) {
      throw new IllegalStateException("이미 존재하는 serial number 입니다.");
    }
  }

  public Optional<StudentResponseDto> getStudentById(Long id) {
    return studentRepository.findById(id).map(this::mapToResponseDto);
  }

  public StudentResponseDto updateStudent(StudentUpdateRequestDto studentUpdateRequestDto,
      Student existingStudent) {
    Logs logs = new Logs(Status.ACTIVE, LocalDateTime.now(), -1L);

    // Update fields from request DTO
    existingStudent.setName(studentUpdateRequestDto.getName());
    existingStudent.setSerialNumber((studentUpdateRequestDto.getSerial_number()));
    existingStudent.setGrade(studentUpdateRequestDto.getGrade());
    existingStudent.setBirth(studentUpdateRequestDto.getBirth());
    existingStudent.setSex(studentUpdateRequestDto.getSex());
    existingStudent.setPhoneNum(studentUpdateRequestDto.getPhone_num());
    existingStudent.setFatherPhoneNum(studentUpdateRequestDto.getFather_phone_num());
    existingStudent.setMotherPhoneNum(studentUpdateRequestDto.getMother_phone_num());
    existingStudent.setGuardiansPhoneNum(studentUpdateRequestDto.getGuardians_phone_num());
    existingStudent.setLogs(logs);

    // Save the updated student
    Student savedStudent = studentRepository.save(existingStudent);
    return mapToResponseDto(savedStudent);
  }

  public StudentResponseDto deleteStudent(Student existingStudent) {

    // Update fields from request DT
    Logs logs = new Logs(Status.ACTIVE, LocalDateTime.now(), -1L);
    existingStudent.setLogs(logs);

    // Save the updated student
    Student savedStudent = studentRepository.save(existingStudent);
    return mapToResponseDto(savedStudent);
  }


  private StudentResponseDto mapToResponseDto(Student student) {
    StudentResponseDto responseDto = new StudentResponseDto();
    responseDto.setId(student.getId());
    responseDto.setLogin_id(student.getLoginId());
    responseDto.setName(student.getName());
    responseDto.setSerial_number(student.getSerialNumber());
    responseDto.setGrade(student.getGrade());
    responseDto.setBirth(student.getBirth());
    responseDto.setSex(student.getSex());
    responseDto.setPhone_num(student.getPhoneNum());
    responseDto.setFather_phone_num(student.getFatherPhoneNum());
    responseDto.setMother_phone_num(student.getMotherPhoneNum());
    responseDto.setGuardians_phone_num(student.getGuardiansPhoneNum());

    return responseDto;
  }
}
