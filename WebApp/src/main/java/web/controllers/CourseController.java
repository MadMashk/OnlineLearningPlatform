package web.controllers;


import model.Attachment;
import model.Course;
import model.Task;
import model.Teacher;
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
import services.CourseService;
import services.TaskService;
import services.TeacherService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = ("/courses"))
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TeacherService teacherService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method=RequestMethod.POST, headers ="Accept=application/json") //добавить курс
    public Course addCourse(@RequestBody Course course){
        return courseService.addNewCourse(course);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/", method=RequestMethod.GET, headers ="Accept=application/json") //получить все курсы
    public Page<Course> courseSearching(@RequestParam("keyword") Optional<String> keyword,
                                        @PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id") Pageable pageable) {
        return courseService.courseSearching(keyword.orElse(""), pageable);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method=RequestMethod.GET, headers ="Accept=application/json") //получить один курс
    public Course getOneCourses(@PathVariable("id") Integer courseId){
        return courseService.getOneCourse(courseId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method=RequestMethod.DELETE) //удалить курс
    public void deleteCourse(@PathVariable("id") Integer courseId){
         courseService.deleteCourse(courseId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method=RequestMethod.PUT, headers ="Accept=application/json") //обновить курс
    public Course deleteCourse(@PathVariable("id") Integer courseId,
                               @RequestBody Course course ){
        return courseService.updateCourse(course, courseId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}/tasks/{taskId}", method=RequestMethod.POST, headers ="Accept=application/json") //добавить задание к курсу
    public void setTaskToCourse(@PathVariable("id") Integer courseId,
                                @PathVariable("taskId") Integer taskId ){
       taskService.setTaskToCourse(courseId,taskId);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/tasks", method=RequestMethod.GET, headers ="Accept=application/json") //получить все задания курса
    public Page<Task> getAllTasksOfCourse(@PathVariable("id") Integer courseId,
                                          @PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id") Pageable pageable){
        return taskService.getAllTasksOfCourse(courseId, pageable);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/tasks/{taskId}",  method=RequestMethod.GET, headers ="Accept=application/json")//получить одно задание курса
    public Task getOneTaskOfCourse(@PathVariable("id") Integer courseId,
                                   @PathVariable("taskId") Integer taskId){
        return taskService.getOneTasksOfCourse(courseId,taskId);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/attachments", method=RequestMethod.GET, headers ="Accept=application/json") //получить вложения курса //todo переделать возвращаемый фаил
    public List<Attachment> getAttachmentsOfCourse(@PathVariable("id") Integer id){
     return courseService.getAttachmentsOfCourse(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}/attachments/uploading", method=RequestMethod.POST, headers ="Accept=application/json") //загрузить вложения в курс
    public void uploadAttachments(@RequestParam("attachments")MultipartFile[] attachments,
                                  @PathVariable("id") Integer courseId){
        for (MultipartFile attachment: attachments) {
            courseService.saveAttachmentToCourse(attachment,courseId);
        }
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/attachments/downloading/{attachmentId}", method=RequestMethod.POST, headers ="Accept=application/json") //скачать вложение из курса
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable("attachmentId") Integer attachmentId,
                                                                @PathVariable("id") Integer courseID){
     Attachment attachment = courseService.getOneAttachmentOfCourse(attachmentId,courseID);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment:filename=\""+attachment.getName()+"\"")
                .body(new  ByteArrayResource(attachment.getData()));
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}/teachers", method = RequestMethod.GET, headers ="Accept=application/json") //получить учителей курса
    public Page<Teacher> getTeachersOfCourse(@PathVariable("id") Integer courseId,
                                             @PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id") Pageable pageable){
       return courseService.getTeachersOfCourse(courseId, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}/teachers/{teacherId}", method = RequestMethod.POST, headers ="Accept=application/json") //добавить учителя к курсу
    public void setTeachersToCourse(@PathVariable("id") Integer courseId,
                                    @PathVariable("teacherId") Integer teacherId){
         teacherService.addTeacherToCourse(teacherId,courseId);
    }

}
