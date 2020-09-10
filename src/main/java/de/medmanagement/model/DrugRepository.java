package de.medmanagement.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called drugRepository
// CRUD refers Create, Read, Update, Delete

public interface DrugRepository extends JpaRepository<Drug, Integer> {
    // List<Drug> findAllByUserId(Integer userId);
    List<Drug> findAllByUserName(String userName);
}
