package com.challenge.viapath.controller;

import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.model.request.RateRecipeRequest;
import com.challenge.viapath.model.response.RecipesListResponse;
import com.challenge.viapath.service.client.RecipeClientService;
import com.challenge.viapath.service.db.RecipeDbService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/recipes")
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Recipes API (vipath)", version = "1.0", description = "Recipes API Information"))
@Validated
public class RecipesController {

    Logger logger = LoggerFactory.getLogger(RecipesController.class);
    private final RecipeClientService recipeClientService;
    private final RecipeDbService recipeDbService;

    public RecipesController(RecipeClientService recipeClientService, RecipeDbService recipeDbService) {
        this.recipeClientService = recipeClientService;
        this.recipeDbService = recipeDbService;
    }

    //TODO: Headers could be added using a swagger class ( I must read about how to implement it)
    @GetMapping(value = "/", produces = "application/json")
    @Operation(summary = "Get recipes by search query")
    public ResponseEntity<RecipeSearchResponseDTO> retrieveRecipes(@RequestHeader("x-api-key") String apiKey,
                                                                   @RequestHeader("x-api-secret") String apiSecret,
                                                                   @RequestParam(value = "query") String searchQuery) throws JsonProcessingException {
        logger.info("Starting getting recipes at: " + System.currentTimeMillis());
        RecipeSearchResponseDTO response = recipeClientService.fetchRecipes(searchQuery);
        logger.info("Finished getting recipes at: " + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/by-ids", produces = "application/json")
    @Operation(summary = "Get recipes by ids")
    public ResponseEntity<RecipesListResponse> getRecipesByIds(@RequestHeader("x-api-key") String apiKey,
                                                               @RequestHeader("x-api-secret") String apiSecret,
                                                               @RequestParam(value = "ids") List<Long> recipesIds) {
        logger.info("Starting recipes by ids at: " + System.currentTimeMillis());
        RecipesListResponse response = recipeDbService.getRecipesByIds(recipesIds);
        logger.info("Finished recipes by ids at: " + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/rate", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Rate recipe")
    public ResponseEntity<String> rateRecipe(@RequestHeader("x-api-key") String apiKey,
                                             @RequestHeader("x-api-secret") String apiSecret,
                                             @RequestBody @Valid RateRecipeRequest rateRecipeRequest) {
        logger.info("Starting rate recipe: " + System.currentTimeMillis());
        recipeDbService.rateRecipe(rateRecipeRequest);
        logger.info("Finished rate recipe: " + System.currentTimeMillis());
        return ResponseEntity.ok("OK");
    }

    // Bonus
    @GetMapping(value = "/source-url", produces = "application/json")
    @Operation(summary = "Get recipe source url")
    public ResponseEntity<String> retrieveRecipeDetails(@RequestHeader("x-api-key") String apiKey,
                                                        @RequestHeader("x-api-secret") String apiSecret,
                                                        @RequestParam(value = "id") Long recipeId) {
        logger.info("Starting processing recipe details at: " + System.currentTimeMillis());
        recipeClientService.fetchAndPersistRecipeDetails(recipeId);
        logger.info("Finished processing recipe details at: " + System.currentTimeMillis());
        return ResponseEntity.ok("OK");
    }

}
