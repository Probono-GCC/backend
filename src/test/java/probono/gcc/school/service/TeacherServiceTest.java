package probono.gcc.school.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import probono.gcc.school.model.enums.Status;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherServiceTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TeacherRepository teacherRepository;

  @Autowired
  private TeacherService teacherService;

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
  @DisplayName("Service : teacher 생성 TEST")
  void createTeacher() {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .loginId("testId")
        .name("testName")
        .loginPw("testPw")
        .build();

    // when
    TeacherCreateResponseDto responseDto = teacherService.createTeacher(requestDto);

    // then
    assertThat(teacherRepository.count()).isEqualTo(1L);
    Teacher savedTeacher = teacherRepository.findAll().get(0);
    assertThat(savedTeacher.getLoginId()).isEqualTo("testId");
    assertThat(savedTeacher.getName()).isEqualTo("testName");
    assertThat(savedTeacher.getLoginPw()).isEqualTo("testPw");
  }

  @Test
  @DisplayName("Service : 모든 선생님 목록을 조회 TEST")
  void findAllTeacher() {
    // given
    TeacherCreateRequestDto requestDto1 = TeacherCreateRequestDto.builder()
        .name("testName1")
        .loginId("testId1")
        .loginPw("testPw1")
        .build();

    TeacherCreateRequestDto requestDto2 = TeacherCreateRequestDto.builder()
        .name("testName2")
        .loginId("testId2")
        .loginPw("testPw2")
        .build();

    teacherService.createTeacher(requestDto1);
    teacherService.createTeacher(requestDto2);

    // when
    List<TeacherResponseDto> teachers = teacherService.findAllTeacher();

    // then
    assertThat(teachers.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Service : 특정 선생님 정보를 조회 TEST")
  void findOneTeacher() {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .name("testName")
        .loginId("testId")
        .loginPw("testPw")
        .build();

    Long id = teacherService.createTeacher(requestDto).getId();

    // when
    TeacherResponseDto teacher = teacherService.findOneTeacher(id);

    // then
    assertThat(teacher).isNotNull();
    assertThat(teacher.getName()).isEqualTo("testName");
  }

  @Test
  @DisplayName("Service : 특정 선생님 정보를 수정 TEST")
  void updateTeacher() {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .name("testName")
        .loginId("testId")
        .loginPw("testPw")
        .build();

    Long id = teacherService.createTeacher(requestDto).getId();

    TeacherUpdateRequestDto updateRequestDto = TeacherUpdateRequestDto.builder()
        .name("updatedName")
        .previous_pw("testPw")
        .new_pw("updatedPw")
        .build();

    // when
    Long updatedId = teacherService.update(id, updateRequestDto);

    // then
    Teacher updatedTeacher = teacherRepository.findById(updatedId).get();
    assertThat(updatedTeacher.getName()).isEqualTo("updatedName");
    assertThat(updatedTeacher.getLoginPw()).isEqualTo("updatedPw");
  }

  @Test
  @DisplayName("Service : 특정 선생님을 삭제 TEST")
  void deleteTeacher() {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .name("testName")
        .loginId("testId")
        .loginPw("testPw")
        .build();

    Long id = teacherService.createTeacher(requestDto).getId();

    // when
    Long deletedId = teacherService.deleteTeacher(id);

    // then
    Teacher deletedTeacher = teacherRepository.findById(deletedId).orElse(null);
    assertThat(deletedTeacher).isNotNull();
    assertThat(deletedTeacher.getStatus()).isEqualTo(Status.INACTIVE);
  }

  @Test
  @DisplayName("Service : ID 중복 여부를 체크 TEST")
  void checkIdDuplicate() {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .name("testName")
        .loginId("testId")
        .loginPw("testPw")
        .build();

    teacherService.createTeacher(requestDto);

    // when
    boolean isDuplicate = teacherService.isLoginIdDuplicate("testId");

    // then
    assertThat(isDuplicate).isTrue();
  }

}
