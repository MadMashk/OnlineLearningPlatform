package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.ReceivedTask;
import org.example.model.Task;
import org.example.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "задания", description = "управление заданиями")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "получить определенное задание")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Task getOneTask(@PathVariable("id") @Parameter(description = "id задания") Integer id){
        return taskService.getOneTask(id);
    }

    @Operation(summary = "удалить задание")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteTask(@PathVariable("id") @Parameter(description = "id задания") Integer id){
        taskService.deleteTask(id);
    }

    @Operation(summary = "добавление нового задания")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json" )
    public Task addTask(@RequestBody @Parameter(description = "новое задание") Task task){
        return taskService.addTask(task);
    }

    @Operation(summary = "обновить данные о задании")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public Task updateTask(@PathVariable("id") @Parameter(description = "id задания") Integer id,
                           @RequestBody @Parameter(description = "обновленная информация о задании") Task task){
        return taskService.updateTask(id, task);
    }

    @Operation(
            summary = "отправить задание на проверку",
            description = "сдать задание на проверку учителю, сели задание необходимо проверить"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}/checking", method = RequestMethod.POST)  //сдать задание на проверку
    public ReceivedTask getTaskToCheck(@PathVariable("id") @Parameter(description = "id задания") Integer id,
                                       @RequestParam("studentId") @Parameter(description = "id ученика") Integer studentId) {
        return taskService.getTaskToCheck(id, studentId);
    }

    @Operation(
            summary = "завершить задание",
            description = "студент может завершить задание сам или его завершит учитель после проверки"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('TEACHER') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}/completion", method = RequestMethod.POST)
    public ReceivedTask completeTask(@PathVariable("id") @Parameter(description = "id задания") Integer id,
                                     @RequestParam("studentId") @Parameter(description = "id ученика") Integer studentId){
        return taskService.completeTask(studentId, id);
    }

}
