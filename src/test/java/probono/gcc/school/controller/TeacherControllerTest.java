package probono.gcc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import probono.gcc.school.model.enums.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerTest {

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
    //teacherRepository.deleteAll(); // 모든 테이블의 모든 데이터 삭제
  }

  @AfterEach
  void tearDown() {
    // 데이터베이스 정리
    teacherRepository.deleteAll(); // 모든 테이블의 모든 데이터 삭제
  }

  @Test
  @DisplayName("Controller : teacher 생성 TEST")
  void createTeacher() throws Exception {
    //given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .loginId("testId")
        .name("testName")
        .loginPw("testPw")
        .build();

    String json = objectMapper.writeValueAsString(requestDto);

    //when
    mockMvc.perform(post("/teachers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andDo(print());

    // then
    assertThat(teacherRepository.count()).isEqualTo(1L);
    Teacher savedTeacher = teacherRepository.findAll().get(0);
    assertThat(savedTeacher.getLoginId()).isEqualTo("testId");
    assertThat(savedTeacher.getName()).isEqualTo("testName");
    assertThat(savedTeacher.getLoginPw()).isEqualTo("testPw");

  }

  @Test
  @DisplayName("Controller : 모든 선생님 목록을 조회 TEST.")
  void getAllTeachers() throws Exception {
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
    mockMvc.perform(get("/teachers")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());

    // then
    assertThat(teacherRepository.count()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Controller : 특정 선생님 정보를 수정 TEST")
  void updateTeacher() throws Exception {
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
    String json = objectMapper.writeValueAsString(updateRequestDto);

    // when
    mockMvc.perform(put("/teachers/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andDo(print());

    // then
    assertThat(teacherRepository.findById(id).get().getName()).isEqualTo("updatedName");
  }

  @Test
  @DisplayName("Controller : 특정 선생님을 삭제 TEST")
  void deleteTeacher() throws Exception {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .name("testName")
        .loginId("testId")
        .loginPw("testPw")
        .build();

    Long id = teacherService.createTeacher(requestDto).getId();

    // when
    mockMvc.perform(delete("/teachers/" + id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());

    // then
    Teacher deletedTeacher = teacherRepository.findById(id).orElse(null);
    assertThat(deletedTeacher).isNotNull();
    assertThat(deletedTeacher.getStatus()).isEqualTo(Status.INACTIVE);
  }


  @Test
  @DisplayName("ID 중복 여부를 체크 TEST")
  void checkIdDuplicate() throws Exception {
    // given
    TeacherCreateRequestDto requestDto = TeacherCreateRequestDto.builder()
        .name("testName")
        .loginId("testId")
        .loginPw("testPw")
        .build();

    teacherService.createTeacher(requestDto);

    TeacherCheckIdDTO checkIdDTO = TeacherCheckIdDTO.builder()
        .loginId("testId")
        .build();
    String json = objectMapper.writeValueAsString(checkIdDTO);

    // when
    mockMvc.perform(post("/teachers/check-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andDo(print());

    // then
    boolean isDuplicate = teacherRepository.existsByLoginId("testId");
    assertThat(isDuplicate).isTrue();
  }


}
