package probono.gcc.school.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.dto.TeacherListResponseDto;
import probono.gcc.school.model.dto.TeacherCreateRequestDto;
import probono.gcc.school.model.dto.TeacherCreateResponseDto;
import probono.gcc.school.model.dto.TeacherUpdateRequestDto;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private ModelMapper modelMapper;

    public TeacherCreateResponseDto createTeacher(TeacherCreateRequestDto requestDto) {
        //requestDto의 login_pw와 re_type_pw가 같은지 확인하고 다르면 예외처리
        //같으면 re_type_pw field를 빼고 나머지 field로 requestDto 새로 생성해서 객체 만들기
        if (!requestDto.getLogin_pw().equals(requestDto.getRe_type_pw())) {
            // Throw an exception if passwords do not match
            throw new IllegalArgumentException("Password and re-type password do not match");
        }

        // Create a new TeacherCreateRequestDto without the re_type_pw field
        TeacherCreateRequestDto sanitizedRequestDto = new TeacherCreateRequestDto();
        sanitizedRequestDto.setName(requestDto.getName());
        sanitizedRequestDto.setLogin_id(requestDto.getLogin_id());
        sanitizedRequestDto.setLogin_pw(requestDto.getLogin_pw());

        // Convert sanitizedRequestDto to Teacher entity
        Teacher teacher = convertToEntity(sanitizedRequestDto);

        //created_charded_id를 Dummy data로 set
        teacher.setCreated_charged_id(1L);
        teacher.setStatus(Status.ACTIVE);
        // Teacher 엔티티를 데이터베이스에 저장
        Teacher teacherCreated=teacherRepository.save(teacher);
        return convertToDto(teacherCreated);

    }

    private TeacherCreateResponseDto convertToDto(Teacher teacherCreated) {
        TeacherCreateResponseDto responseDto = modelMapper.map(teacherCreated, TeacherCreateResponseDto.class);
        return responseDto;
    }

    private Teacher convertToEntity(TeacherCreateRequestDto requestDto) {
        Teacher teacher = modelMapper.map(requestDto, Teacher.class);
        return teacher;
    }

    // 모든 Teacher 가져오기
    public List<TeacherListResponseDto> findAllTeacher() {
        try {
            List<Teacher> teacherList = teacherRepository.findAll();
            // stream과 mapper를 사용하여 리스트 변환
            return teacherList.stream()
                    .map(teacher -> modelMapper.map(teacher, TeacherListResponseDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Exception handling
            throw new RuntimeException("An error occurred while fetching teachers", e);
        }
    }


    public TeacherCreateResponseDto findOneTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("fail to findOneTeaher")
        );
//        return new TeacherCreateResponseDto(teacher);
        return null;
    }

    @Transactional
    public Long update(Long id, TeacherUpdateRequestDto updateTeacher) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        //Dummy Data
        //updateTeacher.setUpdated_charged_id = 1L ;
        teacher.update(updateTeacher);
        return teacher.getId();
    }

    @Transactional
    public Long deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        teacher.delete();
        return teacher.getId();
    }

    public Teacher findById(Long updatedTeacherId) {
        Teacher teacher = teacherRepository.findById(updatedTeacherId).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        return teacher;
    }
}
