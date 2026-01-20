package legacy;

import library.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de non-régression - Book")
class BookTest {

    @Test
    @DisplayName("Création d'un livre valide - Cas nominal")
    void testBook_ValidBook_Success() {
        // Arrange & Act
        Book book = new Book("978-1234567890", "Clean Code", "Robert Martin", 2008);
        
        // Assert
        assertEquals("978-1234567890", book.getIsbn());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals(2008, book.getYear());
    }

    @Test
    @DisplayName("Création d'un livre avec ISBN vide - Point critique")
    void testBook_EmptyIsbn_Allowed() {
        // Arrange & Act
        Book book = new Book("", "Test Book", "Test Author", 2020);
        
        // Assert
        assertEquals("", book.getIsbn(), "Le système permet les ISBN vides");
    }

    @Test
    @DisplayName("Création d'un livre avec titre vide - Validation")
    void testBook_EmptyTitle_Allowed() {
        // Arrange & Act
        Book book = new Book("978-1234567890", "", "Test Author", 2020);
        
        // Assert
        assertEquals("", book.getTitle());
    }

    @Test
    @DisplayName("Création d'un livre avec auteur vide - Validation")
    void testBook_EmptyAuthor_Allowed() {
        // Arrange & Act
        Book book = new Book("978-1234567890", "Test Book", "", 2020);
        
        // Assert
        assertEquals("", book.getAuthor());
    }

    @Test
    @DisplayName("Création d'un livre avec année 0 - Cas limite")
    void testBook_YearZero_Allowed() {
        // Arrange & Act
        Book book = new Book("978-1234567890", "Test Book", "Test Author", 0);
        
        // Assert
        assertEquals(0, book.getYear());
    }

    @Test
    @DisplayName("Création d'un livre avec année négative - Cas limite")
    void testBook_NegativeYear_Allowed() {
        // Arrange & Act
        Book book = new Book("978-1234567890", "Ancient Book", "Old Author", -500);
        
        // Assert
        assertEquals(-500, book.getYear());
    }

    @Test
    @DisplayName("toString formate correctement - Couverture complète")
    void testBook_ToString_FormatsCorrectly() {
        // Arrange
        Book book = new Book("978-1234567890", "Clean Code", "Robert Martin", 2008);
        
        // Act
        String result = book.toString();
        
        // Assert
        assertEquals("[978-1234567890] Clean Code - Robert Martin (2008)", result);
    }

    @Test
    @DisplayName("toString avec valeurs vides - Cas limite")
    void testBook_ToString_WithEmptyValues() {
        // Arrange
        Book book = new Book("", "", "", 0);
        
        // Act
        String result = book.toString();
        
        // Assert
        assertEquals("[]  -  (0)", result);
    }

    @Test
    @DisplayName("Getters retournent les bonnes valeurs - Couverture")
    void testBook_Getters_ReturnCorrectValues() {
        // Arrange
        Book book = new Book("123", "Title", "Author", 2020);
        
        // Assert - Tous les getters
        assertEquals("123", book.getIsbn());
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals(2020, book.getYear());
    }
}
