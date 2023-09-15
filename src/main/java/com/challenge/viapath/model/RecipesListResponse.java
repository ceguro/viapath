package com.challenge.viapath.model;

import com.challenge.viapath.model.entities.Recipes;
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
    private List<Recipes> results;
}
