package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.ReceivedCourse;
import org.example.model.ReceivedTask;
import org.example.model.Student;
import org.example.model.dto.StudentDTO;
import org.example.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Tag(name = "студенты", description = "управление студентами")
@RestController
@RequestMapping(path = ("/students"))
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Operation(
            summary = "получить всех студентов",
            description = "с возможностью поиска по ключевому слову и сортировки по определенному парметру"
    )
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/", method = RequestMethod.GET, headers ="Accept=application/json")
    public Page<StudentDTO> getAllStudents(@PageableDefault(page = 0, size = 15, direction = Sort.Direction.ASC, sort = "points")
                                           @Parameter(description = "pageable запрос, содержащий номер страницы, кол-во страниц, тип сортировки, параметр сортировки ") Pageable pageable){
        return studentService.getAllStudents(pageable);
    }

    @Operation(summary = "добавить нового студента")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.POST, headers ="Accept=application/json")
    public Student addStudent(@RequestBody @Parameter(description = "новый студент") Student student ){
    return studentService.addStudent(student);
    }

    @Operation(summary = "получить профиль студента")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers ="Accept=application/json")
    public StudentDTO getProfileOfStudent(@PathVariable("id") @Parameter(description = "id студента") Integer studentId){
        return studentService.getOneStudent(studentId);
    }

    @Operation(summary = "удалить определенного студента")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteStudent(@PathVariable("id") @Parameter(description = "id студента") Integer studentId){
        studentService.deleteStudent(studentId);
    }

    @Operation(summary = "обновить профиль студента")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers ="Accept=application/json")
    public StudentDTO updateStudent(@PathVariable("id") @Parameter(description = "id студента") Integer studentId,
                                    @RequestBody @Parameter(description = "обновленная информация о студенте") StudentDTO student){
        return studentService.updateStudent(studentId, student);
    }

    @ResponseBody
    @Operation(summary = "получить все приобретенные курсы студента")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/courses", method = RequestMethod.GET, headers ="Accept=application/json")
    public Page<ReceivedCourse> getAllReceivedCoursesOfStudent(@PathVariable("id") @Parameter(description = "id студента") Integer studentId ,
                                                               @PageableDefault(page = 0, size = 7, direction = Sort.Direction.ASC, sort = "id")
                                                               @Parameter(description = "pageable запрос, содержащий номер страницы, кол-во страниц, тип сортировки, параметр сортировки ") Pageable pageable){
        return studentService.getReceivedCourses(studentId, pageable);
    }

    @Operation(summary = "добавить курс студенту")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}/courses/{courseId}", method = RequestMethod.POST, headers ="Accept=application/json")
    public ReceivedCourse  setCourseToStudent(@PathVariable("id") Integer studentId,
                                              @PathVariable("courseId") Integer courseId){
        return studentService.setCourseToStudent(courseId, studentId);
    }

    @Operation(summary = "добавить задание студенту")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "{id}/tasks/{taskId}", method = RequestMethod.POST, headers ="Accept=application/json")
    public ReceivedTask setTaskToStudent(@PathVariable("id") @Parameter(description = "id студента") Integer studentId,
                                         @PathVariable("taskId") @Parameter(description = "id задания") Integer taskId){
        return studentService.setTaskToStudent(taskId, studentId);
    }

    @Operation(summary = "получить все взятые задания студента")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "{id}/tasks/{receivedTaskId}", method = RequestMethod.GET, headers ="Accept=application/json")
    public ReceivedTask getReceivedTaskOfStudent(@PathVariable("id") @Parameter(description = "id студента") Integer studentId,
                                                 @PathVariable("receivedTaskId") @Parameter(description = "id задания") Integer taskId){
        return studentService.getReceivedTaskOfStudent(studentId,taskId);
    }

}

