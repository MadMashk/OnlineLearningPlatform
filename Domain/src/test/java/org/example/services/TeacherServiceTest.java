package org.example.services;


import org.example.model.AppUser;
import org.example.model.Course;
import org.example.model.Teacher;
import org.example.model.dto.TeacherDTO;
import org.example.repositories.IAppUserRepository;
import org.example.repositories.ICourseRepository;
import org.example.repositories.ITeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    @InjectMocks
    private TeacherService teacherService;
    @Mock
    private ITeacherRepository teacherRepository;
    @Mock
    private ICourseRepository courseRepository;
    @Mock
    private IAppUserRepository userRepository;
    @Captor
    private ArgumentCaptor<Teacher> teacherArgumentCaptor;
    @Captor
    private ArgumentCaptor<Course> courseArgumentCaptor;

    Teacher teacher;
    Teacher teacher2;

    Course course;
    Course course2;

    AppUser user;

    List<Teacher> teacherList;
    List<Course> courseList;
    List<AppUser> userList;
    List<TeacherDTO> teacherDTOList;

    Page<Teacher> page;
    Page<TeacherDTO> teacherDTOPage;

    TeacherDTO teacherDTO;

    @BeforeEach
    void init(){
        user=new AppUser();
        user.setId(1);
        user.setUserName("Masha");
        user.setEmail("myMail@gmail.com");

        userList=new ArrayList<>();
        userList.add(user);

        teacher=new Teacher();
        teacher2=new Teacher();
        teacher.setId(1);
        teacher2.setId(2);

        course=new Course();
        course.setId(1);
        course2=new Course();

        teacher.setId(1);
        teacher.setName("Masha");
        teacher.setUserName("Masha");

        teacher2.setId(2);
        teacher2.setName("Alisa");
        teacher2.setUserName("Alisa");

        teacherList=new ArrayList<>();
        teacherList.add(teacher);

        course.setTeachersOfCourse(teacherList);
        course2.setTeachersOfCourse(teacherList);

        courseList=new ArrayList<>();
        courseList.add(course);
        courseList.add(course2);

        teacherDTO=new TeacherDTO("Masha","Masha","myMail@gmail.com",courseList);

        teacherDTOList=new ArrayList<>();
        teacherDTOList.add(teacherDTO);

        page=new PageImpl<>(teacherList,PageRequest.of(0,5,Sort.by("id").ascending()),teacherList.size());
        teacherDTOPage=new PageImpl<>(teacherDTOList,PageRequest.of(0,5,Sort.by("id").ascending()),teacherDTOList.size());
    }

    @Test
    void getAllTeachersShouldGetAllTeachers(){
        when(teacherRepository.findAll()).thenReturn(teacherList);
        when(courseRepository.findAll()).thenReturn(courseList);
        when(userRepository.findByUserName("Masha")).thenReturn(Optional.of(user));

        Page<TeacherDTO> returnedTeacherDTOPage=teacherService.getAllTeachers(teacherDTOPage.getPageable());

        assertThat(returnedTeacherDTOPage.getContent(),equalTo(teacherDTOPage.getContent()));
    }

    @Test
    void addTeacherShouldAddTeacher(){
        when(teacherRepository.save(teacher)).thenReturn(teacher);

        Teacher returnedTeacher=teacherService.addTeacher(teacher);

        verify(teacherRepository).save(teacher);

        assertThat(returnedTeacher,equalTo(teacher));
    }

    @Test
    void addTeacherToCourseShouldAddTeacher2ToCourse1(){
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(2)).thenReturn(Optional.of(teacher2));

        teacherService.addTeacherToCourse(2,1);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue().getId(),equalTo(1));
        assertTrue(courseArgumentCaptor.getValue().getTeachersOfCourse().contains(teacher2));
        assertThat(courseArgumentCaptor.getValue().getTeachersOfCourse().size(),equalTo(2));
    }

    @Test
    void deleteTeacherShouldDeleteTeacher(){
        when(teacherRepository.findById(1)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(1);

        verify(teacherRepository).delete(teacherArgumentCaptor.capture());

        assertThat(teacherArgumentCaptor.getValue(),is(teacher));
    }

    @Test
    void getOneShouldGetTeacher1(){
        when(teacherRepository.findById(1)).thenReturn(Optional.of(teacher));
        when(userRepository.findByUserName("Masha")).thenReturn(Optional.of(user));
        when(courseRepository.findAll()).thenReturn(courseList);

        TeacherDTO returnedTeacherDTO=teacherService.getOne(1);

        assertThat(returnedTeacherDTO,equalTo(teacherDTO));
    }

    @Test
    void updateTeacherTestShouldUpdateTeacher1(){
        teacherDTO.setUserName(null);
        teacherDTO.setName("Test");
        TeacherDTO testTeacherDTO=new TeacherDTO("Test","Masha","myMail@gmail.com",null);

        when(teacherRepository.findById(1)).thenReturn(Optional.of(teacher));
        when(userRepository.findByUserName("Masha")).thenReturn(Optional.of(user));

        TeacherDTO returnedTeacherDTO=teacherService.updateTeacher(1,teacherDTO);

        verify(teacherRepository).save(teacherArgumentCaptor.capture());

        assertThat(returnedTeacherDTO,equalTo(testTeacherDTO));
        assertThat(teacherArgumentCaptor.getValue().getId(),is(teacher.getId()));
    }
}
