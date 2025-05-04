package com.accenture.booklibrary.model;

public enum Category {

    CATEGORY_NOVELA("Novela"),
    CATEGORY_ENSAYO("Ensayo"),
    CATEGORY_POESIA("Poesía");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
