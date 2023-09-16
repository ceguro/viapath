package com.challenge.viapath.repository.implementation;

import com.challenge.viapath.model.entities.RecipeDetail;
import com.challenge.viapath.repository.RecipeDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class RecipeDetailImplementation {

    private final RecipeDetailRepository recipeDetailRepository;;

    public RecipeDetailImplementation(RecipeDetailRepository recipeDetailRepository) {
        this.recipeDetailRepository = recipeDetailRepository;
    }

    public void saveRecipeDetail(RecipeDetail recipeDetail) {
        recipeDetailRepository.save(recipeDetail);
    }
}
