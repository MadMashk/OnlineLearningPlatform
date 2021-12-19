package services;

import model.Attachment;
import model.Course;
import model.Teacher;
import model.exceptions.InputException;
import model.exceptions.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import repositories.ICourseRepository;
import repositories.ITeacherRepository;

import java.util.List;

@Service
    public class CourseService {
        @Autowired
        private ICourseRepository courseRepository;
        @Autowired ITeacherRepository teacherRepository;
        private static final Logger logger = LogManager.getLogger(CourseService.class);

    public CourseService() {
    }


    @Transactional(readOnly = true)
    public Page<Course> courseSearching(String keyword, Pageable pageable) { //поиск курса
          return  courseRepository.findAll(keyword, pageable);
    }

   @Transactional(readOnly = true)
   public Course getOneCourse(int courseId){ //получить один курс
           return courseRepository.findById(courseId)
                    .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
   }

    @Transactional
    public Course addNewCourse(Course course) {          //добавить новый курс
        Course savedCourse = courseRepository.save(course);
        logger.info("course " + savedCourse.getId() + " is created");
        return savedCourse;
    }

    @Transactional
    public Course updateCourse(Course course, int courseId) {  //обновить курс
        Course course1 = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
        if (course.getId()==null){
            course.setId(course1.getId());
        }
        if (courseNameCheck(course.getName())) {
            if (course.getName() == null) {
                course.setName(course1.getName());
            }
        }
        if(course.getAttachmentsOfCourse()==null){
            course.setAttachmentsOfCourse(course1.getAttachmentsOfCourse());
        }
        if (course.getDescription()== null){
            course.setDescription(course1.getDescription());
        }
        if (course.getPrice() == null) {
            course.setPrice(course1.getPrice());
        }
        if (course.getTeachersOfCourse() == null){
            course.setTeachersOfCourse(course1.getTeachersOfCourse());
        }
        if (course.getTasksOfCourse() == null){
            course.setTasksOfCourse(course1.getTasksOfCourse());
        }
        courseRepository.save(course);
        logger.info("course with id " + courseId + " is updated");
        return course;
    }

    @Transactional
    public void deleteCourse(int courseId) {                //удалить курс
        courseRepository.delete(courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId)));
        logger.info("course " + courseId + " is deleted");
    }


    public boolean courseNameCheck(String courseName) {     //проверка названия курса
        if (courseName.trim().length() == 0) {
           throw new InputException("the name shouldn't be empty");
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<Attachment> getAttachmentsOfCourse( Integer courseId ){
        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new NotFoundException("course " + courseId + " doesn't exist"));
        return course.getAttachmentsOfCourse();
    }

    @Transactional
    public void saveAttachmentToCourse(MultipartFile file, Integer courseId){ //добавить вложения к курсу
        String fileName = file.getOriginalFilename();
        try {
            Attachment attachment = new Attachment();
            attachment.setName(fileName);
            attachment.setDocType(file.getContentType());
            attachment.setData(file.getBytes());
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new NotFoundException("course " + courseId + " doesn't exist"));
            course.getAttachmentsOfCourse().add(attachment);
            courseRepository.save(course); //todo переделать на сохранение на диск
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("error caused by " + e);
        }

    }

    @Transactional(readOnly = true)
    public Attachment getOneAttachmentOfCourse(Integer attachmentId, Integer courseId ){ //получить вложение курса
        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new NotFoundException("course " + courseId + " doesn't exist"));
       List<Attachment> attachmentsOfCourse = course.getAttachmentsOfCourse();
        for (Attachment attachment: attachmentsOfCourse) {
            if (attachment.getId().equals(attachmentId)){
                return attachment;
            }
        }
        throw new NotFoundException("course " + courseId + " hasn't attachment " + attachmentId);
    }

    @Transactional(readOnly = true)
    public Page<Teacher> getTeachersOfCourse (int courseId, Pageable pageable)  {  //получить всех учителей определенного курса
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found with id = " + courseId));
        List<Teacher> teacherList = course.getTeachersOfCourse();
        return new PageImpl<>(teacherList, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),pageable.getSort()), teacherList.size());
    }
}
