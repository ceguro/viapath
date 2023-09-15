package com.challenge.viapath.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class RecipesRequest {
    private List<Long> ids;
}
