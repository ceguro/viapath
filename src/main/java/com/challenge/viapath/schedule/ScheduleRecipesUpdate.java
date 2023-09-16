package com.challenge.viapath.schedule;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.model.entities.Recipe;
import com.challenge.viapath.repository.implementation.RecipeImplementation;
import com.challenge.viapath.service.client.RecipeClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleRecipesUpdate {

    Logger logger = LoggerFactory.getLogger(ScheduleRecipesUpdate.class);

    private final RecipeClientService recipeClientService;

    private final RecipeImplementation recipeImplementation;

    public ScheduleRecipesUpdate(RecipeClientService recipeClientService, RecipeImplementation recipeImplementation) {
        this.recipeClientService = recipeClientService;
        this.recipeImplementation = recipeImplementation;
    }

    // Every hour
    @Scheduled(cron = "0 0 * * * *")
    public void updateRecipes() {
        //TODO: pagination can be implemented to avoid fetching all recipes
        List<Recipe> dataBaseRecipes = recipeClientService.getAll();
        logger.info("Updating recipes at " + System.currentTimeMillis());

        dataBaseRecipes.parallelStream()
                .forEach(recipe -> {
                    try {
                        RecipeDTO updatedRecipeData = recipeClientService.getRecipe(recipe.getId());
                        if (recipeHasChanged(recipe, updatedRecipeData)) {
                            logger.info("Old recipe data: " + recipe);
                            recipeImplementation.updateRecipes(updatedRecipeData);
                            logger.info("New recipe data: " + updatedRecipeData);
                        }

                    } catch (Exception e) {
                        logger.error("Error updating recipe", e);
                        throw new RuntimeException(e);
                    }
                });
        logger.info("Recipes updated at " + System.currentTimeMillis());
    }

    private boolean recipeHasChanged(Recipe recipe, RecipeDTO updatedRecipeData) {
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
