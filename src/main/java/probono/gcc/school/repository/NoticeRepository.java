package probono.gcc.school.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Status;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {


  List<Notice> findByCourseId(Course courseId);

  //List<Notice> findByClassIdIsNullAndCourseIdIsNull();

  Page<Notice> findByStatusAndClassId(Status status, Classes classes, Pageable pageable);

  List<Notice> findByStatusAndClassId(Status status, Classes classes);

  Page<Notice> findByStatusAndCourseId(Status status, Course course, Pageable pageable);

  List<Notice> findByStatusAndCourseId(Status status, Course course);

  Page<Notice> findByTypeAndStatus(NoticeType type, Status status, Pageable pageable);

  @Modifying
  @Transactional
  @Query("UPDATE Notice n SET n.views = n.views + 1 WHERE n.id = :id")
  void incrementViews(@Param("id") Long id);

}
