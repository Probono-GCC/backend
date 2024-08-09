package probono.gcc.school.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import probono.gcc.school.model.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
