package com.challenge.viapath.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipes", indexes = @Index(columnList = "id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    private long id;
    private int readyInMinutes;
    private String sourceUrl;
    private String image;
    private int servings;
    private String title;

    @Column(columnDefinition = "integer default 0")
    private int rating;

}
