package com.javarush.domain;

import static java.util.Objects.isNull;

public enum Feature {
    TRAILERS("Trailers"),
    COMMENTARIES("Commentaries"),
    DELETED_SCENES("Deleted Scenes"),
    BEHIND_THE_SCENES("Behind the Scenes");
    private final String value;

    Feature(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Feature featureByValue(String value){
        if (isNull(value) || value.isEmpty()){
            return null;
        }
        for (Feature feature : Feature.values()) {
            if (feature.value.equals(value)) {
                return feature;
            }
        }
        return null;
    }
}
