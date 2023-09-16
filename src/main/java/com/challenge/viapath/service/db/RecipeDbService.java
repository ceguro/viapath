package com.challenge.viapath.service.db;

import com.challenge.viapath.model.response.RecipesListResponse;
import com.challenge.viapath.model.entities.Recipe;
import com.challenge.viapath.model.request.RateRecipeRequest;
import com.challenge.viapath.repository.implementation.RecipeImplementation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeDbService {

    private final RecipeImplementation recipeImplementation;

    public RecipeDbService(RecipeImplementation recipeImplementation) {
        this.recipeImplementation = recipeImplementation;
    }

    public RecipesListResponse getRecipesByIds(List<Long> recipeIds) {
        List <Recipe> recipes = this.recipeImplementation.getRecipesByIds(recipeIds);
        return new RecipesListResponse(recipes);
    }

    public void rateRecipe(RateRecipeRequest rateRecipeRequest) {
        this.recipeImplementation.rateRecipe(rateRecipeRequest);
        return;
    }
}