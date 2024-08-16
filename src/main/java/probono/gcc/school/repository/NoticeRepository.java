package probono.gcc.school.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.enums.Status;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

  List<Notice> findByClassId(Classes classId);

  List<Notice> findByCourseId(Course courseId);

  List<Notice> findByClassIdIsNullAndCourseIdIsNull();

  Page<Notice> findByStatusAndClassId(Status status, Classes classes, Pageable pageable);

  Page<Notice> findByStatusAndCourseId(Status status, Course course, Pageable pageable);

  Page<Notice> findByClassIdIsNullAndCourseIdIsNull(Status status, Pageable pageable);

}
