package org.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.AppUser;
import org.example.model.Course;
import org.example.model.Teacher;
import org.example.model.dto.TeacherDTO;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.IAppUserRepository;
import org.example.repositories.ICourseRepository;
import org.example.repositories.ITeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private ITeacherRepository teacherRepository;
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private IAppUserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(TaskService.class);
    public TeacherService() {

    }

    @Transactional(readOnly = true)
    public Page<TeacherDTO> getAllTeachers(Pageable pageable){ //получить всех учителей
        List <Teacher> teacherList = teacherRepository.findAll();
        List <TeacherDTO> teacherDTOS = new ArrayList<>();

        teacherList.forEach(teacher -> {
            List <Course> coursesOfTeacher = new ArrayList<>();
            courseRepository.findAll().forEach(course -> {
                if(course.getTeachersOfCourse().contains(teacher)){
                    coursesOfTeacher.add(course);
                }
            });
            AppUser currentUser = userRepository.findByUserName(teacher.getUserName())
                    .orElseThrow(()->new NotFoundException("user not found with user name " + teacher.getUserName()));
            teacherDTOS.add(new TeacherDTO(teacher.getName(),teacher.getUserName(),currentUser.getEmail(), coursesOfTeacher));

        });
        return  new PageImpl<>(teacherDTOS,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),pageable.getSort()), teacherDTOS.size());
    }


    @Transactional
    public Teacher addTeacher(Teacher teacher) { //добавить учителя
       Teacher savedTeacher = teacherRepository.save(teacher);
       logger.info("teacher "+ savedTeacher.getId() + " is created");
       return savedTeacher;
    }

    @Transactional
    public void addTeacherToCourse(int teacherId, int courseId) { //добавить учителя к курсу
       Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
       Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("teacher not found with id = " + teacherId));
        course.getTeachersOfCourse().add(teacher);
        logger.info("teacher " + teacherId + " is added to course "+ courseId);
        courseRepository.save(course);
    }

    @Transactional
    public void deleteTeacher(int teacherId){ //удалить учителя
        teacherRepository.delete(teacherRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("teacher not found with id = " + teacherId)));
        logger.info("teacher " + teacherId+ "is deleted");
    }


    @Transactional(readOnly = true)
    public TeacherDTO getOne(int teacherId) { //получить одного учителя
      Teacher teacher = teacherRepository.findById(teacherId)
               .orElseThrow(() -> new NotFoundException("teacher not found with id = " + teacherId));
        AppUser currentUser = userRepository.findByUserName(teacher.getUserName())
                .orElseThrow(()->new NotFoundException("user not found with user name " + teacher.getUserName()));
        List<Course> coursesOfTeacher = new ArrayList<>();
        courseRepository.findAll().forEach(course ->{
             course.getTeachersOfCourse().forEach(teacher1 -> {
                 if (teacher1.getId().equals(teacherId)){
                     coursesOfTeacher.add(course);
                 }
             });
        });
        return new TeacherDTO(teacher.getName(),teacher.getUserName(),currentUser.getEmail(), coursesOfTeacher);
    }

    @Transactional
    public TeacherDTO updateTeacher(int teacherId, TeacherDTO teacherDTO){ //обновить учителя
        Teacher teacherToUpdate = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("teacher not found with id = " + teacherId));
        Teacher updatedTeacher = new Teacher();
        AppUser currentUser = userRepository.findByUserName(teacherToUpdate.getUserName())
                .orElseThrow(() -> new NotFoundException("user not found with user name " + teacherToUpdate.getUserName()));
        updatedTeacher.setId(teacherId);
        if (teacherDTO.getName()==null){
           updatedTeacher.setName(teacherToUpdate.getName());
        }
        else {
            updatedTeacher.setName(teacherDTO.getName());
        }
        if (teacherDTO.getUserName()==null){
            updatedTeacher.setUserName(teacherToUpdate.getUserName());
        }
        if(teacherDTO.getEmail()!=null) {
            currentUser.setEmail(teacherDTO.getEmail());
        }

        logger.info("teacher " + teacherId + " is updated");
        logger.info("user" + currentUser.getId() + "is updated");
        teacherRepository.save(updatedTeacher);
        userRepository.save(currentUser);
        return new TeacherDTO(updatedTeacher.getName(),updatedTeacher.getUserName(),currentUser.getEmail(), null);
    }



}
