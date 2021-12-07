package web.controllers;

import model.ReceivedTask;
import model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import services.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET) //получить определенное задание
    public Task getOneTask(@PathVariable("id") Integer id){
        return taskService.getOneTask(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE) //удалить задание
    public void deleteTask(@PathVariable("id") Integer id){
        taskService.deleteTask(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json" ) //добавить задание
    public Task addTask(@RequestBody Task task){
        return taskService.addTask(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json") //обновить задание
    public Task updateTask(@PathVariable("id") Integer id,
                           @RequestBody Task task){
        return taskService.updateTask(id, task);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}/checking", method = RequestMethod.POST)  //сдать задание на проверку
    public ReceivedTask getTaskToCheck(@PathVariable("id") Integer id,
                                       @RequestParam("studentId") Integer studentId) {
       return taskService.getTaskToCheck(id, studentId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('TEACHER') or @securityService.hasStudentAccess(principal.getUsername(), #studentId)")
    @RequestMapping(value = "/{id}/completion", method = RequestMethod.POST) //завершить задание
    public ReceivedTask completeTask(@PathVariable("id") Integer id,
                                     @RequestParam("studentId") Integer studentId){
        return taskService.completeTask(studentId, id);
    }

}
