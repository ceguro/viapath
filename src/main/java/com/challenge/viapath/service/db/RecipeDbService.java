package com.challenge.viapath.service.db;

import com.challenge.viapath.model.RecipesListResponse;
import com.challenge.viapath.model.entities.Recipes;
import com.challenge.viapath.repository.implementation.RecipesImplementation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeDbService {

    private final RecipesImplementation recipesImplementation;

    public RecipeDbService(RecipesImplementation recipesImplementation) {
        this.recipesImplementation = recipesImplementation;
    }

    public RecipesListResponse getRecipesByIds(List<Long> recipeIds) {
        List <Recipes> recipes = this.recipesImplementation.getRecipesByIds(recipeIds);
        return new RecipesListResponse(recipes);
    }
}