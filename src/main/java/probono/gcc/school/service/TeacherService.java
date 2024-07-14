package probono.gcc.school.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.TeacherListResponseDto;
import probono.gcc.school.model.dto.TeacherRequestDto;
import probono.gcc.school.model.dto.TeacherResponseDto;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.repository.TeacherRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherResponseDto createTeacher(TeacherRequestDto requestDto) {
        Teacher teacher = new Teacher(requestDto);
        teacherRepository.save(teacher);
        return new TeacherResponseDto(teacher);
    }

    // 모든 Teacher 가져오기
    public List<TeacherListResponseDto> findAllTeacher() {
        try{
            List<Teacher> teacherList = teacherRepository.findAll();

            List<TeacherListResponseDto> responseDtoList = new ArrayList<>();

            for (Teacher teacher : teacherList) {
                responseDtoList.add(
                        new TeacherListResponseDto(teacher)
                );
            }
            return responseDtoList;
        } catch (Exception e) {
//            throw new DBEmptyDataException("a");
        }
        return null;
    }



}
