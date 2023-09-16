package com.challenge.viapath.model.response;

import com.challenge.viapath.model.entities.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecipesListResponse {
    private List<Recipe> results;
}
