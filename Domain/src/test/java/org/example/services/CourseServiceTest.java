package org.example.services;


import org.example.model.Attachment;
import org.example.model.Course;
import org.example.model.Teacher;
import org.example.model.exceptions.InputException;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.ICourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CourseServiceTest{
    @InjectMocks
    private CourseService courseService;
    @Mock
    private ICourseRepository courseRepository;
    @Mock
    private MultipartFile file;
    @Captor
    private ArgumentCaptor<Course> courseArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> courseIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> textArgumentCaptor;
    @Captor
    private ArgumentCaptor<Pageable> pageableArgumentCaptor;

    Course course;

    Attachment attachment;

    List<Attachment> attachmentList;
    List<Teacher> teacherList;

    Page<Teacher> teacherPage;
    Pageable pageable;

    @BeforeEach
    void init(){
        course=new Course();
        course.setId(1);
        course.setName("Java");

        attachment=new Attachment();
        attachment.setId(1);
        attachmentList=new ArrayList<>();
        attachmentList.add(attachment);

        teacherList=new ArrayList<>();
        teacherList.add(new Teacher());

        course.setAttachmentsOfCourse(attachmentList);
        course.setTeachersOfCourse(teacherList);

        pageable=PageRequest.of(0,5,Sort.by("id").ascending());

        teacherPage=new PageImpl<>(teacherList,PageRequest.of(0,5,Sort.by("id").ascending()),teacherList.size());
    }

    @Test
    void courseSearchingShouldSearchCourse1(){
        courseService.courseSearching("courseName",pageable);

        verify(courseRepository).findAll(textArgumentCaptor.capture(),pageableArgumentCaptor.capture());

        assertThat(textArgumentCaptor.getValue(),is("courseName"));
        assertThat(pageableArgumentCaptor.getValue(),is(pageable));
    }

    @Test
    void getOneCourseShouldReturnCourse(){
    when(courseRepository.findById(1)).thenReturn(Optional.of(course));

    Course returnedCourse=courseService.getOneCourse(1);

    assertThat(returnedCourse,is(course));
    }

    @Test
    void getOneCourseShouldThrowNotFoundException(){
        when(courseRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(()->{courseService.getOneCourse(1);}).isInstanceOf(NotFoundException.class);
    }

    @Test
    void addNewCourseShouldAddNewCourse1(){
        when(courseRepository.save(course)).thenReturn(course);

        courseService.addNewCourse(course);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue(),is(course));
    }

    @Test
    void updateCourseShouldUpdateCourse(){
        Course course2=new Course();
        course2.setName("new course");

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        courseService.updateCourse(course2,1);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue().getName(),equalTo(course2.getName()));
        assertThat(courseArgumentCaptor.getValue().getId(),is(1));
    }

    @Test
    void deleteCourseShouldDeleteCourse(){
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1);

        verify(courseRepository).delete(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue(),is(course));
    }

    @Test
    void courseNameCheckShouldCheckName(){
        Boolean returnedAnswer=courseService.courseNameCheck("name");

        assertThat(returnedAnswer,equalTo(true));
    }

    @Test
    void courseNameCheckShouldCheckNameFail(){
        assertThatThrownBy(()->{courseService.courseNameCheck("");}).isInstanceOf(InputException.class);
    }

    @Test
    void getAttachmentsOfCourse(){
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        List<Attachment> attachmentList=courseService.getAttachmentsOfCourse(1);

        verify(courseRepository).findById(courseIdArgumentCaptor.capture());

        assertThat(courseIdArgumentCaptor.getValue(),is(1));
        assertThat(attachmentList,equalTo(course.getAttachmentsOfCourse()));
    }

    @Test
    void saveAttachmentToCourse(){
        Course testCourse=new Course();
        testCourse.setId(2);
        testCourse.setAttachmentsOfCourse(new ArrayList<>());
        Attachment attachmentTest=new Attachment();
        attachmentTest.setName("testFile");

        when(courseRepository.findById(2)).thenReturn(Optional.of(testCourse));
        when(file.getOriginalFilename()).thenReturn("testFile");

        courseService.saveAttachmentToCourse(file,2);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue(),equalTo(testCourse));
        assertThat(courseArgumentCaptor.getValue().getAttachmentsOfCourse().get(0),equalTo(attachmentTest));
    }

    @Test
    void getOneAttachmentOfCourseShouldReturnOneAttachment(){
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Attachment returnedAttachment=courseService.getOneAttachmentOfCourse(1,1);

        assertThat(returnedAttachment,equalTo(attachment));
    }

    @Test
    void getOneAttachmentOfCourseShouldThrowNotFoundException(){
        assertThatThrownBy(()->{courseService.getOneAttachmentOfCourse(2,1);}).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getTeachersOfCourseShouldReturnTeachers(){
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Page<Teacher> returnedTeacherPage=courseService.getTeachersOfCourse(1,pageable);

        verify(courseRepository).findById(1);

        assertThat(returnedTeacherPage.getContent(),equalTo(teacherList));
    }
}