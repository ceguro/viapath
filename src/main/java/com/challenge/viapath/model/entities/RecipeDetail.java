package com.challenge.viapath.model.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "recipe_details", indexes = @Index(columnList = "id" , name = "recipe_details_id_index" , unique = true))
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecipeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long recipeId;

    @Column(name = "recipe_blob", columnDefinition = "bytea")
    private byte[] recipeBlob;

}
