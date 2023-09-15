package com.challenge.viapath.controller

import com.challenge.viapath.service.client.RecipesClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@AutoConfigureMockMvc
@WebMvcTest(RecipesController)
class RecipesControllerSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @MockBean
    RecipesClientService recipesService

    def "when get is performed then the response has status 200 and content is as expected"() {
        given:
        def expectedResponse = ""
        recipesService.RetrieveRecipes() >> expectedResponse

        expect: "Status is 200 and the response content matches the expectation"
        mockMvc.perform(get("/v1/recipes/"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString == expectedResponse
    }
}
