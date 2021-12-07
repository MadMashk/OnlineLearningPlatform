package web.controllers;

import model.ReceivedCourse;
import model.ReceivedTask;
import model.Student;
import model.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import services.StudentService;

@RestController
@RequestMapping(path = ("/students"))
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/", method = RequestMethod.GET, headers ="Accept=application/json") //получить всех учеников
    public Page<StudentDTO> getAllStudents(@PageableDefault(page = 0, size = 15, direction = Sort.Direction.ASC, sort = "points") Pageable pageable){
        return studentService.getAllStudents(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.POST, headers ="Accept=application/json")  //добавить ученика
    public Student addStudent(@RequestBody Student student){
        return studentService.addStudent(student);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers ="Accept=application/json") //получить ученика
    public StudentDTO getProfileOfStudent(@PathVariable("id") Integer studentId){
        return studentService.getOneStudent(studentId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE) //удалить ученика
    public void deleteStudent(@PathVariable("id") Integer studentId){
        studentService.deleteStudent(studentId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers ="Accept=application/json") //обновить ученика
    public StudentDTO deleteStudent(@PathVariable("id") Integer studentId,
                                 @RequestBody StudentDTO student){
       return studentService.updateStudent(studentId, student);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/courses", method = RequestMethod.GET, headers ="Accept=application/json") //все курсы ученика
    public Page<ReceivedCourse> getAllReceivedCoursesOfStudent(@PathVariable("id") Integer studentId,
                                                               @PageableDefault(page = 0, size = 7, direction = Sort.Direction.ASC, sort = "id") Pageable pageable){
        return studentService.getReceivedCourses(studentId, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}/courses/{courseId}", method = RequestMethod.POST, headers ="Accept=application/json") //добавить курс ученику
    public ReceivedCourse  setCourseToStudent(@PathVariable("id") Integer studentId,
                                              @PathVariable("courseId") Integer courseId){
       return studentService.setCourseToStudent(courseId, studentId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "{id}/tasks/{taskId}", method = RequestMethod.POST, headers ="Accept=application/json") //добавить задание ученику
    public ReceivedTask setTaskToStudent(@PathVariable("id") Integer studentId,
                                         @PathVariable("taskId") Integer taskId){
        return studentService.setTaskToStudent(taskId, studentId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "{id}/tasks/{receivedTaskId}", method = RequestMethod.GET, headers ="Accept=application/json") //получить взятое задание ученика
    public ReceivedTask getReceivedTaskOfStudent(@PathVariable("id") Integer studentId,
                                                 @PathVariable("receivedTaskId") Integer taskId){
        return studentService.getReceivedTaskOfStudent(studentId,taskId);
    }

}
