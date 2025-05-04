package com.accenture.booklibrary.config;

import com.accenture.booklibrary.model.Book;
import com.accenture.booklibrary.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static com.accenture.booklibrary.model.Author.AUTHOR_CORTAZAR;
import static com.accenture.booklibrary.model.Author.AUTHOR_DE_CERVANTES;
import static com.accenture.booklibrary.model.Author.AUTHOR_GALLEGOS;
import static com.accenture.booklibrary.model.Author.AUTHOR_GARCIA_MARQUEZ;
import static com.accenture.booklibrary.model.Author.AUTHOR_VARGAS_LLOSA;
import static com.accenture.booklibrary.model.Category.CATEGORY_ENSAYO;
import static com.accenture.booklibrary.model.Category.CATEGORY_NOVELA;
import static com.accenture.booklibrary.model.Category.CATEGORY_POESIA;

@Configuration
public class ApplicationConfig {

    @Bean
    public CommandLineRunner loadData(BookRepository bookRepository) {
        return args -> {
            Book book1 = new Book("Rayuela", AUTHOR_CORTAZAR.getDescription(), 736, CATEGORY_NOVELA.getDescription(), "Una novela innovadora que permite múltiples lecturas, cuestionando la realidad y la existencia.");
            Book book2 = new Book("Bestiario", AUTHOR_CORTAZAR.getDescription(), 150, CATEGORY_NOVELA.getDescription(), "Colección de cuentos donde lo cotidiano se mezcla con lo fantástico.");
            Book book3 = new Book("Final del juego", AUTHOR_CORTAZAR.getDescription(), 180, CATEGORY_NOVELA.getDescription(), "Cuentos que muestran la tensión entre la infancia y la adultez.");
            Book book4 = new Book("Historias de cronopios y de famas", AUTHOR_CORTAZAR.getDescription(), 160, CATEGORY_ENSAYO.getDescription(), "Textos breves, surrealistas y poéticos, con personajes únicos.");

            Book book5 = new Book("Cien años de soledad", AUTHOR_GARCIA_MARQUEZ.getDescription(), 417, CATEGORY_NOVELA.getDescription(), "Una saga familiar que mezcla realismo mágico y la historia de América Latina.");
            Book book6 = new Book("El amor en los tiempos del cólera", AUTHOR_GARCIA_MARQUEZ.getDescription(), 348, CATEGORY_NOVELA.getDescription(), "Una historia de amor que desafía al tiempo y a la muerte.");
            Book book7 = new Book("Relato de un náufrago", AUTHOR_GARCIA_MARQUEZ.getDescription(), 128, CATEGORY_ENSAYO.getDescription(), "Crónica periodística sobre la supervivencia de un marinero.");
            Book book8 = new Book("Doce cuentos peregrinos", AUTHOR_GARCIA_MARQUEZ.getDescription(), 192, CATEGORY_NOVELA.getDescription(), "Relatos breves de latinoamericanos en Europa, con toques de lo insólito.");

            Book book9 = new Book("La tía Julia y el escribidor", AUTHOR_VARGAS_LLOSA.getDescription(), 376, CATEGORY_NOVELA.getDescription(), "Una novela semiautobiográfica que mezcla ficción y vida personal.");
            Book book10 = new Book("Conversación en La Catedral", AUTHOR_VARGAS_LLOSA.getDescription(), 601, CATEGORY_NOVELA.getDescription(), "Reflexión sobre el poder y la corrupción en el Perú.");
            Book book11 = new Book("La ciudad y los perros", AUTHOR_VARGAS_LLOSA.getDescription(), 448, CATEGORY_NOVELA.getDescription(), "Retrato brutal de la vida militar y la sociedad limeña.");
            Book book12 = new Book("La verdad de las mentiras", AUTHOR_VARGAS_LLOSA.getDescription(), 304, CATEGORY_ENSAYO.getDescription(), "Ensayos literarios sobre la importancia de la ficción.");

            Book book13 = new Book("Don Quijote de la Mancha", AUTHOR_DE_CERVANTES.getDescription(), 863, CATEGORY_NOVELA.getDescription(), "Obra maestra que parodia los libros de caballería y reflexiona sobre la locura.");
            Book book14 = new Book("Novelas ejemplares", AUTHOR_DE_CERVANTES.getDescription(), 384, CATEGORY_NOVELA.getDescription(), "Colección de doce novelas breves que exploran temas variados.");
            Book book15 = new Book("La Galatea", AUTHOR_DE_CERVANTES.getDescription(), 314, CATEGORY_POESIA.getDescription(), "Novela pastoril que combina poesía con narrativa romántica.");
            Book book16 = new Book("Viaje al Parnaso", AUTHOR_DE_CERVANTES.getDescription(), 112, CATEGORY_POESIA.getDescription(), "Poema alegórico donde defiende su lugar en el mundo literario.");

            Book book17 = new Book("Doña Bárbara", AUTHOR_GALLEGOS.getDescription(), 378, CATEGORY_NOVELA.getDescription(), "Una novela de contrastes entre civilización y barbarie en los llanos venezolanos.");
            Book book18 = new Book("Canaima", AUTHOR_GALLEGOS.getDescription(), 450, CATEGORY_NOVELA.getDescription(), "Relato épico en la selva venezolana, con reflexiones sobre el hombre y la naturaleza.");
            Book book19 = new Book("La trepadora", AUTHOR_GALLEGOS.getDescription(), 320, CATEGORY_NOVELA.getDescription(), "Historia de ambición y lucha por el poder en la Venezuela rural.");
            Book book20 = new Book("Sobre la misma tierra", AUTHOR_GALLEGOS.getDescription(), 289, CATEGORY_ENSAYO.getDescription(), "Explora la identidad nacional a través de la historia de sus personajes.");

            List<Book> books = new ArrayList<>();
            books.add(book1);
            books.add(book2);
            books.add(book3);
            books.add(book4);
            books.add(book5);
            books.add(book6);
            books.add(book7);
            books.add(book8);
            books.add(book9);
            books.add(book10);
            books.add(book11);
            books.add(book12);
            books.add(book13);
            books.add(book14);
            books.add(book15);
            books.add(book16);
            books.add(book17);
            books.add(book18);
            books.add(book19);
            books.add(book20);
            bookRepository.saveAll(books);
        };
    }
}
