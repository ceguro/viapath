package com.challenge.viapath.controller;

import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.service.RecipesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/recipes")
public class RecipesController {

    Logger logger = LoggerFactory.getLogger(RecipesController.class);
    private final RecipesService recipesService;

    public RecipesController(RecipesService recipesService) {
        this.recipesService = recipesService;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<RecipeSearchResponseDTO> retrieveRecipes(@RequestParam(value = "query") String searchQuery) throws JsonProcessingException {
        logger.info("Getting all recipes at: " + System.currentTimeMillis());
        RecipeSearchResponseDTO response = recipesService.RetrieveRecipes(searchQuery);
        logger.info("Finished getting all recipes at: " + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

}
