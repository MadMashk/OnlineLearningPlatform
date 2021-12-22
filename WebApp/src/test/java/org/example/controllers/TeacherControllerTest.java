package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.model.Teacher;
import org.example.model.dto.TeacherDTO;
import org.example.model.exceptions.NotFoundException;
import org.example.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class TeacherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TeacherService teacherService;

    Teacher teacher1;
    Teacher teacher2;

    TeacherDTO teacherDTO1;
    TeacherDTO teacherDTO2;

    List<Teacher> teacherList;
    List<TeacherDTO> teacherDTOList;

    Page<TeacherDTO> teacherPage;

    Pageable pageable;
    @BeforeEach
    void setUp() {
        teacher1 = new Teacher();
        teacher1.setUserName("MashaUser");
        teacher1.setName("Masha");
        teacher1.setId(1);

        teacher2 = new Teacher();
        teacher2.setUserName("PashaUser");
        teacher2.setName("Pasha");
        teacher2.setId(2);

        teacherList = new ArrayList<>();
        teacherList.add(teacher1);
        teacherList.add(teacher2);

        teacherDTO1 = new TeacherDTO(teacher1.getName(), teacher1.getUserName(), "masha@gmail.com", null );
        teacherDTO2 = new TeacherDTO(teacher2.getName(), teacher2.getUserName(), "pasha@gmail.com", null );

        teacherDTOList = new ArrayList<>();
        teacherDTOList.add(teacherDTO1);
        teacherDTOList.add(teacherDTO2);

        pageable =  PageRequest.of(0,5, Sort.by("id").ascending());
        teacherPage = new PageImpl<>(teacherDTOList, pageable, teacherDTOList.size());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void shouldGetAllTeachersSuccessfully() {
        when(teacherService.getAllTeachers(pageable)).thenReturn(teacherPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Masha")))
                .andExpect(jsonPath("$.content[1].name", is("Pasha")));

        verify(teacherService).getAllTeachers(pageable);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void shouldAddTeacherSuccessfully() {
        when(teacherService.addTeacher(teacher1)).thenReturn(teacher1);

        mockMvc.perform(MockMvcRequestBuilders.post("/teachers/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(teacher1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));

        verify(teacherService).addTeacher(teacher1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void shouldGetOneTeacherSuccessfully() {
        when(teacherService.getOne(1)).thenReturn(teacherDTO1);

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Masha")));

        verify(teacherService).getOne(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void getOneTeacherShouldFail404NotFound() {
        when(teacherService.getOne(1)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(teacherService).getOne(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void shouldDeleteTeacherSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(teacherService).deleteTeacher(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void deleteTeacherFail404NotFound() {
        doThrow(NotFoundException.class)
                .when(teacherService)
                .deleteTeacher(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(teacherService).deleteTeacher(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void shouldUpdateTeacherSuccessfully() {
        TeacherDTO teacherDTOToUpdate = new TeacherDTO("Nastja", null, null, null);
        TeacherDTO updatedTeacherDTO = new TeacherDTO("Nastja", "MashaUser", null, null);

        when(teacherService.updateTeacher(1, teacherDTOToUpdate)).thenReturn(updatedTeacherDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(teacherDTOToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Nastja")))
                .andExpect(jsonPath("$.userName", equalTo("MashaUser")));

        verify(teacherService).updateTeacher(1,teacherDTOToUpdate);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void updateTeacherFail404NotFound() {
        TeacherDTO teacherDTOToUpdate = new TeacherDTO("Nastja", null, null, null);

        when(teacherService.updateTeacher(1, teacherDTOToUpdate)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.put("/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(teacherDTOToUpdate)))
                .andExpect(status().isNotFound());

        verify(teacherService).updateTeacher(1,teacherDTOToUpdate);

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}