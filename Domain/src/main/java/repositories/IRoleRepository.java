package repositories;

import model.AppRoles;
import model.constants.RolesEnumeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<AppRoles, Integer> {


   Optional <AppRoles> findByName(RolesEnumeration name);
}
