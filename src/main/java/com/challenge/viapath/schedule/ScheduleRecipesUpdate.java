package com.challenge.viapath.schedule;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.model.entities.Recipes;
import com.challenge.viapath.repository.implementation.RecipesImplementation;
import com.challenge.viapath.service.client.RecipesClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleRecipesUpdate {

    Logger logger = LoggerFactory.getLogger(ScheduleRecipesUpdate.class);

    private final RecipesClientService recipesClientService;

    private final RecipesImplementation recipesImplementation;

    public ScheduleRecipesUpdate(RecipesClientService recipesClientService, RecipesImplementation recipesImplementation) {
        this.recipesClientService = recipesClientService;
        this.recipesImplementation = recipesImplementation;
    }

    //@Scheduled(cron = "1 * * * * *")
    public void updateRecipes() {
        List<Recipes> dataBaseRecipes = recipesClientService.getAll();
        logger.info("Updating recipes at " + System.currentTimeMillis());

        dataBaseRecipes.parallelStream()
                .forEach(recipe -> {
                    try {
                        RecipeDTO updatedRecipeData = recipesClientService.getRecipe(recipe.getId());
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
