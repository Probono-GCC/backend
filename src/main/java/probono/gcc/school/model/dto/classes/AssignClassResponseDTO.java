package probono.gcc.school.model.dto.classes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignClassResponseDTO {
  private long classId;
  private int year;
  private Grades grade;
  private Sections section;
  private TeacherResponseDTO teacher;
  private StudentResponseDTO student;

}
