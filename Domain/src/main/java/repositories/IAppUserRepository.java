package repositories;

import model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IAppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByUserName (String userName);

    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);
}
