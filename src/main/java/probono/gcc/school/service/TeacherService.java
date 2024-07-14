package probono.gcc.school.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.TeacherRequestDto;
import probono.gcc.school.model.dto.TeacherResponseDto;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.repository.TeacherRepository;

@Service
@AllArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;



    public TeacherResponseDto createTeacher(TeacherRequestDto requestDto) {
        Teacher teacher = new Teacher(requestDto);
        teacherRepository.save(teacher);
        return new TeacherResponseDto(teacher);
    }
}
