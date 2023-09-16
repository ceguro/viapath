package com.challenge.viapath.repository.implementation;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.error.exception.NotFoundException;
import com.challenge.viapath.model.entities.Recipe;
import com.challenge.viapath.model.request.RateRecipeRequest;
import com.challenge.viapath.repository.RecipesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeImplementation {

    Logger logger = LoggerFactory.getLogger(RecipeImplementation.class);

    private final RecipesRepository recipesRepository;

    private final int DEFAULT_RATING = 0;

    public RecipeImplementation(RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    @JmsListener(destination = "RecipesQueue")
    public void saveRecipes(RecipeSearchResponseDTO recipes) {
        recipes.getResults().forEach(
                recipe -> recipesRepository.save(new Recipe(recipe.getId(),
                        recipe.getReadyInMinutes(),
                        recipe.getSourceUrl(),
                        recipe.getImage(),
                        recipe.getServings(),
                        recipe.getTitle(),
                        DEFAULT_RATING))
        );
    }

    public List<Recipe> getRecipes() {
        return recipesRepository.findAll();
    }

    public void updateRecipes(RecipeDTO recipeDto) {
        Recipe recipe = recipesRepository.findById(recipeDto.getId()).orElseThrow(() -> {
                    logger.error("A  recipe with the id " + recipeDto.getId() + " was not found");
            return null;
        });
        recipe.setImage(recipeDto.getImage());
        recipe.setReadyInMinutes(recipeDto.getReadyInMinutes());
        recipe.setServings(recipeDto.getServings());
        recipe.setSourceUrl(recipeDto.getSourceUrl());
        recipe.setTitle(recipeDto.getTitle());
        recipesRepository.save(recipe);
    }

    public List<Recipe> getRecipesByIds(List<Long> recipesIds) {
        return recipesRepository.findAllById(recipesIds);
    }

    public Recipe getRecipeById(Long recipesId) {
        return recipesRepository.findById(recipesId).orElseThrow(() -> {
            logger.error("A  recipe with the id " + recipesId + " was not found");
            return new NotFoundException("A  recipe with the id " + recipesId + " was not found");
        });
    }

    public void rateRecipe(RateRecipeRequest rateRecipeRequest) {
        Recipe recipe = recipesRepository.findById(rateRecipeRequest.getRecipeId())
                .orElseThrow(() -> {
                    logger.error("A  recipe with the id " + rateRecipeRequest.getRecipeId() + " was not found");
                    return new NotFoundException("A  recipe with the id " + rateRecipeRequest.getRecipeId() + " was not found");
                });
        recipe.setRating(rateRecipeRequest.getRating());
        recipesRepository.save(recipe);
    }
}
