

//해당 course의 선생님 조회

//선생님의 담당 course 조회
package probono.gcc.school.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

  @PostMapping("/courseUser")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<CourseUserResponse> createCourseUser(
      @RequestBody @Valid CreateCourseUserRequest request) {
    CourseUserResponse createdCourseUser = courseUserService.create(request);
    return ResponseEntity.ok(createdCourseUser);
  }


  @GetMapping("/courseUser/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN', 'STUDENT')")
  public ResponseEntity<CourseUserResponse> getCourseUser(@PathVariable long id) {
    CourseUserResponse findCourseUser = courseUserService.getCourseUser(id);
    return ResponseEntity.ok(findCourseUser);
  }

  @PutMapping("/courseUser/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<CourseUserResponse> updateCourseUser(@PathVariable long id,
      @RequestBody @Valid CreateCourseUserRequest request) {
    CourseUserResponse updatedCourseUser = courseUserService.updateCourseUser(id, request);
    return ResponseEntity.ok(updatedCourseUser);
  }

  @DeleteMapping("/courseUser/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Void> deleteCourseUser(@PathVariable long id) {
    courseUserService.deleteCourseUser(id);
    return ResponseEntity.noContent().build();
  }


  //해당 course를 듣는 학생리스트 조회
  @GetMapping("/courseUser/course/{courseId}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Page<CourseUserResponse>> getStudentsByCourse(@PathVariable long courseId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<CourseUserResponse> studentsList = courseUserService.getStudentsByCourseId(courseId, page,
        size);
    return ResponseEntity.ok(studentsList);
  }


  //해당 course의 선생님 조회
  @GetMapping("/courseUser/course/{courseId}/teacher")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<CourseUserResponse> getTeacherByCourse(@PathVariable Long courseId) {
    CourseUserResponse courseUserTeacher = courseUserService.getTeacherByCourseId(courseId);

    return ResponseEntity.ok(courseUserTeacher);
  }


  //선생님이 담당하는 courseList 조회
  @GetMapping("/courseUser/teacher/{username}/courses")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<List<CourseUserResponse>> getCoursesByTeacher(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,@PathVariable String username) {
    List<CourseUserResponse> coursesList = courseUserService.getCoursesByTeacherUsername(username);
    return ResponseEntity.ok(coursesList);
  }



}
