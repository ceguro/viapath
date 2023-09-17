package com.challenge.viapath.service.db

import com.challenge.viapath.model.entities.Recipe
import com.challenge.viapath.model.request.RateRecipeRequest
import com.challenge.viapath.repository.implementation.RecipeImplementation
import spock.lang.Specification

class RecipeDbServiceSpec extends Specification {

    def recipeImplementation = Mock(RecipeImplementation)

    def recipeDbService = new RecipeDbService(recipeImplementation)


    def "getRecipesByIds should return a list of recipes"() {
        given:
        def recipeIds = [1L, 2L]
        def recipes = [
                new Recipe(id: 1L,
                        readyInMinutes: 1,
                        sourceUrl: "source",
                        image: 1,
                        servings: 1,
                        title: 1,),
                new Recipe(id: 2L,
                        readyInMinutes: 1,
                        sourceUrl: "source",
                        image: 1,
                        servings: 1,
                        title: 1,)
        ]

        recipeImplementation.getRecipesByIds(recipeIds) >> recipes

        when:
        def response = recipeDbService.getRecipesByIds(recipeIds)

        then:
        response.results.size() == 2
    }

    def "rateRecipe should rate a recipe"() {
        given:
        def rateRecipeRequest = new RateRecipeRequest(recipeId: 1L, rating: 4)

        when:
        recipeDbService.rateRecipe(rateRecipeRequest)

        then:
        1 * recipeImplementation.rateRecipe(rateRecipeRequest)
    }
}
