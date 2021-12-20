package org.example.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.Attachment;
import org.example.model.Course;
import org.example.model.Task;
import org.example.model.Teacher;
import org.example.services.CourseService;
import org.example.services.TaskService;
import org.example.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "курсы", description = "управление курсами")
@RestController
@RequestMapping(path = ("/courses"))
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TeacherService teacherService;

    @Operation(summary = "добавление нового курса")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method=RequestMethod.POST, headers ="Accept=application/json")
    public Course addCourse(@RequestBody @Parameter(description = "новый курс") Course course){
        return courseService.addNewCourse(course);
    }

    @Operation(
            summary = "получить все курсы",
            description = "с возможностью поиска по ключевому слову и сортировки по определенному парметру"
    )
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/", method=RequestMethod.GET, headers ="Accept=application/json")
    public Page<Course> courseSearching(@RequestParam("keyword") @Parameter(description = "ключевое слово") Optional<String> keyword,
                                        @PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id")
                                        @Parameter(description = "pageable запрос, содержащий номер страницы, кол-во страниц, тип сортировки, параметр сортировки ") Pageable pageable) {
        return courseService.courseSearching(keyword.orElse(""), pageable);
    }

    @Operation(summary = "получить определенный курс")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method=RequestMethod.GET, headers ="Accept=application/json")
    public Course getOneCourses(@PathVariable("id") @Parameter(description = "id курса") Integer courseId){
        return courseService.getOneCourse(courseId);
    }

    @Operation(summary = "удалить курс")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method=RequestMethod.DELETE)
    public void deleteCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId){
        courseService.deleteCourse(courseId);
    }

    @Operation(summary = "обновление информации курса")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method=RequestMethod.PUT, headers ="Accept=application/json")
    public Course deleteCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId,
                               @RequestBody @Parameter(description = "курс с обновленной информацией") Course course ){
        return courseService.updateCourse(course, courseId);
    }

    @Operation(summary = "добавить задание к курсу")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}/tasks/", method=RequestMethod.POST, headers ="Accept=application/json")
    public void setTaskToCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId,
                                @RequestParam("taskId") @Parameter(description = "id задания") Integer taskId ){
        taskService.setTaskToCourse(courseId,taskId);
    }

    @Operation(summary = "все задания курса")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/tasks", method=RequestMethod.GET, headers ="Accept=application/json")
    public Page<Task> getAllTasksOfCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId,
                                          @PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id")
                                          @Parameter(description = "pageable запрос, содержащий номер страницы, кол-во страниц, тип сортировки, параметр сортировки ") Pageable pageable){
        return taskService.getAllTasksOfCourse(courseId, pageable);
    }

    @Operation(summary = "определенное задание курса")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/tasks/{taskId}",  method=RequestMethod.GET, headers ="Accept=application/json")
    public Task getOneTaskOfCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId,
                                   @PathVariable("taskId") @Parameter(description = "id задания") Integer taskId){
        return taskService.getOneTasksOfCourse(courseId,taskId);
    }


    @Operation(summary = "получить вложение курса")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/attachments", method=RequestMethod.GET, headers ="Accept=application/json") //todo переделать возвращаемый фаил
    public List<Attachment> getAttachmentsOfCourse(@PathVariable("id") @Parameter(description = "id курса") Integer id ){
        return courseService.getAttachmentsOfCourse(id);
    }

    @Operation(summary = "загрузка вложений в курс")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}/attachments/uploading", method=RequestMethod.POST, headers ="Accept=application/json")
    public void uploadAttachments(@RequestParam("attachments") @Parameter(description = "вложение") MultipartFile[] attachments,
                                  @PathVariable("id") @Parameter(description = "id курса") Integer courseId){
        for (MultipartFile attachment: attachments) {
            courseService.saveAttachmentToCourse(attachment,courseId);
        }
    }

    @Operation(summary = "скачать вложение из курса")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/attachments/downloading/{attachmentId}", method=RequestMethod.POST, headers ="Accept=application/json")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable("attachmentId") @Parameter(description = "id вложения") Integer attachmentId,
                                                                @PathVariable("id") @Parameter(description = "id курса") Integer courseID){
        Attachment attachment = courseService.getOneAttachmentOfCourse(attachmentId,courseID);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment:filename=\""+attachment.getName()+"\"")
                .body(new  ByteArrayResource(attachment.getData()));
    }

    @Operation(summary = "показать учителей курса")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/teachers", method = RequestMethod.GET, headers ="Accept=application/json")
    public Page<Teacher> getTeachersOfCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId,
                                             @PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id")
                                             @Parameter(description = "pageable запрос, содержащий номер страницы, кол-во страниц, тип сортировки, параметр сортировки ")Pageable pageable){
        return courseService.getTeachersOfCourse(courseId, pageable);
    }

    @Operation(summary = "добавить учителя к курсу")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}/teachers/", method = RequestMethod.POST, headers ="Accept=application/json")
    public void setTeachersToCourse(@PathVariable("id") @Parameter(description = "id курса") Integer courseId,
                                    @RequestParam("teacherId") @Parameter(description = "id учителя") Integer teacherId){
        teacherService.addTeacherToCourse(teacherId,courseId);
    }

}
