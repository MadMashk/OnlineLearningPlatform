package services;


import model.Course;
import model.ReceivedTask;
import model.Student;
import model.Task;
import model.constants.CompleteStatus;
import model.exceptions.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repositories.ICourseRepository;
import repositories.IReceivedTaskRepository;
import repositories.IStudentRepository;
import repositories.ITaskRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    ITaskRepository taskRepository;
    @Autowired
    ICourseRepository courseRepository;
    @Autowired
    IStudentRepository studentRepository;
    @Autowired
    IReceivedTaskRepository receivedTaskRepository;

    private static final Logger logger = LogManager.getLogger(TaskService.class);

    public TaskService() {
    }

    public Task addTask(Task task) { //добавить задание
        Task savedTask = taskRepository.save(task);
        logger.info("task " + savedTask.getId() + " is created");
        return savedTask;
    }

    public void deleteTask(int id) { //удалить задание
        taskRepository.delete(taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + id)));
        logger.info("task " + id + " is deleted");
    }


    public Task getOneTask(int taskId) { //получить определенное задание
       return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + taskId));
    }


    public Task updateTask(int taskId, Task task) { //обновить задание
        Task taskToUpdate = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + taskId));
        if (task.getId()==null){
            task.setId(taskToUpdate.getId());
        }
        if (task.getName() == null) {
            task.setName(taskToUpdate.getName());
        }
        if (task.getDescription() == null) {
            task.setDescription(taskToUpdate.getDescription());
        }
        if (task.getPointsForCompletion() == null) {
            task.setPointsForCompletion(taskToUpdate.getPointsForCompletion());
        }
        logger.info("task " + taskId + " is updated");
        return taskRepository.save(task);
    }


    public Page<Task> getAllTasksOfCourse(int courseId, Pageable pageable) { //все задания курса
        List<Task> tasksOfCourse = new ArrayList<>();
        for (int i = 0; i < courseRepository.findAll().size(); i++) {
            if (courseRepository.findAll().get(i).getId() == courseId) {
                tasksOfCourse = courseRepository.findAll().get(i).getTasksOfCourse();
            }
        }
        return new PageImpl<>(tasksOfCourse, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),pageable.getSort()), tasksOfCourse.size());
    }


    public Task getOneTasksOfCourse(int courseId, int taskId) { //одно задание курса
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + taskId));
        for (int i = 0; i < courseRepository.findAll().size(); i++) {
            if (courseRepository.findAll().get(i) == course) {
                for (int j = 0; j < courseRepository.findAll().get(i).getTasksOfCourse().size(); j++) {
                    if (courseRepository.findAll().get(i).getTasksOfCourse().get(j)==task)
                    return courseRepository.findAll().get(i).getTasksOfCourse().get(j);
                }
            }
        }
        throw new NotFoundException("course "+ courseId+ " hasn't task " + taskId);
    }


    public void setTaskToCourse(int courseId, int taskId) {  //добовить задание в курс
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + taskId));
        course.getTasksOfCourse().add(task);
        logger.info("task "+ taskId +" is added to course "+ courseId);
        courseRepository.save(course);
    }


    public ReceivedTask getTaskToCheck(int receivedTaskId, int studentId) { //сдать задание на провеку
        ReceivedTask receivedTask = receivedTaskRepository.findById(receivedTaskId)
                .orElseThrow(() -> new NotFoundException("received task not found with id = " + receivedTaskId));
        studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student task not found with id = " + studentId));
        if(!receivedTask.getStudent().getId().equals(studentId)){
            throw new NotFoundException("student "+ studentId + " hasn't a received task " + receivedTaskId);
        }
        receivedTask.setCompleteStatus(CompleteStatus.CHECKING);
        receivedTaskRepository.save(receivedTask);
        logger.info("received task" + receivedTask + "is checking");
        return receivedTask;
    }


    public ReceivedTask completeTask(int studentId, int receivedTaskId) { //завершить задание
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student task not found with id = " + studentId));
        ReceivedTask receivedTask = receivedTaskRepository.findById(receivedTaskId)
                .orElseThrow(() -> new NotFoundException("received task not found with id = " + receivedTaskId));
        if(!receivedTask.getStudent().getId().equals(studentId)){
            throw new NotFoundException("student "+ studentId + " hasn't a received task " + receivedTaskId);
        }
        receivedTask.setCompleteStatus(CompleteStatus.COMPLETED);
        student.setPoints((long) receivedTask.getTask().getPointsForCompletion());
        receivedTaskRepository.save(receivedTask);
        studentRepository.save(student);
        logger.info("received task " + receivedTaskId + " is completed");
        return receivedTask;
    }



}