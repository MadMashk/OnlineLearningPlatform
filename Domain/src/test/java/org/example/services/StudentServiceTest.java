package org.example.services;


import org.example.model.*;
import org.example.model.constants.CompleteStatus;
import org.example.model.dto.StudentDTO;
import org.example.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest{

    @InjectMocks
    private StudentService studentService;
    @Mock
    private IStudentRepository studentRepository;
    @Mock
    private IAppUserRepository userRepository;
    @Mock
    private ICourseRepository courseRepository;
    @Mock
    private IReceivedCourseRepository receivedCourseRepository;
    @Mock
    private IReceivedTaskRepository receivedTaskRepository;
    @Mock
    private ITaskRepository taskRepository;
    @Captor
    private ArgumentCaptor<Student> studentArgumentCaptor;
    @Captor
    private ArgumentCaptor<ReceivedCourse> receivedCourseArgumentCaptor;
    @Captor
    private ArgumentCaptor<ReceivedTask> receivedTaskArgumentCaptor;

    Student student;
    Student student2;

    AppUser user;

    Pageable pageable;

    Course course;

    ReceivedTask receivedTask;
    ReceivedTask receivedTask2;

    ReceivedCourse receivedCourse;

    List<Student> studentList;
    List<ReceivedCourse> receivedCourseList;
    List<ReceivedTask> receivedTaskList;
    List<StudentDTO> studentDTOList;

    Page<StudentDTO> studentDTOPage;
    Page<ReceivedCourse> receivedCoursePage;

    StudentDTO studentDTO;
    StudentDTO studentDTO2;


    @BeforeEach
    void init(){
        student=new Student();
        student.setPoints((long)100);
        student.setName("Masha");
        student.setUserName("Masha");
        student.setId(1);

        student2=new Student();
        student2.setPoints((long)100);
        student2.setName("no");
        student2.setUserName("Masha");
        student2.setId(1);

        studentList=new ArrayList<>();
        studentList.add(student);

        user=new AppUser();
        user.setUserName("Masha");
        user.setEmail("myMail.@gmail.com");
        user.setId(1);

        course=new Course();
        course.setId(1);

        receivedCourse=new ReceivedCourse();
        receivedCourse.setCourse(course);
        receivedCourse.setStudent(student);
        receivedCourse.setId(1);

        receivedCourseList=new ArrayList<>();
        receivedCourseList.add(receivedCourse);

        receivedTask=new ReceivedTask();
        receivedTask.setId(1);
        receivedTask.setStudent(student);
        receivedTask.setCompleteStatus(CompleteStatus.CHECKING);

        receivedTask2=new ReceivedTask();
        receivedTask2.setId(2);
        receivedTask2.setStudent(student);
        receivedTask2.setCompleteStatus(CompleteStatus.COMPLETED);

        receivedTaskList=new ArrayList<>();
        receivedTaskList.add(receivedTask);
        receivedTaskList.add(receivedTask2);

        pageable=PageRequest.of(0,5,Sort.by("id").ascending());

        studentDTO=new StudentDTO("Masha","Masha", (long)100,"myMail.@gmail.com");
        studentDTO2=new StudentDTO("Masha","no", (long)100,"myMail.@gmail.com");

        studentDTOList=new ArrayList<>();
        studentDTOList.add(studentDTO);

        studentDTOPage=new PageImpl<>(studentDTOList,PageRequest.of(0,5,Sort.by("id").ascending()),studentDTOList.size());

        receivedCoursePage=new PageImpl<>(receivedCourseList,PageRequest.of(0,5,Sort.by("id").ascending()),receivedCourseList.size());
    }

    @Test
    void getAllStudentsShouldReturnAllStudents(){
        when(studentRepository.findAll()).thenReturn(studentList);
        when(userRepository.findByUserName("Masha")).thenReturn(Optional.of(user));

        Page<StudentDTO> studentDTOPage1=studentService.getAllStudents(pageable);

        assertThat(studentDTOPage1.getContent(),equalTo(studentDTOPage.getContent()));
    }

    @Test
    void getOneStudentShouldReturnOneStudent(){
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(userRepository.findByUserName("Masha")).thenReturn(Optional.of(user));

        StudentDTO returnedStudent=studentService.getOneStudent(1);

        assertThat(returnedStudent,equalTo(studentDTO));
    }

    @Test
    void addStudentShouldAddStudent(){
        when(studentRepository.save(student)).thenReturn(student);

        studentService.addStudent(student);

        verify(studentRepository).save(student);
    }

    @Test
    void updateStudentShouldUpdateStudent(){
        studentDTO.setName("no");
        studentDTO.setUserName(null);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(userRepository.findByUserName("Masha")).thenReturn(Optional.of(user));

        StudentDTO returnedStudentDTO=studentService.updateStudent(1,studentDTO);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        assertThat(returnedStudentDTO,equalTo(studentDTO2));
        assertThat(studentArgumentCaptor.getValue(),equalTo(student2));
    }

    @Test
    void deleteStudentShouldDeleteStudent(){
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        studentService.deleteStudent(1);

        verify(studentRepository).delete(student);
    }

    @Test
    void getOneByUserNameShouldReturnStudent(){
        when(studentRepository.getOneByUserName("Masha")).thenReturn(Optional.of(student));

        Student returnedStudent=studentService.getOneByUserName("Masha");

        assertThat(returnedStudent,equalTo(student));
    }

    @Test
    void setCourseToStudentShouldSetCourse(){
        List<ReceivedCourse> receivedCourseList=new ArrayList<>();
        ReceivedCourse receivedCourse1=new ReceivedCourse();
        receivedCourse1.setStudent(student);
        receivedCourse1.setCourse(course);
        receivedCourse1.setDateOfReceiving(new Date());
        receivedCourse1.setCompleteStatus(CompleteStatus.RECEIVED);
        receivedCourseList.add(receivedCourse1);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2)).thenReturn(Optional.of(course));
        when(receivedCourseRepository.findAll()).thenReturn(receivedCourseList);

        studentService.setCourseToStudent(2,1);

        verify(receivedCourseRepository).save(receivedCourseArgumentCaptor.capture());

        assertThat(receivedCourseArgumentCaptor.getValue(),equalTo(receivedCourse1));
    }

    @Test
    void setTaskToStudentShouldSetTask(){
        Task task=new Task();
        task.setId(1);
        ReceivedTask receivedTask1=new ReceivedTask();
        receivedTask1.setStudent(student);
        receivedTask1.setTask(task);
        receivedTask1.setDateOfReceiving(new Date());
        receivedTask1.setCompleteStatus(CompleteStatus.RECEIVED);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        studentService.setTaskToStudent(1,1);

        verify(receivedTaskRepository).save(receivedTaskArgumentCaptor.capture());

        assertThat(receivedTaskArgumentCaptor.getValue(),equalTo(receivedTask1));
    }

    @Test
    void getReceivedCoursesShouldReturnReceivedCourses(){
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(receivedCourseRepository.findAll()).thenReturn(receivedCourseList);

        Page<ReceivedCourse> receivedCoursePage=studentService.getReceivedCourses(1,pageable);

        assertThat(receivedCoursePage.getContent(),equalTo(receivedCoursePage.getContent()));
    }

    @Test
    void getReceivedTaskOfStudentShouldReturnReceivedTask(){
        when(receivedTaskRepository.findById(1)).thenReturn(Optional.of(receivedTask));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(receivedTaskRepository.findAll()).thenReturn(receivedTaskList);

        ReceivedTask returnedReceivedTask=studentService.getReceivedTaskOfStudent(1,1);

        assertThat(returnedReceivedTask,equalTo(receivedTask));
    }

    @Test
    void getAllUncompletedTasksOfStudents(){
        receivedTaskList.remove(receivedTask2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(receivedTaskRepository.findAll()).thenReturn(receivedTaskList);

        List<ReceivedTask> returnedReceivedTaskList=studentService.getAllUncompletedTasksOfStudent(1);

        assertThat(returnedReceivedTaskList,equalTo(receivedTaskList));
    }
}
