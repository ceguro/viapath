package com.challenge.viapath.repository.implementation;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.model.entities.Recipes;
import com.challenge.viapath.repository.RecipesRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipesImplementation {

    private final RecipesRepository recipesRepository;

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
                        recipe.getTitle()))
        );
    }

    public List<Recipes> getRecipes() {
        return recipesRepository.findAll();
    }

    public void updateRecipes(RecipeDTO recipes) {
        Recipes recipe = recipesRepository.findById(recipes.getId()).orElseThrow();
        recipe.setImage(recipes.getImage());
        recipe.setReadyInMinutes(recipes.getReadyInMinutes());
        recipe.setServings(recipes.getServings());
        recipe.setSourceUrl(recipes.getSourceUrl());
        recipe.setTitle(recipes.getTitle());
        recipesRepository.save(recipe);
    }

}
