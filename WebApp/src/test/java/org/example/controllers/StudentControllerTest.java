package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.model.*;
import org.example.model.dto.StudentDTO;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.IStudentRepository;
import org.example.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentService studentService;
    @MockBean
    private IStudentRepository studentRepository;

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
    ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> integerArgumentCaptor2 = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<StudentDTO> studentDTOArgumentCaptor = ArgumentCaptor.forClass(StudentDTO.class);


    List<Student> studentList;
    List<StudentDTO> studentDTOS;
    List<ReceivedCourse> receivedCourses;
    List<ReceivedTask> receivedTasks;

    Page<StudentDTO> studentPage;
    Page<ReceivedCourse> receivedCoursesPage;

    Student student1;
    Student student2;
    Student student3;

    Course course1;
    Course course2;

    Task task1;
    Task task2;

    ReceivedCourse receivedCourse1;
    ReceivedCourse receivedCourse2;

    ReceivedTask receivedTask1;
    ReceivedTask receivedTask2;

    AppUser user1;
    AppUser user2;
    AppUser user3;

    StudentDTO student1DTO;
    StudentDTO student2DTO;
    StudentDTO student3DTO;

    Pageable pageable;

    @BeforeEach
    void setUp() {
        student1 = new Student();
        student1.setId(1);
        student1.setName("Masha");
        student1.setUserName("MashaUser");
        student1.setPoints((long) 10);

        student2 = new Student();
        student2.setId(2);
        student2.setName("Sasha");
        student2.setUserName("SashaUser");
        student2.setPoints((long) 20);

        student3 = new Student();
        student3.setId(3);
        student3.setName("Pasha");
        student3.setUserName("PashaUser");
        student3.setPoints((long) 40);

        user1 = new AppUser();
        user1.setUserName("MashaUser");
        user1.setId(1);
        user1.setEmail("masha4321@gmail.com");
        user1.setPassWord("1234");

        user2 = new AppUser();
        user2.setUserName("SashaUser");
        user2.setId(2);
        user2.setEmail("sasha4321@gmail.com");
        user2.setPassWord("1234");

        user3 = new AppUser();
        user3.setUserName("PashaUser");
        user3.setId(1);
        user3.setEmail("pasha4321@gmail.com");
        user3.setPassWord("1234");

        course1 = new Course();
        course1.setId(1);
        course1.setName("java course");

        course2 = new Course();
        course2.setId(2);
        course2.setName("c++ course");

        task1 = new Task();
        task1.setId(1);
        task1.setName("task1");

        task2 = new Task();
        task2.setId(1);
        task2.setName("task2");

        receivedCourse1 = new ReceivedCourse();
        receivedCourse1.setId(1);
        receivedCourse1.setCourse(course1);
        receivedCourse1.setStudent(student1);

        receivedCourse2 = new ReceivedCourse();
        receivedCourse2.setId(2);
        receivedCourse2.setCourse(course2);
        receivedCourse2.setStudent(student1);

        receivedTask1 = new ReceivedTask();
        receivedTask1.setId(1);
        receivedTask1.setStudent(student1);
        receivedTask1.setTask(task1);

        receivedTask2 = new ReceivedTask();
        receivedTask2.setId(2);
        receivedTask2.setStudent(student1);
        receivedTask2.setTask(task2);



        student1DTO = new StudentDTO(
             student1.getUserName(),
             student1.getName(),
             student1.getPoints(),
             user1.getEmail()
        );
        student2DTO = new StudentDTO(
                student2.getUserName(),
                student2.getName(),
                student2.getPoints(),
                user2.getEmail()
        );
        student3DTO = new StudentDTO(
                student3.getUserName(),
                student3.getName(),
                student3.getPoints(),
                user3.getEmail()
        );

        studentList = new ArrayList<>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);

        studentDTOS = new ArrayList<>();
        studentDTOS.add(student1DTO);
        studentDTOS.add(student2DTO);
        studentDTOS.add(student3DTO);

        receivedCourses = new ArrayList<>();
        receivedCourses.add(receivedCourse1);
        receivedCourses.add(receivedCourse2);

        receivedTasks = new ArrayList<>();
        receivedTasks.add(receivedTask1);
        receivedTasks.add(receivedTask2);

        studentPage= new PageImpl<>(studentDTOS, PageRequest.of(0, 10, Sort.by("points").ascending()),studentDTOS.size());
        receivedCoursesPage = new PageImpl<>(receivedCourses, PageRequest.of(0, 5, Sort.by("name").ascending()), receivedCourses.size());

        pageable =  PageRequest.of(0,5, Sort.by("name").ascending());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void evaluatesPageableParameter() {
        mockMvc.perform(MockMvcRequestBuilders.get("/students/")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpect(status().isOk());


        verify(studentService).getAllStudents(pageableCaptor.capture());
        Pageable pageable =  pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().toString()).isEqualTo("id: ASC");
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void shouldGetAllStudentsSuccessfully() {
    when(studentService.getAllStudents(any())).thenReturn(studentPage);
    mockMvc.perform(MockMvcRequestBuilders.get("/students/"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].userName", is("MashaUser")))
            .andExpect(jsonPath("$.content[0].name", is("Masha")))
            .andExpect(jsonPath("$.content[0].points", is(10)))
            .andExpect(jsonPath("$.content[0].email", is("masha4321@gmail.com")));

    verify(studentService, times(1)).getAllStudents(any());

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void addStudentSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.post("/students/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(student1)))
                .andExpect(status().isOk());

        verify(studentService).addStudent(studentCaptor.capture());

        assertThat(studentCaptor.getValue(), equalTo(student1));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void getProfileOfStudentSuccessfully() {
        when(studentService.getOneStudent(1)).thenReturn(student1DTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName", is("MashaUser")))
                .andExpect(jsonPath("$.name", is("Masha")))
                .andExpect(jsonPath("$.points", is(10)))
                .andExpect(jsonPath("$.email", is("masha4321@gmail.com")));

        verify(studentService, times(1)).getOneStudent(1);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void getProfileOfStudentFail404NotFound() {
        when(studentService.getOneStudent(1)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.get("/students/{id}", 1))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).getOneStudent(1);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void deleteStudentSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/students/{id}", 1))
                .andExpect(status().isOk());

        verify(studentService).deleteStudent(integerArgumentCaptor.capture());
        assertThat(integerArgumentCaptor.getValue(), equalTo(1));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void deleteStudentFail404NotFound() {
        doThrow(NotFoundException.class)
                .when(studentService)
                .deleteStudent(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/students/{id}", 1))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).deleteStudent(1);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void updateStudentSuccessfully() {
        StudentDTO studentDTO = new StudentDTO("KatjaUser", null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/students/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(studentDTO)))
                .andExpect(status().isOk());

        verify(studentService).updateStudent(integerArgumentCaptor.capture(), studentDTOArgumentCaptor.capture());
        assertThat(integerArgumentCaptor.getValue(), equalTo(1));
        assertThat(studentDTOArgumentCaptor.getValue(), equalTo(studentDTO));

    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void ShouldGetAllReceivedCoursesOfStudentSuccessfully() {

        when(studentService.getReceivedCourses(1, pageable)).thenReturn(receivedCoursesPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/{id}/courses/", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].course.name", is("java course")))
                .andExpect(jsonPath("$.content[1].course.name", is("c++ course")));

        verify(studentService, times(1)).getReceivedCourses(1, pageable);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldSetCourseToStudentSuccessfully() {
        when(studentService.setCourseToStudent(1, 1)).thenReturn(receivedCourse1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student1));

        mockMvc.perform(MockMvcRequestBuilders.post("/students/{id}/courses/{courseId}/", 1, 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(studentService).setCourseToStudent(integerArgumentCaptor.capture(),integerArgumentCaptor2.capture());
        assertThat(integerArgumentCaptor.getValue(),equalTo(1));
        assertThat(integerArgumentCaptor2.getValue(), equalTo(1));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldSetTaskToStudentSuccessfully() {
        when(studentService.setTaskToStudent(1, 1)).thenReturn(receivedTask1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student1));

        mockMvc.perform(MockMvcRequestBuilders.post("/students/{id}/tasks/{taskId}/", 1, 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(studentService).setTaskToStudent(integerArgumentCaptor.capture(),integerArgumentCaptor2.capture());
        assertThat(integerArgumentCaptor.getValue(),equalTo(1));
        assertThat(integerArgumentCaptor2.getValue(), equalTo(1));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getReceivedTaskOfStudent() {
        when(studentService.getReceivedTaskOfStudent(1,1)).thenReturn(receivedTask1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student1));

        mockMvc.perform(MockMvcRequestBuilders.get("/students/{id}/tasks/{receivedTaskId}/", 1, 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));


       verify(studentService, times(1)).getReceivedTaskOfStudent(1,1);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getReceivedTaskOfStudentFAil404NotFound() {
        when(studentService.getReceivedTaskOfStudent(1,1)).thenThrow(new NotFoundException("NOT FOUND"));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student1));

        mockMvc.perform(MockMvcRequestBuilders.get("/students/{id}/tasks/{receivedTaskId}/", 1, 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).getReceivedTaskOfStudent(1,1);
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}