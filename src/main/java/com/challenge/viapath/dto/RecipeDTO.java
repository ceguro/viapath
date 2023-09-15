package com.challenge.viapath.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecipeDTO implements Serializable {

    private long id;
    private int readyInMinutes;
    private String sourceUrl;
    private String image;
    private int servings;
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeDTO recipes = (RecipeDTO) o;
        return id == recipes.id && readyInMinutes == recipes.readyInMinutes && servings == recipes.servings && title.equals(recipes.title) &&  sourceUrl.equals(recipes.sourceUrl) && image.equals(recipes.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, readyInMinutes, servings, title,  sourceUrl, image);
    }


}
