package repositories;

import model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Integer> {

     @Query("SELECT c FROM Course c where c.subject LIKE %?1%"
     +" OR c.name LIKE %?1%"
     +" OR c.description LIKE %?1%"
     )
     Page<Course> findAll(String keyword, Pageable pageable);
}
