package com.challenge.viapath.service.client

import com.challenge.viapath.dto.RecipeDTO
import com.challenge.viapath.dto.RecipeSearchResponseDTO
import com.challenge.viapath.model.entities.Recipe
import com.challenge.viapath.repository.implementation.RecipeDetailImplementation
import com.challenge.viapath.repository.implementation.RecipeImplementation
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class RecipeClientServiceSpec extends Specification {

    def restTemplate = Mock(RestTemplate)
    def jmsTemplate = Mock(JmsTemplate)
    def recipeImplementation = Mock(RecipeImplementation)
    def recipeDetailImplementation = Mock(RecipeDetailImplementation)

    def recipeClientService = new RecipeClientService(
            new RestTemplateBuilder(),
            recipeImplementation,
            recipeDetailImplementation
    )

    def setup() {
        ReflectionTestUtils.setField(recipeClientService, 'restTemplate', restTemplate)
        ReflectionTestUtils.setField(recipeClientService, 'jmsTemplate', jmsTemplate)
        ReflectionTestUtils.setField(recipeClientService, 'hostSpoonacular', 'http://ejemplo.com')
        ReflectionTestUtils.setField(recipeClientService, 'spoonacularSearch', 'buscarRecetas')
        ReflectionTestUtils.setField(recipeClientService, 'spoonacularInfo', 'infoReceta')
        ReflectionTestUtils.setField(recipeClientService, 'apiKey', 'miClaveSecreta')
    }

    def "Test fetchRecipes method"() {
        given:
        def searchQuery = "Chicken"
        1 * restTemplate.getForEntity(_, String.class) >> {
            return ResponseEntity.ok("""{
            "results": [
                {
                    "id": 1,
                    "readyInMinutes": 1,
                    "sourceUrl": "source",
                    "image": "image",
                    "servings": 1,
                    "title": "title"
                },
                {
                    "id": 2,
                    "readyInMinutes": 1,
                    "sourceUrl": "source",
                    "image": "image",
                    "servings": 1,
                    "title": "title"
                }
            ],
            "baseUri": "http://example.com",
            "offset": 0,
            "number": 2,
            "totalResults": 2,
            "processingTimeMs": 100,
            "expires": 1234567890,
            "isStale": false
        }""")
        }

        def expectedResult = new RecipeSearchResponseDTO(
                results: [
                        new RecipeDTO(id: 1,
                                readyInMinutes: 1,
                                sourceUrl: "source",
                                image: "image",
                                servings: 1,
                                title: "title",),
                        new RecipeDTO(id: 2,
                                readyInMinutes: 1,
                                sourceUrl: "source",
                                image: "image",
                                servings: 1,
                                title: "title",),
                ],
                baseUri: "http://example.com",
                offset: 0,
                number: 2,
                totalResults: 2,
                processingTimeMs: 100,
                expires: 1234567890,
                isStale: false
        )

        1 * jmsTemplate.convertAndSend("RecipesQueue", _)

        when:
        def result = recipeClientService.fetchRecipes(searchQuery)

        then:
        result == expectedResult
    }

    def "Test getRecipe method"() {
        given:
        def recipeId = 1
        def responseEntity = ResponseEntity.ok("""                {
                    "id": 1,
                    "readyInMinutes": 1,
                    "sourceUrl": "source",
                    "image": 1,
                    "servings": 1,
                    "title": "title"
                }""")
        def expectedResult = new RecipeDTO(1, 1, "source", "1", 1, "title")

        1 * restTemplate.getForEntity(_, String.class) >> {
            return responseEntity
        }

        when:
        def result = recipeClientService.getRecipe(recipeId)

        then:
        result == expectedResult
    }

    def "Test fetchAndPersistRecipeDetails method"() {
        given:
        def recipeId = 1
        def recipe = new Recipe(id: 1, readyInMinutes: 1,  sourceUrl: "http://example.com/recipe", image: "image", servings: 1, title: "title")
        def recipeDetailsBlob = "Recipe Details".getBytes()

        1 * recipeImplementation.getRecipeById(recipeId) >> recipe

        1 * restTemplate.getForEntity(_, byte[].class) >> {
            return ResponseEntity.ok(recipeDetailsBlob)
        }

        when:
        recipeClientService.fetchAndPersistRecipeDetails(recipeId)

        then:
        1 * recipeDetailImplementation.saveRecipeDetail(_)
    }

}


