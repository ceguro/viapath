package com.challenge.viapath.service;

import com.challenge.viapath.dto.RecipeDTO;
import com.challenge.viapath.dto.RecipeSearchResponseDTO;
import com.challenge.viapath.error.handle.RestTemplateResponseErrorHandler;
import com.challenge.viapath.model.entities.Recipes;
import com.challenge.viapath.repository.implementation.RecipesImplementation;
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

import java.util.List;

@Service
public class RecipesService {

    Logger logger = LoggerFactory.getLogger(RecipesService.class);

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

    private final RecipesImplementation recipesImplementation;

    @Autowired
    public RecipesService(RestTemplateBuilder restTemplateBuilder,  RecipesImplementation recipesImplementation) {
        this.recipesImplementation = recipesImplementation;
        this.restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
    }

    public RecipeSearchResponseDTO RetrieveRecipes(String searchQuery) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(buildGetAllRecipesUrl(searchQuery), String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RecipeSearchResponseDTO recipesResponse = objectMapper.readValue(response.getBody(), new TypeReference<RecipeSearchResponseDTO>() {});
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

    private String buildGetAllRecipesUrl(String searchQuery) {
        String uri = UriComponentsBuilder.fromHttpUrl(hostSpoonacular).path(spoonacularSearch)
                .queryParam("query", searchQuery).queryParam("apiKey", apiKey).build().toUriString();
        logger.debug("URI: {}", uri);
        return uri;
    }

    private String buildGetRecipeUrl(Long recipeId) {
        String uri = UriComponentsBuilder.fromHttpUrl(hostSpoonacular).path(recipeId.toString()).path(spoonacularInfo)
                .queryParam("apiKey", apiKey).build().toUriString();
        logger.debug("URI: {}", uri);
        return uri;
    }

    public List<Recipes> getAll() {
        return this.recipesImplementation.getRecipes();
    }
}
