package probono.gcc.school.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Subject;

public interface ImageRepository extends JpaRepository<Image, Long> {

  Optional<Image> findById(Long imageId);
}
