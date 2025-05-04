package com.accenture.booklibrary.model;

public enum Author {

    AUTHOR_CORTAZAR("Julio Cortázar"),
    AUTHOR_GARCIA_MARQUEZ("Gabriel García Márquez"),
    AUTHOR_VARGAS_LLOSA("Mario Vargas Llosa"),
    AUTHOR_DE_CERVANTES("Miguel de Cervantes"),
    AUTHOR_GALLEGOS("Rómulo Gallegos");

    private final String description;

    Author(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
