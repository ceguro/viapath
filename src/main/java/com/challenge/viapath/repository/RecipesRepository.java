package com.challenge.viapath.repository;

import com.challenge.viapath.model.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipesRepository extends JpaRepository<Recipe, Long> {}
