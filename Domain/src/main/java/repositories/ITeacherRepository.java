package repositories;

import model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITeacherRepository extends JpaRepository<Teacher, Integer> {

    @Query("SELECT t FROM Teacher t where t.userName = ?1")
    Optional<Teacher> getOneByUserName(String username);
}
