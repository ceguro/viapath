package com.challenge.viapath.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipes", indexes = @Index(columnList = "id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipes {

    @Id
    private long id;
    private int readyInMinutes;
    private String sourceUrl;
    private String image;
    private int servings;
    private String title;

}
