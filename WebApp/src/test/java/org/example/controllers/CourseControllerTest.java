package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.model.Attachment;
import org.example.model.Course;
import org.example.model.Task;
import org.example.model.Teacher;
import org.example.model.exceptions.NotFoundException;
import org.example.services.CourseService;
import org.example.services.TaskService;
import org.example.services.TeacherService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseService courseService;
    @MockBean
    private TaskService taskService;
    @MockBean
    private TeacherService teacherService;

    ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);
    Course course1;
    Course course2;
    Course course3;

    Task task1;
    Task task2;

    Teacher teacher;

    Attachment attachment;

    List<Course> courses;
    List<Task> tasks;
    List<Attachment> attachments;
    List<Teacher> teacherList;

    Page<Course> coursePage;
    Page<Task> taskPage;
    Page<Teacher> teacherPage;

    Pageable pageable;



    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1);

        teacherList = new ArrayList<>();
        teacherList.add(teacher);

        attachment = new Attachment();
        attachment.setId(1);

        attachments = new ArrayList<>();
        attachments.add(attachment);

        task1 = new Task();
        task1.setName("task 1");
        task1.setId(1);
        task1.setDescription("some task");

        task2 = new Task();
        task2.setName("task 2");
        task2.setId(2);
        task2.setDescription("some task");

        tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        course1 = new Course();
        course1.setId(1);
        course1.setName("java course");
        course1.setDescription("programming course");
        course1.setTasksOfCourse(tasks);
        course1.setAttachmentsOfCourse(attachments);
        course1.setTeachersOfCourse(teacherList);

        course2 = new Course();
        course2.setId(2);
        course2.setName("c++ course");
        course2.setDescription("programming course");

        course3 = new Course();
        course3.setId(3);
        course3.setName("other course");
        course3.setDescription("some course");



        courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);
        courses.add(course3);


        teacherPage = new PageImpl<>(teacherList, PageRequest.of(0,5, Sort.by("id").ascending()), teacherList.size());
        coursePage = new PageImpl<>(courses, PageRequest.of(0,5, Sort.by("id").ascending()), courses.size());
        taskPage = new PageImpl<>(tasks, PageRequest.of(0,5, Sort.by("id").ascending()), tasks.size());
        pageable =  PageRequest.of(0,5, Sort.by("id").ascending());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void shouldAddCourseSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.post("/courses/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(course1)))
                .andExpect(status().isOk());

        verify(courseService).addNewCourse(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue().getName(), equalTo(course1.getName()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void courseSearchingShouldSearchAllProgrammingCourses() {
        List<Course> courses1 = new ArrayList<>();
        courses1.add(course1);
        courses1.add(course2);
        Page<Course> coursePage1 = new PageImpl<>(courses1, PageRequest.of(0, 5, Sort.by("id").ascending()), courses1.size());

        when(courseService.courseSearching("programming", pageable)).thenReturn(coursePage1);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("keyword","programming")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(courseService, times(1)).courseSearching("programming", pageable);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void shouldGetCourse1Successfully() {
        when(courseService.getOneCourse(1)).thenReturn(course1);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(courseService, times(1)).getOneCourse(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void getOneCourseShouldFail404NotFound() {
        when(courseService.getOneCourse(1)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}", 1))
                .andExpect(status().isNotFound());


        verify(courseService, times(1)).getOneCourse(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void deleteCourseShouldDeleteCourse1Successfully() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/courses/{id}", 1))
                .andExpect(status().isOk());

        verify(courseService, times(1)).deleteCourse(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void deleteCourseShouldFail403() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/courses/{id}", 1))
                .andExpect(status().isForbidden());

        verify(courseService, times(0)).deleteCourse(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void updateCourseShouldUpdateCourse1() {
        Course courseToUpdate = new Course();
        courseToUpdate.setName("swift course");

        mockMvc.perform(MockMvcRequestBuilders.put("/courses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(courseToUpdate)))
                .andExpect(status().isOk());

        verify(courseService).updateCourse(courseToUpdate, 1 );

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void updateCourseShouldFail404NotFound() {
        Course courseToUpdate = new Course();
        courseToUpdate.setName("swift course");

        when(courseService.updateCourse(courseToUpdate, 1)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.put("/courses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(courseToUpdate)))
                .andExpect(status().isNotFound());

        verify(courseService).updateCourse(courseToUpdate, 1 );

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void setTaskToCourseShouldSetTask1ToCourse2() {

        mockMvc.perform(MockMvcRequestBuilders.post("/courses/{id}/tasks/", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .param("taskId", "1"))
                .andExpect(status().isOk());

        verify(taskService).setTaskToCourse(2,1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void setTaskToCourseShouldFail404NotFound() {
        doThrow(NotFoundException.class)
                .when(taskService)
                .setTaskToCourse(2,1);

        mockMvc.perform(MockMvcRequestBuilders.post("/courses/{id}/tasks/", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .param("taskId", "1"))
                .andExpect(status().isNotFound());

        verify(taskService).setTaskToCourse(2,1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void getAllTasksOfCourse() {
        when(taskService.getAllTasksOfCourse(1,pageable)).thenReturn(taskPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}/tasks/", 1)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)));

        verify(taskService).getAllTasksOfCourse(1, pageable);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void getOneTaskOfCourse() {
        when(taskService.getOneTasksOfCourse(1,1)).thenReturn(task1);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}/tasks/{task1}", 1,1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(taskService).getOneTasksOfCourse(1,1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "STUDENT")
    void getAttachmentsOfCourse() {
        when(courseService.getAttachmentsOfCourse(1)).thenReturn(attachments);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}/attachments", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)));

        verify(courseService).getAttachmentsOfCourse(1);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void shouldUploadAttachments() {
        MockMultipartFile file
                = new MockMultipartFile(
                "attachments",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        System.out.println(file.getContentType());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/courses/{id}/attachments/uploading", 1)
                .file(file)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(courseService).saveAttachmentToCourse(file, 1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void downloadAttachment() {
      attachment.setDocType("text/plain");
      attachment.setData("Hello, World!".getBytes());

       when(courseService.getOneAttachmentOfCourse(1,1)).thenReturn(attachment);
       MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/courses/{id}/attachments/downloading/{attachmentId}", 1,1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk()).andReturn();

       verify(courseService).getOneAttachmentOfCourse(1,1);
       assertTrue(mvcResult.getResponse().containsHeader(CONTENT_DISPOSITION));
       assertThat(mvcResult.getResponse().getContentType(), equalTo(attachment.getDocType()));
       assertThat(mvcResult.getResponse().getContentAsByteArray().length, equalTo(attachment.getData().length));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void getTeachersOfCourseShouldGetTeachersSuccessfully() {
        when(courseService.getTeachersOfCourse(1, pageable)).thenReturn(teacherPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}/teachers", 1)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)));

        verify(courseService).getTeachersOfCourse(1, pageable);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void getTeachersOfCourseFail404NotFound() {
        when(courseService.getTeachersOfCourse(1, pageable)).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/{id}/teachers", 1)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "id,asc"))
                .andExpect(status().isNotFound());


        verify(courseService).getTeachersOfCourse(1, pageable);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "masha", password = "12345", roles = "ADMIN")
    void setTeachersToCourse() {

        mockMvc.perform(MockMvcRequestBuilders.post("/courses/{id}/teachers/", 1)
                .param("teacherId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        verify(teacherService).addTeacherToCourse(1,1);
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}