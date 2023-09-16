package com.challenge.viapath.service.client

import com.challenge.viapath.dto.RecipeDTO
import com.challenge.viapath.dto.RecipeSearchResponseDTO
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
                    "id": "1",
                    "readyInMinutes": 1,
                    "sourceUrl": "source",
                    "image": 1,
                    "servings": 1,
                    "title": 1
                },
                {
                    "id": "1",
                    "readyInMinutes": 1,
                    "sourceUrl": "source",
                    "image": 1,
                    "servings": 1,
                    "title": 1
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
                                image: 1,
                                servings: 1,
                                title: 1,),
                        new RecipeDTO(id: 1,
                                readyInMinutes: 1,
                                sourceUrl: "source",
                                image: 1,
                                servings: 1,
                                title: 1,),
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

   /* def "Test getRecipe method"() {
        given:
        def recipeId = 1
        def objectMapper = Mock(ObjectMapper)
        def responseEntity = ResponseEntity.ok("""{
        "name": "Chicken Curry",
        "id": 1
    }""")
        def expectedResult = new RecipeDTO(1, 1, "", "", 1, 1)

        when:
        restTemplate.getForEntity(_, String.class) >> {
            return responseEntity
        }

        objectMapper.readValue(_, _) >> {
            return expectedResult
        }

        def result = recipeClientService.getRecipe(recipeId)

        then:
        1 * restTemplate.getForEntity(_, String.class)
        result.name == "Chicken Curry"
        result == expectedResult
    }

    def "Test fetchAndPersistRecipeDetails method"() {
        given:
        def recipeId = 1
        def recipe = new Recipe(id: 1, sourceUrl: "http://example.com/recipe")
        def recipeDetailsBlob = "Recipe Details".getBytes()

        when:
        recipeImplementation.getRecipeById(recipeId) >> recipe
        restTemplate.getForEntity(_, byte[].class) >> {
            return ResponseEntity.ok(recipeDetailsBlob)
        }

        recipeClientService.fetchAndPersistRecipeDetails(recipeId)

        then:
        1 * recipeImplementation.getRecipeById(recipeId)
        1 * restTemplate.getForEntity(_, byte[].class)
        1 * recipeDetailImplementation.saveRecipeDetail(_)
    }
*/
}


