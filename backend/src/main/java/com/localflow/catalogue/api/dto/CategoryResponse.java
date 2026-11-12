package com.localflow.catalogue.api.dto;

import com.localflow.catalogue.domain.Category;

import java.time.OffsetDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        int sortOrder,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
