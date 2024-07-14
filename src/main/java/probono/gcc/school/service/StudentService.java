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
        student.setLogin_id(studentCreateRequestDto.getLogin_id());
        student.setLogin_pw(studentCreateRequestDto.getLogin_pw());
        student.setName(studentCreateRequestDto.getName());
        student.setSerial_number(studentCreateRequestDto.getSerial_number());
        student.setGrade(studentCreateRequestDto.getGrade());
        student.setStatus(Status.ACTIVE);
        student.setCreated_at(LocalDateTime.now());
        student.setUpdated_at(null);
        student.setCreated_charged_id(-1L);
        student.setUpdated_charged_id(-1L);

        Student savedStudent = studentRepository.save(student);
        return mapToResponseDto(savedStudent);
    }

    public Optional<StudentResponseDto> getStudentById(Long id) {
        return studentRepository.findById(id).map(this::mapToResponseDto);
    }

    public StudentResponseDto updateStudent(StudentUpdateRequestDto studentUpdateRequestDto, Student existingStudent){

        // Update fields from request DTO
        existingStudent.setName(studentUpdateRequestDto.getName());
        existingStudent.setSerial_number((studentUpdateRequestDto.getSerial_number()));
        existingStudent.setGrade(studentUpdateRequestDto.getGrade());
        existingStudent.setBirth(studentUpdateRequestDto.getBirth());
        existingStudent.setSex(studentUpdateRequestDto.getSex());
        existingStudent.setPhone_num(studentUpdateRequestDto.getPhone_num());
        existingStudent.setFather_phone_num(studentUpdateRequestDto.getFather_phone_num());
        existingStudent.setMother_phone_num(studentUpdateRequestDto.getMother_phone_num());
        existingStudent.setGuardians_phone_num(studentUpdateRequestDto.getGuardians_phone_num());
        existingStudent.setUpdated_at(LocalDateTime.now());
        existingStudent.setUpdated_charged_id(-1L);

        // Save the updated student
        Student savedStudent = studentRepository.save(existingStudent);
        return mapToResponseDto(savedStudent);
    }

    public StudentResponseDto deleteStudent(Student existingStudent){

        // Update fields from request DT
        existingStudent.setStatus(Status.INACTIVE);
        existingStudent.setUpdated_at(LocalDateTime.now());
        existingStudent.setUpdated_charged_id(-1L);

        // Save the updated student
        Student savedStudent = studentRepository.save(existingStudent);
        return mapToResponseDto(savedStudent);
    }


    private StudentResponseDto mapToResponseDto(Student student) {
        StudentResponseDto responseDto = new StudentResponseDto();
        responseDto.setId(student.getId());
        responseDto.setLogin_id(student.getLogin_id());
        responseDto.setName(student.getName());
        responseDto.setSerial_number(student.getSerial_number());
        responseDto.setGrade(student.getGrade());
        responseDto.setBirth(student.getBirth());
        responseDto.setSex(student.getSex());
        responseDto.setPhone_num(student.getPhone_num());
        responseDto.setFather_phone_num(student.getFather_phone_num());
        responseDto.setMother_phone_num(student.getMother_phone_num());
        responseDto.setGuardians_phone_num(student.getGuardians_phone_num());
        responseDto.setStatus(student.getStatus());
        responseDto.setCreated_at(student.getCreated_at());
        responseDto.setUpdated_at(student.getUpdated_at());
        responseDto.setCreated_charged_id(student.getCreated_charged_id());
        responseDto.setUpdated_charged_id(student.getUpdated_charged_id());
        return responseDto;
    }
}
