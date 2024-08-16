package probono.gcc.school.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.ClassResponse;
import probono.gcc.school.model.dto.CreateClassRequest;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.course.CreateCourseRequest;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.service.CourseService;

@RestController
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @PostMapping("/course")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<CourseResponse> createCourse(
      @RequestBody @Valid CreateCourseRequest request) {
    CourseResponse createdCourse = courseService.create(request.getClassId(),
        request.getSubjectId());
    return ResponseEntity.ok(createdCourse);
  }

  @GetMapping("/course/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN', 'STUDENT')")
  public ResponseEntity<CourseResponse> getCourse(@PathVariable long id) {
    CourseResponse findCourse = courseService.getCourse(id);
    return ResponseEntity.ok(findCourse);
  }

  @PutMapping("/course/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<CourseResponse> updateCourse(@PathVariable long id,
      @RequestBody @Valid CreateCourseRequest request) {
    CourseResponse updateCourse = courseService.updateCourse(id, request);
    return ResponseEntity.ok(updateCourse);
  }

  @DeleteMapping("/course/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Void> deleteCourse(@PathVariable long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }
}
