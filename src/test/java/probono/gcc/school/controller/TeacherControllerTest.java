package probono.gcc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import probono.gcc.school.model.dto.TeacherCreateRequestDto;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.repository.TeacherRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("teacher 생성 TEST")
    void createTeacher() throws Exception{
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
}
