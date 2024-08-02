package probono.gcc.school.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.model.enums.Status;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        // 데이터베이스 초기화 또는 필요한 데이터 설정
        teacherRepository.deleteAll(); // 모든 테이블의 모든 데이터 삭제
    }

    @AfterEach
    void tearDown() {
        // 데이터베이스 정리
        teacherRepository.deleteAll(); // 모든 테이블의 모든 데이터 삭제
    }

    @Test
    @DisplayName("Repository: 선생님 생성과 저장 TEST")
    void saveTeacher() {
        // given
        Teacher teacher = Teacher.builder()
                .name("testName")
                .loginId("testId")
                .loginPw("testPw")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        // when
        Teacher savedTeacher = teacherRepository.save(teacher);

        // then
        assertThat(savedTeacher).isNotNull();
        assertThat(savedTeacher.getId()).isNotNull();
        assertThat(savedTeacher.getName()).isEqualTo("testName");
        assertThat(savedTeacher.getLoginId()).isEqualTo("testId");
    }

    @Test
    @DisplayName("Repository: 모든 선생님 조회 TEST")
    void findAllTeachers() {
        // given
        Teacher teacher1 = Teacher.builder()
                .name("testName1")
                .loginId("testId1")
                .loginPw("testPw1")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        Teacher teacher2 = Teacher.builder()
                .name("testName2")
                .loginId("testId2")
                .loginPw("testPw2")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);

        // when
        List<Teacher> teachers = teacherRepository.findAll();

        // then
        assertThat(teachers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Repository: ID로 특정 선생님 조회 TEST")
    void findTeacherById() {
        // given
        Teacher teacher = Teacher.builder()
                .name("testName")
                .loginId("testId")
                .loginPw("testPw")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);
        Long id = savedTeacher.getId();

        // when
        Optional<Teacher> foundTeacher = teacherRepository.findById(id);

        // then
        assertThat(foundTeacher).isPresent();
        assertThat(foundTeacher.get().getName()).isEqualTo("testName");
    }

    @Test
    @DisplayName("Repository: 특정 선생님 정보 수정 TEST")
    void updateTeacher() {
        // given
        Teacher teacher = Teacher.builder()
                .name("testName")
                .loginId("testId")
                .loginPw("testPw")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);
        Long id = savedTeacher.getId();

        // when
        savedTeacher.setName("updatedName");
        Teacher updatedTeacher = teacherRepository.save(savedTeacher);

        // then
        assertThat(updatedTeacher.getName()).isEqualTo("updatedName");
    }

    @Test
    @DisplayName("Repository: 특정 선생님 soft 삭제 TEST")
    void deleteTeacher() {
        // given
        Teacher teacher = Teacher.builder()
                .name("testName")
                .loginId("testId")
                .loginPw("testPw")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);
        Long id = savedTeacher.getId();

        // when
        savedTeacher.setStatus(Status.INACTIVE);
        teacherRepository.save(savedTeacher);

        // then
        Teacher softDeletedTeacher = teacherRepository.findById(id).orElseThrow();
        assertThat(softDeletedTeacher.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("Repository: ID 중복 여부를 체크 TEST")
    void checkLoginIdDuplicate() {
        // given
        Teacher teacher = Teacher.builder()
                .name("testName")
                .loginId("testId")
                .loginPw("testPw")
                .created_charged_id(1L) // Dummy data 설정
                .status(Status.ACTIVE)
                .build();

        teacherRepository.save(teacher);

        // when
        boolean isDuplicate = teacherRepository.existsByLoginId("testId");

        // then
        assertThat(isDuplicate).isTrue();
    }


}
