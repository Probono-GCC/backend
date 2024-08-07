package probono.gcc.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Subject;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
