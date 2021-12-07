package web.controllers;

import model.Teacher;
import model.dto.TeacherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import services.TeacherService;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/", method = RequestMethod.GET) //получить всех учителей
    public Page<TeacherDTO> getAllTeachers(@PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id") Pageable pageable){
        return teacherService.getAllTeachers(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.POST) //добавить учителя
    public Teacher addTeacher(@RequestBody Teacher teacher){
        return teacherService.addTeacher(teacher);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET) //получить определенного учителя
    public TeacherDTO getOneTeacher(@PathVariable("id") Integer id){
        return teacherService.getOne(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE) //удалить учителя
    public void deleteTeacher(@PathVariable("id") Integer id){
         teacherService.deleteTeacher(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasTeacherAccess(principal.getUsername(), #id)")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT) //обновить учителя
    public TeacherDTO updateTeacher(@PathVariable("id") Integer id,
                                 @RequestBody TeacherDTO teacher){
       return teacherService.updateTeacher(id,teacher);
    }

}
