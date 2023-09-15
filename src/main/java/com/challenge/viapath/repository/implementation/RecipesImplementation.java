package com.challenge.viapath.repository.implementation;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.error.exception.NotFoundException;
import com.challenge.viapath.model.entities.Recipes;
import com.challenge.viapath.model.request.RateRecipeRequest;
import com.challenge.viapath.repository.RecipesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipesImplementation {

    Logger logger = LoggerFactory.getLogger(RecipesImplementation.class);

    private final RecipesRepository recipesRepository;

    private final int DEFAULT_RATING = 0;

    public RecipesImplementation(RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    @JmsListener(destination = "RecipesQueue")
    public void saveRecipes(RecipeSearchResponseDTO recipes) {
        recipes.getResults().forEach(
                recipe -> recipesRepository.save(new Recipes(recipe.getId(),
                        recipe.getReadyInMinutes(),
                        recipe.getSourceUrl(),
                        recipe.getImage(),
                        recipe.getServings(),
                        recipe.getTitle(),
                        DEFAULT_RATING))
        );
    }

    public List<Recipes> getRecipes() {
        return recipesRepository.findAll();
    }

    public void updateRecipes(RecipeDTO recipeDto) {
        Recipes recipe = recipesRepository.findById(recipeDto.getId()).orElseThrow(() -> {
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

    public List<Recipes> getRecipesByIds(List<Long> recipesIds) {
        return recipesRepository.findAllById(recipesIds);
    }

    public void rateRecipe(RateRecipeRequest rateRecipeRequest) {
        Recipes recipe = recipesRepository.findById(rateRecipeRequest.getRecipeId())
                .orElseThrow(() -> {
                    logger.error("A  recipe with the id " + rateRecipeRequest.getRecipeId() + " was not found");
                    return new NotFoundException("A  recipe with the id " + rateRecipeRequest.getRecipeId() + " was not found");
                });
        recipe.setRating(rateRecipeRequest.getRating());
        recipesRepository.save(recipe);
    }

}
