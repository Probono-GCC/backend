package probono.gcc.school.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.course.CreateCourseRequest;
import probono.gcc.school.model.dto.courseUser.CourseUserResponse;
import probono.gcc.school.model.dto.courseUser.CreateCourseUserRequest;
import probono.gcc.school.service.CourseUserService;

@RestController
@RequiredArgsConstructor
public class CourseUserController {

  private final CourseUserService courseUserService;

  @PostMapping("/course")
  public ResponseEntity<CourseUserResponse> createCourseUser(
      @RequestBody @Valid CreateCourseUserRequest request) {
    CourseResponse createdCourse = courseUserService.create(request);
    return ResponseEntity.ok(createdCourse);
  }

  @GetMapping("/course/{id}")
  public ResponseEntity<CourseResponse> getCourse(@PathVariable long id) {
    CourseResponse findCourse = courseService.getCourse(id);
    return ResponseEntity.ok(findCourse);
  }

  @PutMapping("/course/{id}")
  public ResponseEntity<CourseResponse> updateCourse(@PathVariable long id,
      @RequestBody @Valid CreateCourseRequest request) {
    CourseResponse updateCourse = courseService.updateCourse(id, request);
    return ResponseEntity.ok(updateCourse);
  }

  @DeleteMapping("/course/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }
}
