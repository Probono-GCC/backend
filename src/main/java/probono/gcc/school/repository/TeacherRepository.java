package probono.gcc.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByLoginId(String loginId);
}
