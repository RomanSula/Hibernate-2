package com.javarush.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {
    @Override
    public String convertToDatabaseColumn(Rating rating) {
        return rating.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(String s) {
        Rating[] values = Rating.values();
        for (Rating rating : values) {
            if (rating.getValue().equals(s)) {
                return rating;
            }
        }
        return null;
    }
}
