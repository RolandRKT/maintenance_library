package legacy;

import library.Borrower;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de non-régression - Borrower")
class BorrowerTest {

    @Test
    @DisplayName("Création d'un emprunteur avec nom valide - Cas nominal")
    void testBorrower_ValidName_Success() {
        // Arrange & Act
        Borrower borrower = new Borrower("Le R");
        
        // Assert
        assertEquals("Le R", borrower.getName());
    }

    @Test
    @DisplayName("Création d'un emprunteur avec nom vide - Point critique")
    void testBorrower_EmptyName_Allowed() {
        // Arrange & Act
        Borrower borrower = new Borrower("");
        
        // Assert
        assertEquals("", borrower.getName(), "Le système permet les noms vides");
    }

    @Test
    @DisplayName("Attribut name est public - Dette technique")
    void testBorrower_NameIsPublic_CanBeModified() {
        // Arrange
        Borrower borrower = new Borrower("Le R");
        
        // Act - Le champ n'est plus public, on peut le modifier avec méthode
        borrower.setName("Le L");
        
        // Assert
        assertEquals("Le L", borrower.getName(), "L'attribut name est modifiable car public");
    }

    @Test
    @DisplayName("Création d'un emprunteur avec nom null - Cas limite")
    void testBorrower_NullName_Allowed() {
        // Arrange & Act
        Borrower borrower = new Borrower(null);
        
        // Assert
        assertNull(borrower.getName(), "Le système permet les noms null");
    }

    @Test
    @DisplayName("Création d'un emprunteur avec caractères spéciaux - Validation")
    void testBorrower_SpecialCharactersInName_Allowed() {
        // Arrange & Act
        Borrower borrower = new Borrower("Le J");
        
        // Assert
        assertEquals("Le J", borrower.getName());
    }

    @Test
    @DisplayName("Création d'un emprunteur avec nom très long - Cas limite")
    void testBorrower_LongName_Allowed() {
        // Arrange
        String longName = "A".repeat(1000);
        
        // Act
        Borrower borrower = new Borrower(longName);
        
        // Assert
        assertEquals(longName, borrower.getName());
        assertEquals(1000, borrower.getName().length());
    }
}
