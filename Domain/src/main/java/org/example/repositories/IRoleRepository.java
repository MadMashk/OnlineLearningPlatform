package org.example.repositories;

import org.example.model.AppRoles;
import org.example.model.constants.RolesEnumeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<AppRoles, Integer> {


   Optional <AppRoles> findByName(RolesEnumeration name);
}
