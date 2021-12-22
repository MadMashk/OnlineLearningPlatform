package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.model.AppUser;
import org.example.model.ReceivedTask;
import org.example.model.Student;
import org.example.model.Task;
import org.example.model.exceptions.NotFoundException;
import org.example.services.TaskService;
import org.example.spring.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskService;
    @MockBean
    private SecurityService securityService;

    Task task1;
    Task task2;

    Student student1;

    AppUser user1;

    ReceivedTask receivedTask1;




    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1);
        task1.setName("oldTask");

        task2= new Task();
        task2.setId(2);

        student1 = new Student();
        student1.setId(1);
        student1.setUserName("MashaUser");

        receivedTask1 = new ReceivedTask();
        receivedTask1.setTask(task1);
        receivedTask1.setStudent(student1);
        receivedTask1.setId(1);

        user1 = new AppUser();
        user1.setId(1);
        user1.setUserName("MashaUSer");


    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldGetTask1Successfully() {
        when(taskService.getOneTask(1)).thenReturn(task1);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/{id}/", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));

        verify(taskService).getOneTask(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldFail404NotFound() {
        when(taskService.getOneTask(1)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/{id}/", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService).getOneTask(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "ADMIN")
    void shouldDeleteTask1Successfully() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/{id}/", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "ADMIN")
    void deleteTaskShouldFail404NotFound() {
        doThrow(NotFoundException.class)
                .when(taskService)
                .deleteTask(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/{id}/", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService).deleteTask(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "ADMIN")
    void shouldUpdateTask1Successfully() {
        Task updatedTask = new Task();
        updatedTask.setId(1);
        updatedTask.setName("newTask");

        Task taskToUpdate = new Task();
        taskToUpdate.setName("new Task");

        when(taskService.updateTask(1, taskToUpdate )).thenReturn(updatedTask);

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(taskToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(updatedTask.getId())))
                .andExpect(jsonPath("$.name", equalTo(updatedTask.getName())));

        verify(taskService).updateTask(1, taskToUpdate);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "ADMIN")
    void updateTaskShouldFail404NotFound() {
        Task updatedTask = new Task();
        updatedTask.setId(1);
        updatedTask.setName("newTask");

        Task taskToUpdate = new Task();
        taskToUpdate.setName("new Task");

        when(taskService.updateTask(1, taskToUpdate )).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(taskToUpdate)))
                .andExpect(status().isNotFound());


        verify(taskService).updateTask(1, taskToUpdate);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "ADMIN")
    void shouldAddTask1Successfully() {
        when(taskService.addTask(task1)).thenReturn(task1);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(task1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));

        verify(taskService).addTask(task1);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getTaskToCheckShouldGetTask1ToCheck() {
        when(taskService.getTaskToCheck(1,1)).thenReturn(receivedTask1);
        when(securityService.hasStudentAccess("MashaUser", 1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/{id}/checking", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.student.id",equalTo(1)));

        verify(taskService).getTaskToCheck(1,1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getTaskToCheckShouldFail404NotFound() {
        when(taskService.getTaskToCheck(1,1)).thenThrow(new NotFoundException("NOT FOUND"));
        when(securityService.hasStudentAccess("MashaUser", 1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/{id}/checking", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("studentId", "1"))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskToCheck(1,1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldCompleteTask1Successfully() {
        when(taskService.completeTask(1,1)).thenReturn(receivedTask1);
        when(securityService.hasStudentAccess("MashaUser", 1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/{id}/completion", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.student.id",equalTo(1)));

        verify(taskService).completeTask(1,1);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void completeTaskShouldFail404NotFound() {
        when(taskService.completeTask(1,1)).thenThrow(new NotFoundException("NOT FOUND"));
        when(securityService.hasStudentAccess("MashaUser", 1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/{id}/completion", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("studentId", "1"))
                .andExpect(status().isNotFound());

        verify(taskService).completeTask(1,1);

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}