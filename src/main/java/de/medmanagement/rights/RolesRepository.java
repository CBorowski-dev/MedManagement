package de.medmanagement.rights;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String rolename);
}