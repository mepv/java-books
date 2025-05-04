package com.accenture.booklibrary.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.accenture.booklibrary.model.Author.AUTHOR_CORTAZAR;
import static com.accenture.booklibrary.model.Author.AUTHOR_DE_CERVANTES;
import static com.accenture.booklibrary.model.Author.AUTHOR_GALLEGOS;
import static com.accenture.booklibrary.model.Author.AUTHOR_GARCIA_MARQUEZ;
import static com.accenture.booklibrary.model.Author.AUTHOR_VARGAS_LLOSA;
import static com.accenture.booklibrary.model.Category.CATEGORY_ENSAYO;
import static com.accenture.booklibrary.model.Category.CATEGORY_NOVELA;
import static com.accenture.booklibrary.model.Category.CATEGORY_POESIA;

@Component
public class AccessValidation {

    public boolean hasCategoryAuthority(String category, Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ALL")
                || a.getAuthority().equals(categories().get(category)));
    }

    public boolean hasAuthorAuthority(String author, Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ALL")
                        || a.getAuthority().equals(authors().get(author)));
    }

    private Map<String, String> authors() {
        Map<String, String> authors = new HashMap<>();
        authors.put(AUTHOR_CORTAZAR.getDescription(), AUTHOR_CORTAZAR.name());
        authors.put(AUTHOR_GARCIA_MARQUEZ.getDescription(), AUTHOR_GARCIA_MARQUEZ.name());
        authors.put(AUTHOR_VARGAS_LLOSA.getDescription(), AUTHOR_VARGAS_LLOSA.name());
        authors.put(AUTHOR_DE_CERVANTES.getDescription(), AUTHOR_DE_CERVANTES.name());
        authors.put(AUTHOR_GALLEGOS.getDescription(), AUTHOR_GALLEGOS.name());
        return authors;
    }

    private Map<String, String> categories() {
        Map<String, String> categories = new HashMap<>();
        categories.put(CATEGORY_NOVELA.getDescription(), CATEGORY_NOVELA.name());
        categories.put(CATEGORY_ENSAYO.getDescription(), CATEGORY_ENSAYO.name());
        categories.put(CATEGORY_POESIA.getDescription(), CATEGORY_POESIA.name());
        return categories;
    }
}
