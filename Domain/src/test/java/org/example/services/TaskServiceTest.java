package org.example.services;


import org.example.model.Course;
import org.example.model.ReceivedTask;
import org.example.model.Student;
import org.example.model.Task;
import org.example.model.constants.CompleteStatus;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.ICourseRepository;
import org.example.repositories.IReceivedTaskRepository;
import org.example.repositories.IStudentRepository;
import org.example.repositories.ITaskRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;
    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private ICourseRepository courseRepository;
    @Mock
    private IReceivedTaskRepository receivedTaskRepository;
    @Mock
    private IStudentRepository studentRepository;
    @Captor
    private ArgumentCaptor<Course>  courseArgumentCaptor;

    Task task;
    Task task2;

    Course course;

    ReceivedTask receivedTask;

    List<Task> taskList;
    List<Course> courseList;

    Student student;

    @BeforeEach
    void init(){
        task=new Task();
        task.setId(1);
        task.setPointsForCompletion(5);

        task2=new Task();
        task2.setId(2);
        task2.setName("subj");

        taskList=new ArrayList<>();
        taskList.add(task);
        taskList.add(task2);

        course=new Course();
        course.setId(1);
        course.setTasksOfCourse(taskList);

        courseList=new ArrayList<>();
        courseList.add(course);

        student=new Student();
        student.setId(1);

        receivedTask=new ReceivedTask();
        receivedTask.setId(1);
        receivedTask.setStudent(student);
        receivedTask.setTask(task);
    }

    @Test
    void addTaskShouldAddTask(){
        when(taskRepository.save(task)).thenReturn(task);

        taskService.addTask(task);

        verify(taskRepository).save(task);
    }

    @Test
    void deleteTaskShouldDeleteTask(){
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.deleteTask(1);

        verify(taskRepository).delete(task);
    }

    @Test
    void getOneTaskShouldReturnTask(){
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        Task returnedTask=taskService.getOneTask(1);

        assertThat(returnedTask,equalTo(task));
    }

    @Test
    void getOneTaskShouldThrowNouFoundException(){
        assertThatThrownBy(()->{taskService.getOneTask(2);}).isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateTaskShouldUpdateTask(){
        Task waitingTask=new Task();
        waitingTask.setId(2);
        waitingTask.setName("subj");
        waitingTask.setPointsForCompletion(5);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(task2)).thenReturn(task2);

        Task returnedTask=taskService.updateTask(1,task2);

        assertThat(returnedTask,equalTo(waitingTask));
    }

    @Test
    void getAllTaskShouldReturnTasks(){
        Page<Task> taskPage=new PageImpl<>(taskList,PageRequest.of(0,5,Sort.by("id").ascending()),taskList.size());

        when(courseRepository.findAll()).thenReturn(courseList);

        Page<Task> returnedPageTask=taskService.getAllTasksOfCourse(1,PageRequest.of(0,5,Sort.by("id").ascending()));

        assertThat(returnedPageTask.getContent(),equalTo(taskPage.getContent()));
    }

    @Test
    void getAllTasksOfCourseShouldGetAllTask(){ List<Course> courses=new ArrayList<>();
        Pageable pageable= PageRequest.of(0,5, Sort.by("id").ascending());
        Course course2=new Course();
        Course course3=new Course();
        Course course4=new Course();
        course2.setId(2);
        course3.setId(3);
        course4.setId(4);
        course2.setTasksOfCourse(taskList);
        courses.add(course2);
        courses.add(course3);
        courses.add(course4);

        when(courseRepository.findAll()).thenReturn(courses);

        Page<Task> returnedTaskPage=taskService.getAllTasksOfCourse(2,pageable);

        assertThat(returnedTaskPage.getContent(),equalTo(taskList));
    }

    @Test
    void getOneTaskOfCourseShouldGetOneTask(){
        List<Course> courseList=new ArrayList<>();
        Course course=new Course();
        courseList.add(course);
        courseList.add(course);
        courseList.add(course);
        course.setTasksOfCourse(taskList);

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(courseRepository.findAll()).thenReturn(courseList);

        Task returnedTask=taskService.getOneTasksOfCourse(1,1);
        assertThat(returnedTask,equalTo(task));
    }

    @Test
    void setTaskToCourseShouldSetTask(){
        List<Task> tasks=new ArrayList<>();
        Task task2=new Task();
        tasks.add(task);
        tasks.add(task2);
        course.setTasksOfCourse(tasks);

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.setTaskToCourse(1,1);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue(),equalTo(course));
    }

    @Test
    void getTaskToCheckShouldReturnTask(){
        receivedTask.setCompleteStatus(CompleteStatus.CHECKING);

        when(receivedTaskRepository.findById(1)).thenReturn(Optional.of(receivedTask));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(receivedTaskRepository.save(receivedTask)).thenReturn(receivedTask);

        ReceivedTask savedReceivedTask=taskService.getTaskToCheck(1,1);

        assertThat(receivedTask,equalTo(savedReceivedTask));
    }

    @Test
    void completeTaskShouldComplete(){
        receivedTask.setCompleteStatus(CompleteStatus.COMPLETED);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(receivedTaskRepository.findById(1)).thenReturn(Optional.of(receivedTask));

        ReceivedTask returnedReceivedTask=taskService.completeTask(1,1);

        verify(studentRepository).save(student);
        verify(receivedTaskRepository).save(receivedTask);

        assertThat(returnedReceivedTask.getCompleteStatus(),equalTo(CompleteStatus.COMPLETED));
        assertThat(returnedReceivedTask,equalTo(receivedTask));
    }
}
