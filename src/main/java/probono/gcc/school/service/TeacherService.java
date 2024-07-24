package probono.gcc.school.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import probono.gcc.school.model.dto.TeacherResponseDto;
import probono.gcc.school.model.dto.TeacherCreateRequestDto;
import probono.gcc.school.model.dto.TeacherCreateResponseDto;
import probono.gcc.school.model.dto.TeacherUpdateRequestDto;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.TeacherRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private ModelMapper modelMapper;

    public TeacherCreateResponseDto createTeacher(TeacherCreateRequestDto requestDto) {
//
//        // Convert teacherCreateRequestDto to Teacher entity
//        Teacher teacher = modelMapper.map(requestDto, Teacher.class);
//
//        //created_charded_id를 Dummy data로 set
//        teacher.setCreated_charged_id(1L);
//        teacher.setStatus(Status.ACTIVE);
//
//        // Teacher 엔티티를 데이터베이스에 저장
//        // 여기서 save 메소드 호출 시 @PrePersist 메소드가 호출
//        Teacher teacherCreated=teacherRepository.save(teacher);
//        return modelMapper.map(teacherCreated, TeacherCreateResponseDto.class);

        // 수동으로 Teacher 엔티티를 생성하고 값을 설정
        Teacher teacher = new Teacher();
        teacher.setName(requestDto.getName());
        teacher.setLoginId(requestDto.getLoginId());
        teacher.setLoginPw(requestDto.getLoginPw());
        teacher.setCreated_charged_id(1L); // Dummy data 설정
        teacher.setStatus(Status.ACTIVE);

        // Teacher 엔티티를 데이터베이스에 저장
        // 여기서 save 메소드 호출 시 @PrePersist 메소드가 호출
        Teacher teacherCreated = teacherRepository.save(teacher);

        // 저장된 Teacher 엔티티를 TeacherCreateResponseDto로 매핑
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
    public Long update(Long id, TeacherUpdateRequestDto requestDto) {
        // 기존 Teacher 엔티티를 데이터베이스에서 조회
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Invalid id")
        );

        // DTO의 값들을 엔티티에 직접 설정
        teacher.setName(requestDto.getName());
        teacher.setBirth(requestDto.getBirth());
        teacher.setPhone_num(requestDto.getPhone_num());

        //requestDto의 previous_pw와 teacher.getLogin_pw()가 같을시에만 로그인 비밀번호를 업데이트
        if (requestDto.getPrevious_pw() != null && requestDto.getPrevious_pw().equals(teacher.getLoginPw())) {
            teacher.setLoginPw(requestDto.getNew_pw());
        }else if (requestDto.getPrevious_pw() != null) {
            // 비밀번호가 일치하지 않을 경우 예외를 던짐
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Previous password is incorrect.");
        }

        // `updated_at`과 `updated_charged_id`를 설정
        teacher.setUpdated_at(LocalDateTime.now());
        teacher.setUpdated_charged_id(2L); // Dummy data 설정

        // 엔티티 상태가 변경되었으므로 JPA는 이를 자동으로 감지하고 업데이트
        // `save` 메소드를 호출하지 않아도 트랜잭션 종료 시 자동으로 업데이트

        // 업데이트된 엔티티의 ID 반환
        return teacher.getId();
    }

    @Transactional
    public Long deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        // 논리적 삭제 수행
        teacher.setStatus(Status.INACTIVE);
        return teacher.getId();
    }

    public Teacher findById(Long updatedTeacherId) {
        Teacher teacher = teacherRepository.findById(updatedTeacherId).orElseThrow(
                () -> new IllegalArgumentException("unvalid id")
        );
        return teacher;
    }


    // ID 중복 체크 메소드
    public boolean isLoginIdDuplicate(String loginId) {
        return teacherRepository.existsByLoginId(loginId);
    }

}
