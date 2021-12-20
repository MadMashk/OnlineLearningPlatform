package org.example.repositories;

import org.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IStudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM Student s where s.userName = ?1")
    Optional<Student> getOneByUserName(String username);
}
