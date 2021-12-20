package org.example.services;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.*;
import org.example.model.constants.CompleteStatus;
import org.example.model.dto.StudentDTO;
import org.example.model.exceptions.AlreadyExistsException;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private ITaskRepository taskRepository;
    @Autowired
    private IReceivedTaskRepository receivedTaskRepository;
    @Autowired
    private IReceivedCourseRepository receivedCourseRepository;
    @Autowired
    private IAppUserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(StudentService.class);

    public StudentService() {
    }

    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudents(Pageable pageable) { //получить всех учеников
        List<Student> studentList = studentRepository.findAll();
        List<StudentDTO> studentDTOS = new ArrayList<>();
        studentList.forEach(student -> {
            AppUser currentUser = userRepository.findByUserName(student.getUserName())
                    .orElseThrow(() -> new NotFoundException("user not found with user name " + student.getUserName()));
            studentDTOS.add(new StudentDTO(student.getUserName(), student.getName(), student.getPoints(), currentUser.getEmail()));
        });
        return new PageImpl<>(studentDTOS, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),pageable.getSort()), studentDTOS.size());
    }

    @Transactional(readOnly = true)
    public StudentDTO getOneStudent(int studentId) { //получить одного ученика
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student not found with id = " + studentId));
        AppUser currentUser = userRepository.findByUserName(student.getUserName())
                .orElseThrow(()->new NotFoundException("user not found with user name " + student.getUserName()));
        return new StudentDTO(student.getUserName(),student.getName(),student.getPoints(), currentUser.getEmail());
    }

    @Transactional
    public Student addStudent(Student student){ //создать ученика
        Student savedStudent = studentRepository.save(student);
        logger.info("student " + savedStudent.getId() + " is created");
        return student;
    }

    @Transactional
    public StudentDTO updateStudent(int id, StudentDTO studentDTO){ //обновить ученика
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("student not found with id = " + id));
        Student updatedStudent =  new Student();
        updatedStudent.setId(id);
        AppUser currentUser = userRepository.findByUserName(oldStudent.getUserName())
                .orElseThrow(() -> new NotFoundException("user not found with user name " + oldStudent.getUserName()));

        if (studentDTO.getName() == null){
            updatedStudent.setName(oldStudent.getName());
        }
        else {
            updatedStudent.setName(studentDTO.getName());
        }
        if (studentDTO.getUserName()==null){
            updatedStudent.setUserName(oldStudent.getUserName());
        }
        if (studentDTO.getPoints()==null){
            updatedStudent.setPoints(oldStudent.getPoints());
        }
        else {
            updatedStudent.setPoints(studentDTO.getPoints());
        }
        if(studentDTO.getEmail()!=null) {
            currentUser.setEmail(studentDTO.getEmail());
        }
        studentRepository.save(updatedStudent);
        userRepository.save(currentUser);
        logger.info("student " + id + " is updated");
        logger.info("user" + currentUser.getId() + "is updated");
        return new StudentDTO(updatedStudent.getUserName(),updatedStudent.getName(),updatedStudent.getPoints(),currentUser.getEmail());
    }

    @Transactional
    public void deleteStudent(int id){     //удалить ученика
        studentRepository.delete(studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("student not found with id = " + id)));
        logger.info("Student " + id + " is deleted");
    }

    @Transactional(readOnly = true)
    public Student getOneByUserName(String userName){
        return studentRepository.getOneByUserName(userName)
                .orElseThrow(() -> new NotFoundException("student not found with user name = " + userName));
    }

    @Transactional
    public ReceivedCourse setCourseToStudent(int courseId, int studentId) { //добавление курса ученику
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student not found with id = " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
        for (int i = 0; i < receivedCourseRepository.findAll().size(); i++) {
            if(receivedCourseRepository.findAll().get(i).getStudent().getId()==studentId
                    & receivedCourseRepository.findAll().get(i).getCourse().getId()==courseId){
                throw new AlreadyExistsException("course " + courseId + " is already added to student "+ studentId);
            }
        }
        ReceivedCourse receivedCourse = new ReceivedCourse();
        receivedCourse.setCourse(course);
        receivedCourse.setStudent(student);
        receivedCourse.setDateOfReceiving(new Date());
        receivedCourse.setCompleteStatus(CompleteStatus.RECEIVED);
        receivedCourseRepository.save(receivedCourse);
        logger.info("received course is created, course " + courseId + " is given to student " + studentId) ;
        return receivedCourse;
    }

    @Transactional
    public ReceivedTask setTaskToStudent(int taskId, int studentId) { //добавление задания ученику
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + taskId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student not found with id = " +studentId));
        ReceivedTask receivedTask = new ReceivedTask();
        receivedTask.setStudent(student);
        receivedTask.setTask(task);
        receivedTask.setDateOfReceiving(new Date());
        receivedTask.setCompleteStatus(CompleteStatus.RECEIVED);
        receivedTaskRepository.save(receivedTask);
        logger.info("received task is created, task " + taskId + " is given to student " + studentId) ;
        return receivedTask;
    }

    @Transactional(readOnly = true)
    public Page<ReceivedCourse> getReceivedCourses(int studentId, Pageable pageable) { //все  курсы ученика
        List<ReceivedCourse> receivedCourses = new ArrayList<>();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student not found with id = " +studentId));
        for (int i = 0; i < receivedCourseRepository.findAll().size(); i++) {
            if(receivedCourseRepository.findAll().get(i).getStudent()==student){
                receivedCourses.add(receivedCourseRepository.findAll().get(i));
            }
        }
        return new PageImpl<>(receivedCourses, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),pageable.getSort()), receivedCourses.size());
    }

    @Transactional(readOnly = true)
    public ReceivedTask getReceivedTaskOfStudent(int studentId, int receivedTaskId) { //все полученные задания ученика
        ReceivedTask receivedTask = receivedTaskRepository.findById(receivedTaskId)
                .orElseThrow(() -> new NotFoundException("received task not found with id = " + receivedTaskId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student task not found with id = " + studentId));
        for (int i = 0; i < receivedTaskRepository.findAll().size(); i++) {
            if(receivedTaskRepository.findAll().get(i).getStudent()==student & receivedTaskRepository.findAll().get(i)==receivedTask){
                return receivedTaskRepository.findAll().get(i);
            }
        }
       throw new NotFoundException("student hasn't received task " + receivedTaskId);
    }

    @Transactional(readOnly = true)
    public List<ReceivedTask> getAllUncompletedTasksOfStudent(int studentId) { //все незавершенные задания ученика
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student task not found with id = " + studentId));
        List<ReceivedTask> uncompletedTasks = new ArrayList<>();
        for (int i = 0; i < receivedTaskRepository.findAll().size(); i++) {
            if (receivedTaskRepository.findAll().get(i).getStudent() == student & receivedTaskRepository.findAll().get(i).getCompleteStatus().equals(CompleteStatus.RECEIVED) ||  receivedTaskRepository.findAll().get(i).getCompleteStatus().equals(CompleteStatus.CHECKING) ) {
                uncompletedTasks.add(receivedTaskRepository.findAll().get(i));
            }
        }
        return uncompletedTasks;
    }
}

