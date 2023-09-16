package com.challenge.viapath.service.client;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.error.handle.RestTemplateResponseErrorHandler;
import com.challenge.viapath.model.entities.Recipe;
import com.challenge.viapath.model.entities.RecipeDetail;
import com.challenge.viapath.repository.implementation.RecipeDetailImplementation;
import com.challenge.viapath.repository.implementation.RecipeImplementation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
public class RecipeClientService {

    Logger logger = LoggerFactory.getLogger(RecipeClientService.class);

    @Value("${api.client.spoonacular.url}")
    private String hostSpoonacular;

    @Value("${api.client.spoonacular.search}")
    private String spoonacularSearch;

    @Value("${api.client.spoonacular.information}")
    private String spoonacularInfo;

    @Value("${api.client.spoonacular.apiKey}")
    private String apiKey;

    @Autowired
    private JmsTemplate jmsTemplate;
    private final RestTemplate restTemplate;

    private final RecipeImplementation recipeImplementation;

    private final RecipeDetailImplementation  recipeDetailImplementation;

    private static final String API_KEY = "apiKey";
    private static final String QUERY_SEARCH_PARAMETER = "query";

    @Autowired
    public RecipeClientService(RestTemplateBuilder restTemplateBuilder, RecipeImplementation recipeImplementation, RecipeDetailImplementation recipeDetailRepository, RecipeDetailImplementation recipeDetailImplementation) {
        this.recipeImplementation = recipeImplementation;
        this.restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
        this.recipeDetailImplementation = recipeDetailImplementation;
    }

    public RecipeSearchResponseDTO fetchRecipes(String searchQuery) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(buildGetAllRecipesUrl(searchQuery), String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RecipeSearchResponseDTO recipesResponse = objectMapper.readValue(response.getBody(), new TypeReference<RecipeSearchResponseDTO>() {
            });
            jmsTemplate.convertAndSend("RecipesQueue", recipesResponse);
            return recipesResponse;
        } catch (Exception e) {
            logger.error("Error parsing JSON", e);
            throw e;
        }
    }

    public RecipeDTO getRecipe(Long recipeId) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(buildGetRecipeUrl(recipeId), String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), new TypeReference<RecipeDTO>() {});
        } catch (Exception e) {
            logger.error("Error parsing JSON", e);
            throw e;
        }
    }

    public List<Recipe> getAll() {
        return this.recipeImplementation.getRecipes();
    }

    public void fetchAndPersistRecipeDetails(Long recipeId) {
        Recipe recipe = this.recipeImplementation.getRecipeById(recipeId);
        byte[] recipeDetailsBlob = fetchDetailRecipe(recipe.getSourceUrl());
        RecipeDetail recipeDetail = new RecipeDetail();
        recipeDetail.setRecipeBlob(recipeDetailsBlob);
        recipeDetail.setRecipeId(recipe.getId());
        recipeDetailImplementation.saveRecipeDetail(recipeDetail);
    }

    private byte[] fetchDetailRecipe(String url) {
        return restTemplate.getForEntity(buildGetDetailRecipeUrl(url), byte[].class).getBody();
    }

    private String buildGetAllRecipesUrl(String searchQuery) {
        String uri = UriComponentsBuilder.fromHttpUrl(hostSpoonacular).path(spoonacularSearch)
                .queryParam(QUERY_SEARCH_PARAMETER, searchQuery).queryParam(API_KEY, apiKey).build().toUriString();
        logger.debug("URI: {}", uri);
        return uri;
    }

    private String buildGetRecipeUrl(Long recipeId) {
        String uri = UriComponentsBuilder.fromHttpUrl(hostSpoonacular).path(recipeId.toString()).path(spoonacularInfo)
                .queryParam(API_KEY, apiKey).build().toUriString();
        logger.debug("URI: {}", uri);
        return uri;
    }

    private String buildGetDetailRecipeUrl(String url) {
        String uri = UriComponentsBuilder.fromHttpUrl(url).build().toUriString();
        logger.debug("URI: {}", uri);
        return uri;
    }
}
