package com.challenge.viapath.schedule;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.model.entities.Recipes;
import com.challenge.viapath.repository.implementation.RecipesImplementation;
import com.challenge.viapath.service.RecipesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleRecipesUpdate {

    Logger logger = LoggerFactory.getLogger(ScheduleRecipesUpdate.class);

    private final RecipesService recipesService;

    private final RecipesImplementation recipesImplementation;

    public ScheduleRecipesUpdate(RecipesService recipesService, RecipesImplementation recipesImplementation) {
        this.recipesService = recipesService;
        this.recipesImplementation = recipesImplementation;
    }

    //@Scheduled(cron = "1 * * * * *")
    public void updateRecipes() {
        List<Recipes> dataBaseRecipes = recipesService.getAll();
        logger.info("Updating recipes at " + System.currentTimeMillis());

        dataBaseRecipes.parallelStream()
                .forEach(recipe -> {
                    try {
                        RecipeDTO updatedRecipeData = recipesService.getRecipe(recipe.getId());
                        if (recipeHasChanged(recipe, updatedRecipeData)) {
                            logger.info("Old recipe data: " + recipe);
                            recipesImplementation.updateRecipes(updatedRecipeData);
                            logger.info("New recipe data: " + updatedRecipeData);
                        }

                    } catch (Exception e) {
                        logger.error("Error updating recipe", e);
                        throw new RuntimeException(e);
                    }
                });
        logger.info("Recipes updated at " + System.currentTimeMillis());
    }

    private boolean recipeHasChanged(Recipes recipe, RecipeDTO updatedRecipeData) {
        RecipeDTO currentRecipeData = new RecipeDTO(
                recipe.getId(),
                recipe.getReadyInMinutes(),
                recipe.getSourceUrl(),
                recipe.getImage(),
                recipe.getServings(),
                recipe.getTitle()
        );

        return !currentRecipeData.equals(updatedRecipeData);
    }
}
