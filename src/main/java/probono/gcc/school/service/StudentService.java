package probono.gcc.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.StudentCreateRequestDto;
import probono.gcc.school.model.dto.StudentResponseDto;
import probono.gcc.school.model.dto.StudentUpdateRequestDto;
import probono.gcc.school.model.entity.Student;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public StudentResponseDto createStudent(StudentCreateRequestDto studentCreateRequestDto){
        Student student = new Student();
        student.setLoginId(studentCreateRequestDto.getLogin_id());
        student.setLoginPw(studentCreateRequestDto.getLogin_pw());
        student.setName(studentCreateRequestDto.getName());
        student.setSerialNumber(studentCreateRequestDto.getSerial_number());
        student.setGrade(studentCreateRequestDto.getGrade());
        student.setStatus(Status.ACTIVE);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(null);
        student.setCreatedChargedId(-1L);
        student.setUpdatedChargedId(-1L);

        Student savedStudent = studentRepository.save(student);
        return mapToResponseDto(savedStudent);
    }

    public Optional<StudentResponseDto> getStudentById(Long id) {
        return studentRepository.findById(id).map(this::mapToResponseDto);
    }

    public StudentResponseDto updateStudent(StudentUpdateRequestDto studentUpdateRequestDto, Student existingStudent){

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
        existingStudent.setUpdatedAt(LocalDateTime.now());
        existingStudent.setUpdatedChargedId(-1L);

        // Save the updated student
        Student savedStudent = studentRepository.save(existingStudent);
        return mapToResponseDto(savedStudent);
    }

    public StudentResponseDto deleteStudent(Student existingStudent){

        // Update fields from request DT
        existingStudent.setStatus(Status.INACTIVE);
        existingStudent.setUpdatedAt(LocalDateTime.now());
        existingStudent.setUpdatedChargedId(-1L);

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
        responseDto.setStatus(student.getStatus());
        responseDto.setCreated_at(student.getCreatedAt());
        responseDto.setUpdated_at(student.getUpdatedAt());
        responseDto.setCreated_charged_id(student.getCreatedChargedId());
        responseDto.setUpdated_charged_id(student.getUpdatedChargedId());
        return responseDto;
    }
}
