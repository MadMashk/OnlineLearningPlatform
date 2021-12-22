package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.Teacher;
import org.example.model.dto.TeacherDTO;
import org.example.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "учителя", description = "управление учителями")
@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Operation(summary = "получить всех учителей")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Page<TeacherDTO> getAllTeachers(@PageableDefault(page = 0, size = 5, direction = Sort.Direction.ASC, sort = "id")
                                           @Parameter(description = "pageable запрос, содержащий номер страницы, кол-во страниц, тип сортировки, параметр сортировки ") Pageable pageable){
        return teacherService.getAllTeachers(pageable);
    }

    @Operation(summary = "добавление нового учителя")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Teacher addTeacher(@RequestBody @Parameter(description = "новый учитель") Teacher teacher){
        return teacherService.addTeacher(teacher);
    }

    @Operation(summary = "получить профиль определенного учителя")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public TeacherDTO getOneTeacher(@PathVariable("id") @Parameter(description = "id учителя") Integer id){
        return teacherService.getOne(id);
    }

    @Operation(summary = "удалить учителя")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteTeacher(@PathVariable("id") @Parameter(description = "id учителя") Integer id ){
        teacherService.deleteTeacher(id);
    }

    @Operation(summary = "обновить профиль учителя")
    @PreAuthorize("hasRole('ADMIN') or @securityService.hasTeacherAccess(principal.getUsername(), #id)")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public TeacherDTO updateTeacher(@PathVariable("id") @Parameter(description = "id учителя") Integer id,
                                    @RequestBody @Parameter(description = "обновленная информация об учителе") TeacherDTO teacher){
        return teacherService.updateTeacher(id,teacher);
    }
}