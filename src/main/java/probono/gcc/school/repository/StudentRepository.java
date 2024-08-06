package probono.gcc.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Student;
import probono.gcc.school.model.entity.Teacher;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
