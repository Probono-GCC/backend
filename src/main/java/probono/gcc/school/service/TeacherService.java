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
        // TeacherCreateRequestDto를 Teacher 엔티티로 변환
        Teacher teacher = convertToEntity(requestDto);
        //created_charded_id를 Dummy data로 set
        teacher.setCreated_charged_id(1L);
        teacher.setStatus(Status.ACTIVE);
        // Teacher 엔티티를 데이터베이스에 저장
        Teacher teacherCreated=teacherRepository.save(teacher);
        return convertToDto(teacherCreated);
//        // Teacher 엔티티를 TeacherResponseDto로 변환
//        TeacherCreateResponseDto teacherResponseDto = modelMapper.map(teacher, TeacherCreateResponseDto.class);
//        return teacherResponseDto;
//        Post post = convertToEntity(postDto);
//        Post postCreated = postService.createPost(post));
//        return convertToDto(postCreated);
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
    public Long update(Long id, TeacherUpdateRequestDto requestDto) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        teacher.update(requestDto);
        return teacher.getId();
    }

    @Transactional
    public Long deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
        return id;
    }

}
