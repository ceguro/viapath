package com.challenge.viapath.controller;

import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.model.RecipesListResponse;
import com.challenge.viapath.service.client.RecipesClientService;
import com.challenge.viapath.service.db.RecipeDbService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/recipes")
public class RecipesController {

    Logger logger = LoggerFactory.getLogger(RecipesController.class);
    private final RecipesClientService recipesClientService;
    private final RecipeDbService recipeDbService;

    public RecipesController(RecipesClientService recipesClientService, RecipeDbService recipeDbService) {
        this.recipesClientService = recipesClientService;
        this.recipeDbService = recipeDbService;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<RecipeSearchResponseDTO> retrieveRecipes(@RequestParam(value = "query") String searchQuery) throws JsonProcessingException {
        logger.info("Getting recipes at: " + System.currentTimeMillis());
        RecipeSearchResponseDTO response = recipesClientService.RetrieveRecipes(searchQuery);
        logger.info("Finished getting recipes at: " + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/by-ids", produces = "application/json")
    public ResponseEntity<RecipesListResponse> getRecipesByIds(@RequestParam(value = "ids") List<Long> recipesIds) {
        logger.info("Getting recipes by ids at: " + System.currentTimeMillis());
        RecipesListResponse response = recipeDbService.getRecipesByIds(recipesIds);
        logger.info("Finished recipes by ids at: " + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

}
