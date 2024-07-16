package probono.gcc.school.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.dto.TeacherResponseDto;
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

        // Create a new TeacherCreateRequestDto without the re_type_pw field
        TeacherCreateRequestDto teacherCreateRequestDto = new TeacherCreateRequestDto();
        teacherCreateRequestDto.setName(requestDto.getName());
        teacherCreateRequestDto.setLogin_id(requestDto.getLogin_id());
        teacherCreateRequestDto.setLogin_pw(requestDto.getLogin_pw());

        // Convert teacherCreateRequestDto to Teacher entity
        Teacher teacher = modelMapper.map(requestDto, Teacher.class);

        //created_charded_id를 Dummy data로 set
        teacher.setCreated_charged_id(1L);
        teacher.setStatus(Status.ACTIVE);

        // Teacher 엔티티를 데이터베이스에 저장
        // 여기서 save 메소드 호출 시 @PrePersist 메소드가 호출
        Teacher teacherCreated=teacherRepository.save(teacher);
        return modelMapper.map(teacherCreated, TeacherCreateResponseDto.class);

    }

    // 모든 Teacher 가져오기
    public List<TeacherResponseDto> findAllTeacher() {
        try {
            List<Teacher> teacherList = teacherRepository.findAll();
            // stream과 mapper를 사용하여 리스트 변환
            return teacherList.stream()
                    .map(teacher -> modelMapper.map(teacher, TeacherResponseDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Exception handling
            throw new RuntimeException("An error occurred while fetching teachers", e);
        }
    }

    public TeacherResponseDto findOneTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("fail to findOneTeaher")
        );
        return modelMapper.map(teacher, TeacherResponseDto.class);
    }

    @Transactional
    public Long update(Long id, TeacherUpdateRequestDto updateTeacher) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        //Dummy Data
        //updateTeacher.setUpdated_charged_id = 1L ;
        //updated_charded_id를 Dummy data로 set
        //teacher.setCreated_charged_id(1L);
        teacher.setUpdated_charged_id(1L);
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
