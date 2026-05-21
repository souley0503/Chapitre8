package bookstoread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Spécifications de la bibliothèque (BookShelf)")
public class BookShelfSpec {

    private BookShelf shelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;
    private Book cleanCode;

    @BeforeEach
    void init() throws Exception {
        shelf = new BookShelf();

        effectiveJava = new Book("Effective Java", "Joshua Bloch",
                LocalDate.of(2008, Month.MAY, 8));
        codeComplete = new Book("Code Complete", "Steve McConnel",
                LocalDate.of(2004, Month.JUNE, 9));
        mythicalManMonth = new Book("The Mythical Man-Month",
                "Frederick Phillips Brooks", LocalDate.of(1975, Month.JANUARY, 1));
        cleanCode = new Book("Clean Code", "Robert C. Martin",
                LocalDate.of(2008, Month.AUGUST, 1));
    }

    @Nested
    @DisplayName("Est vide")
    class IsEmpty {
        @Test
        @DisplayName("Quand aucun livre n'y est ajouté")
        public void emptyBookShelfWhenNoBookAdded() {
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf devrait être vide.");
        }

        @Test
        @DisplayName("Quand add est appelé sans livres")
        void emptyBookShelfWhenAddIsCalledWithoutBooks() {
            shelf.add();
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf devrait être vide.");
        }
    }

    @Nested
    @DisplayName("Après avoir ajouté des livres")
    class BooksAreAdded {

        @Test
        @DisplayName("Contient deux livres si deux livres sont ajoutés")
        void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            assertEquals(2, books.size(), () -> "BookShelf devrait contenir deux livres.");
        }

        @Test
        @DisplayName("Renvoie au client une collection de livres immuable")
        void booksReturnedFromBookShelfIsImmutableForClient() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            try {
                books.add(mythicalManMonth);
                fail(() -> "Le client ne devrait pas pouvoir modifier la liste directement.");
            } catch (UnsupportedOperationException e) {
                // Succès attendu
            }
        }
    }

    @Nested
    @DisplayName("Organisation et tris")
    class TriEtOrganisation {

        @Test
        @DisplayName("La bibliothèque est organisée lexicographiquement par titre de livre")
        void bookshelfArrangedByBookTitle() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange();
            assertThat(books).containsExactly(codeComplete, effectiveJava, mythicalManMonth);
        }

        @Test
        @DisplayName("Les livres restent dans l'ordre d'insertion d'origine après un appel à arrange")
        void booksInBookShelfAreInInsertionOrderAfterCallingArrange() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            shelf.arrange();
            List<Book> books = shelf.books();
            assertThat(books).containsExactly(effectiveJava, codeComplete, mythicalManMonth);
        }

        @Test
        @DisplayName("Les livres sont organisés selon un critère personnalisé fourni par l'utilisateur (Ordre inverse)")
        void bookshelfArrangedByUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange(Comparator.<Book>naturalOrder().reversed());
            assertThat(books).containsExactly(mythicalManMonth, effectiveJava, codeComplete);
        }

        @Test
        @DisplayName("Exercice 1 : Les livres sont triés par date de publication chronologique")
        void bookshelfArrangedByPublicationDate() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange(Comparator.comparing(Book::getPublishedOn));
            assertThat(books).containsExactly(mythicalManMonth, codeComplete, effectiveJava);
        }
    }

    @Nested
    @DisplayName("Regroupement de livres")
    class GroupementDeLivres {

        @Test
        @DisplayName("Les livres à l'intérieur de la bibliothèque sont regroupés par année de publication")
        void groupBooksInsideBookShelfByPublicationYear() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
            Map<Year, List<Book>> booksByPublicationYear = shelf.groupByPublicationYear();

            assertAll(
                    () -> assertThat(booksByPublicationYear).containsKey(Year.of(2008)),
                    () -> assertThat(booksByPublicationYear.get(Year.of(2008))).containsExactlyInAnyOrder(effectiveJava, cleanCode),
                    () -> assertThat(booksByPublicationYear).containsKey(Year.of(2004)),
                    () -> assertThat(booksByPublicationYear.get(Year.of(2004))).containsExactly(codeComplete),
                    () -> assertThat(booksByPublicationYear).containsKey(Year.of(1975)),
                    () -> assertThat(booksByPublicationYear.get(Year.of(1975))).containsExactly(mythicalManMonth)
            );
        }

        @Test
        @DisplayName("Les livres sont regroupés selon un critère personnalisé (Auteur)")
        void groupBooksByUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
            Map<String, List<Book>> booksByAuthor = shelf.groupBy(Book::getAuthor);

            assertAll(
                    () -> assertThat(booksByAuthor.get("Joshua Bloch")).containsExactly(effectiveJava),
                    () -> assertThat(booksByAuthor.get("Steve McConnel")).containsExactly(codeComplete),
                    () -> assertThat(booksByAuthor.get("Frederick Phillips Brooks")).containsExactly(mythicalManMonth),
                    () -> assertThat(booksByAuthor.get("Robert C. Martin")).containsExactly(cleanCode)
            );
        }

    }
}