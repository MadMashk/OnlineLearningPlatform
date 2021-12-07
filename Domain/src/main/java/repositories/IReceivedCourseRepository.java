package repositories;

import model.ReceivedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReceivedCourseRepository extends JpaRepository<ReceivedCourse, Integer> {
}
