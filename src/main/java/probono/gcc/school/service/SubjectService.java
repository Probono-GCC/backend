package probono.gcc.school.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.dto.SubjectRequestDTO;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Subject;
import org.modelmapper.ModelMapper;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.CourseRepository;
import probono.gcc.school.repository.SubjectRepository;

import static probono.gcc.school.model.enums.Status.ACTIVE;

@Service
@AllArgsConstructor
public class SubjectService {
    private ModelMapper modelMapper;
    private SubjectRepository subjectRepository;
    private CourseRepository courseRepository;
    private CourseService courseService;

    public SubjectResponseDTO createSubject(SubjectRequestDTO requestDto) {
        Optional<Subject> existingSubject = subjectRepository.findByName(requestDto.getName());
        if (existingSubject.isPresent()) {
            throw new IllegalArgumentException("A subject with the same name already exists.");
        }

        Subject subject = new Subject();
        subject.setName(requestDto.getName());
        subject.setElective(requestDto.isElective());
        subject.setStatus(ACTIVE);
        //createdChargeId 설정
        subject.setCreatedChargeId(1L);

        // subject 엔티티를 데이터베이스에 저장
        Subject subjectCreated = subjectRepository.save(subject);

        // 저장된 Teacher 엔티티를 TeacherCreateResponseDto로 매핑
        return modelMapper.map(subjectCreated, SubjectResponseDTO.class);

    }

    public List<SubjectResponseDTO> findAllSubject() {
        try {
            List<Subject> subjectList= subjectRepository.findAllByStatus(ACTIVE);
            // stream과 mapper를 사용하여 리스트 변환
            return subjectList.stream()
                .map(teacher -> modelMapper.map(teacher, SubjectResponseDTO.class))
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Exception handling
            throw new RuntimeException("An error occurred while fetching subjects", e);
        }
    }

    public SubjectResponseDTO findOneSubject(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("fail to findOneSubject")
        );
        return modelMapper.map(subject, SubjectResponseDTO.class);
    }

    @Transactional
    public Long update(Long id, SubjectRequestDTO requestDto) {
        // 기존 Teacher 엔티티를 데이터베이스에서 조회
        Subject subject  = subjectRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Invalid id")
        );

        // DTO의 값들을 엔티티에 직접 설정
        subject.setName(requestDto.getName());
        subject.setElective(requestDto.isElective());


        // `updated_at`과 `updated_charged_id`를 설정
        subject.setUpdatedChargeId(2L); // Dummy data 설정

        // 엔티티 상태가 변경되었으므로 JPA는 이를 자동으로 감지하고 업데이트
        // `save` 메소드를 호출하지 않아도 트랜잭션 종료 시 자동으로 업데이트

        // 업데이트된 엔티티의 ID 반환
        return subject.getSubjectId();
    }

    public Subject findById(Long updatedSubjectId) {
        Subject subject = subjectRepository.findById(updatedSubjectId).orElseThrow(
            () -> new IllegalArgumentException("unvalid id")
        );
        return subject;

    }

    @Transactional
    public Long deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("unvalid id")
        );

        // 2. subject에 할당된 course들을 가져와 논리적 삭제 수행
        List<Course> courses = subject.getCourseList();
        if (courses != null) {
            for (Course course : courses) {
                courseService.deleteCourse(course.getCourseId());
            }
        }

        // 논리적 삭제 수행
        subject.setStatus(Status.INACTIVE);
        // Dummy Data
        subject.setUpdatedChargeId(2L);
        return subject.getSubjectId();
    }
}
