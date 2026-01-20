package legacy;

import library.Book;
import library.Borrower;
import library.Library;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

@DisplayName("Tests de non-régression - Library")
class LibraryTest {

    @BeforeEach
    void setUp() {
        // Nettoie l'état avant chaque test (isolation des tests)
        Library.clearBooks();
        Library.clearLoans();
    }

    @AfterEach
    void tearDown() {
        // Nettoie l'état après chaque test
        Library.clearBooks();
        Library.clearLoans();
    }

    // Test de construction

    @Test
    @DisplayName("Constructeur de Library - Couverture complète")
    void testLibrary_Constructor_CanBeInstantiated() {
        // Act
        Library library = new Library();

        // Assert
        assertNotNull(library, "Une instance de Library peut être créée");
        // Note : En pratique, Library ne devrait jamais être instanciée car tout est
        // static
        // Ce test existe uniquement pour la couverture à 100%
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS D'AJOUT DE LIVRES
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Ajout d'un livre valide - Cas nominal")
    void testAddBook_ValidBook_Success() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);

        // Act
        Library.addBook(book);

        // Assert
        assertEquals(1, Library.getBooks().size(), "La bibliothèque devrait contenir 1 livre");
        assertEquals(book, Library.getBooks().get(0), "Le livre ajouté devrait être dans la liste");
    }

    @Test
    @DisplayName("Ajout de plusieurs livres - Cas nominal")
    void testAddBook_MultipleBooks_Success() {
        // Arrange
        Book book1 = new Book("978-LENNY-001", "Le livre de Lenny", "Lenny", 2026);
        Book book2 = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2025);
        Book book3 = new Book("978-SINGES-001", "Les singes", "Le R", 2024);

        // Act
        Library.addBook(book1);
        Library.addBook(book2);
        Library.addBook(book3);

        // Assert
        assertEquals(3, Library.getBooks().size(), "La bibliothèque devrait contenir 3 livres");
        assertTrue(Library.getBooks().contains(book1));
        assertTrue(Library.getBooks().contains(book2));
        assertTrue(Library.getBooks().contains(book3));
    }

    @Test
    @DisplayName("Ajout d'un livre avec ISBN dupliqué - Point critique identifié")
    void testAddBook_DuplicateIsbn_AllowsDuplicate() {
        // Arrange
        Book book1 = new Book("978-LENNY-999", "Le livre de Lenny", "Lenny", 2026);
        Book book2 = new Book("978-LENNY-999", "Comment devenir comme Lenny", "Moi", 2025);

        // Act
        Library.addBook(book1);
        Library.addBook(book2);

        // Assert - Test de non-régression : le système PERMET actuellement les doublons
        assertEquals(2, Library.getBooks().size(), "Le système permet l'ajout de doublons ISBN (comportement actuel)");
    }

    @Test
    @DisplayName("Ajout d'un livre avec titre dupliqué - Point critique")
    void testAddBook_DuplicateTitle_AllowsDuplicate() {
        // Arrange
        Book book1 = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Book book2 = new Book("978-ROLAND-002", "Comment devenir comme Roland", "Le R", 2025);

        // Act
        Library.addBook(book1);
        Library.addBook(book2);

        // Assert
        assertEquals(2, Library.getBooks().size(), "Le système permet l'ajout de titres dupliqués");
    }

    @Test
    @DisplayName("Attributs books et loans sont maintenant privés - Dette technique résolue")
    void testLibrary_AttributesArePrivate_UseGetters() {
        // Arrange
        Book book = new Book("978-TEST-001", "Bible", "Lenny", 2026);

        // Act - Utilisation des méthodes publiques
        Library.addBook(book);
        Library.clearBooks();

        // Assert
        assertEquals(0, Library.getBooks().size(), "clearBooks() permet de vider la bibliothèque proprement");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS D'EMPRUNT
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Emprunt d'un livre disponible - Cas nominal")
    void testBorrowBook_AvailableBook_Success() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act
        boolean result = Library.borrowBook(book.getIsbn(), "Roland");

        // Assert
        assertTrue(result, "L'emprunt devrait réussir");
        assertTrue(Library.isBorrowed(book.getIsbn()), "Le livre devrait être emprunté");
        assertEquals("Roland", Library.getBorrower(book.getIsbn()).getName());
    }

    @Test
    @DisplayName("Emprunt d'un livre déjà emprunté - Détection")
    void testBorrowBook_AlreadyBorrowed_ReturnsFalse() {
        // Arrange
        Book book = new Book("978-LENNY-001", "Le livre de Lenny", "Lenny", 2026);
        Library.addBook(book);
        Library.borrowBook(book.getIsbn(), "Roland");

        // Act
        boolean result = Library.borrowBook(book.getIsbn(), "Le R");

        // Assert
        assertFalse(result, "L'emprunt devrait échouer car déjà emprunté");
        assertTrue(Library.isBorrowed(book.getIsbn()));
        assertEquals("Roland", Library.getBorrower(book.getIsbn()).getName(),
                "L'emprunteur devrait toujours être Roland");
    }

    @Test
    @DisplayName("Emprunt d'un livre inexistant - Validation")
    void testBorrowBook_NonExistingBook_ReturnsFalse() {
        // Act
        boolean result = Library.borrowBook("978-INEXISTANT-999", "Moi");

        // Assert
        assertFalse(result, "L'emprunt devrait échouer car le livre n'existe pas");
        assertFalse(Library.isBorrowed("978-INEXISTANT-999"));
    }

    @Test
    @DisplayName("Emprunt avec ISBN vide - Point critique")
    void testBorrowBook_EmptyIsbn_Allowed() {
        // Arrange
        Book book = new Book("", "Les singes", "Le R", 2024);
        Library.addBook(book);

        // Act
        boolean result = Library.borrowBook("", "Lenny");

        // Assert
        assertTrue(result, "Le système permet l'emprunt avec ISBN vide");
        assertEquals("Lenny", Library.getBorrower("").getName());
    }

    @Test
    @DisplayName("Emprunt avec nom d'emprunteur vide - Point critique")
    void testBorrowBook_EmptyBorrowerName_Allowed() {
        // Arrange
        Book book = new Book("978-SINGES-001", "Les singes", "Le R", 2024);
        Library.addBook(book);

        // Act
        boolean result = Library.borrowBook(book.getIsbn(), "");

        // Assert
        assertTrue(result);
        assertEquals("", Library.getBorrower(book.getIsbn()).getName());
    }

    @Test
    @DisplayName("Retour d'un livre emprunté - Cas nominal")
    void testReturnBook_BorrowedBook_Success() {
        // Arrange
        Book book = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Library.addBook(book);
        Library.borrowBook(book.getIsbn(), "Lenny");

        // Act
        String borrowerName = Library.returnBook(book.getIsbn());

        // Assert
        assertNotNull(borrowerName, "Le retour devrait retourner le nom de l'emprunteur");
        assertEquals("Lenny", borrowerName);
        assertFalse(Library.isBorrowed(book.getIsbn()), "Le livre ne devrait plus être emprunté");
    }

    @Test
    @DisplayName("Retour d'un livre non emprunté - Cas d'erreur")
    void testReturnBook_NotBorrowed_ReturnsNull() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act
        String borrowerName = Library.returnBook(book.getIsbn());

        // Assert
        assertNull(borrowerName, "Aucun emprunteur ne devrait être retourné");
        assertFalse(Library.isBorrowed(book.getIsbn()));
    }

    @Test
    @DisplayName("Plusieurs emprunteurs avec différents livres - Cas complexe")
    void testBorrowBook_MultipleBorrowers_Success() {
        // Arrange
        Book book1 = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Book book2 = new Book("978-LENNY-001", "Le livre de Lenny", "Lenny", 2026);
        Book book3 = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Library.addBook(book1);
        Library.addBook(book2);
        Library.addBook(book3);

        // Act
        Library.borrowBook(book1.getIsbn(), "Roland");
        Library.borrowBook(book2.getIsbn(), "Le R");
        Library.borrowBook(book3.getIsbn(), "Moi");

        // Assert
        assertTrue(Library.isBorrowed(book1.getIsbn()));
        assertTrue(Library.isBorrowed(book2.getIsbn()));
        assertTrue(Library.isBorrowed(book3.getIsbn()));
        assertEquals("Roland", Library.getBorrower(book1.getIsbn()).getName());
        assertEquals("Le R", Library.getBorrower(book2.getIsbn()).getName());
        assertEquals("Moi", Library.getBorrower(book3.getIsbn()).getName());
    }

    @Test
    @DisplayName("Cycle complet : emprunt-retour-réemprunt - Scénario réel")
    void testBorrowReturnBorrowCycle_Success() {
        // Arrange
        Book book = new Book("978-LENNY-002", "Comment devenir comme Lenny", "Moi", 2025);
        Library.addBook(book);

        // Act & Assert - Premier emprunt
        Library.borrowBook(book.getIsbn(), "Roland");
        assertTrue(Library.isBorrowed(book.getIsbn()));
        assertEquals("Roland", Library.getBorrower(book.getIsbn()).getName());

        // Retour
        Library.returnBook(book.getIsbn());
        assertFalse(Library.isBorrowed(book.getIsbn()));

        // Deuxième emprunt
        Library.borrowBook(book.getIsbn(), "Lenny");
        assertTrue(Library.isBorrowed(book.getIsbn()));
        assertEquals("Lenny", Library.getBorrower(book.getIsbn()).getName());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE RECHERCHE
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Recherche par ISBN existant - Cas nominal")
    void testFindByIsbn_ExistingBook_ReturnsBook() {
        // Arrange
        Book book1 = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Book book2 = new Book("978-SINGES-001", "Les singes", "Le R", 2024);
        Library.addBook(book1);
        Library.addBook(book2);

        // Act
        Book found = Library.byIsbn("978-SINGES-001");

        // Assert
        assertNotNull(found, "Le livre devrait être trouvé");
        assertEquals("978-SINGES-001", found.getIsbn());
        assertEquals("Les singes", found.getTitle());
        assertEquals("Le R", found.getAuthor());
        assertEquals(2024, found.getYear());
    }

    @Test
    @DisplayName("Recherche par ISBN inexistant - Cas d'échec")
    void testFindByIsbn_NonExistingBook_ReturnsNull() {
        // Arrange
        Book book = new Book("978-LENNY-001", "Le livre de Lenny", "Lenny", 2026);
        Library.addBook(book);

        // Act
        Book found = Library.byIsbn("978-INEXISTANT-999");

        // Assert
        assertNull(found, "Aucun livre ne devrait être trouvé");
    }

    @Test
    @DisplayName("Recherche dans bibliothèque vide - White Box (boucle vide)")
    void testFindByIsbn_EmptyLibrary_ReturnsNull() {
        // Act
        Book found = Library.byIsbn("978-BIBLE-001");

        // Assert
        assertNull(found, "Aucun livre dans une bibliothèque vide");
    }

    @Test
    @DisplayName("Recherche avec ISBN vide - Cas limite")
    void testFindByIsbn_EmptyIsbn_CanFindBook() {
        // Arrange
        Book book = new Book("", "Les singes", "Le R", 2024);
        Library.addBook(book);

        // Act
        Book found = Library.byIsbn("");

        // Assert
        assertNotNull(found, "Le livre avec ISBN vide devrait être trouvé");
        assertEquals("Les singes", found.getTitle());
    }

    @Test
    @DisplayName("Recherche avec ISBN null - Cas limite")
    void testFindByIsbn_NullIsbn_ReturnsNull() {
        // Arrange
        Book book = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Library.addBook(book);

        // Act
        Book found = Library.byIsbn(null);

        // Assert
        assertNull(found, "null ne devrait matcher aucun ISBN");
    }

    @Test
    @DisplayName("Recherche retourne le premier livre en cas de doublons - White Box (break)")
    void testFindByIsbn_DuplicateIsbn_ReturnsFirstMatch() {
        // Arrange
        Book book1 = new Book("978-LENNY-999", "Le livre de Lenny", "Lenny", 2026);
        Book book2 = new Book("978-LENNY-999", "Comment devenir comme Lenny", "Moi", 2025);
        Library.addBook(book1);
        Library.addBook(book2);

        // Act
        Book found = Library.byIsbn("978-LENNY-999");

        // Assert
        assertNotNull(found);
        assertEquals("Le livre de Lenny", found.getTitle(),
                "byIsbn retourne le premier livre trouvé (break dans la boucle)");
        assertEquals("Lenny", found.getAuthor());
    }

    @Test
    @DisplayName("isBorrowed sur bibliothèque vide - White Box")
    void testIsBorrowed_EmptyLoans_ReturnsFalse() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act & Assert
        assertFalse(Library.isBorrowed(book.getIsbn()), "Un livre non emprunté retourne false");
    }

    @Test
    @DisplayName("isBorrowed avec ISBN null - Cas limite")
    void testIsBorrowed_NullIsbn_ReturnsFalse() {
        // Act & Assert
        assertFalse(Library.isBorrowed(null), "null ne correspond à aucun emprunt");
    }

    @Test
    @DisplayName("Recherche par auteur sensible à la casse - Point critique")
    void testFindByAuthor_CaseSensitive_Behavior() {
        // Arrange
        Book book1 = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Book book2 = new Book("978-ROLAND-002", "Bible", "roland", 2025);
        Library.addBook(book1);
        Library.addBook(book2);

        // Act
        List<Book> results = Library.findByAuthor("roland");

        // Assert
        assertEquals(2, results.size(), "Recherche insensible à la casse avec toLowerCase()");
        assertTrue(results.contains(book1));
        assertTrue(results.contains(book2));
    }

    @Test
    @DisplayName("Recherche par auteur avec caractères spéciaux - Point critique")
    void testFindByAuthor_SpecialCharacters_ExactMatch() {
        // Arrange
        Book book1 = new Book("978-LER-001", "Les singes", "Le R", 2024);
        Book book2 = new Book("978-LER-002", "Bible", "Le-R", 2025);
        Library.addBook(book1);
        Library.addBook(book2);

        // Act
        List<Book> resultsWithSpace = Library.findByAuthor("le r");
        List<Book> resultsWithHyphen = Library.findByAuthor("le-r");

        // Assert
        assertEquals(1, resultsWithSpace.size(), "Espace doit correspondre exactement");
        assertTrue(resultsWithSpace.contains(book1));

        assertEquals(1, resultsWithHyphen.size(), "Trait d'union doit correspondre exactement");
        assertTrue(resultsWithHyphen.contains(book2));
    }

    @Test
    @DisplayName("Recherche partielle par auteur - Cas nominal")
    void testFindByAuthor_PartialMatch_Works() {
        // Arrange
        Book book1 = new Book("978-LENNY-001", "Le livre de Lenny", "Lenny", 2026);
        Book book2 = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Library.addBook(book1);
        Library.addBook(book2);

        // Act
        List<Book> results = Library.findByAuthor("len");

        // Assert - Recherche partielle fonctionne
        assertEquals(1, results.size());
        assertTrue(results.contains(book1));
        assertFalse(results.contains(book2));
    }

    @Test
    @DisplayName("Recherche par auteur avec chaîne vide - Cas limite")
    void testFindByAuthor_EmptyString_ReturnsEmpty() {
        // Arrange
        Book book = new Book("978-LENNY-001", "Le livre de Lenny", "Lenny", 2026);
        Library.addBook(book);

        // Act
        List<Book> results = Library.findByAuthor("");

        // Assert
        assertEquals(0, results.size(), "Une recherche vide retourne une liste vide");
    }

    @Test
    @DisplayName("Recherche par auteur avec null - Cas limite")
    void testFindByAuthor_NullAuthor_ReturnsEmpty() {
        // Arrange
        Book book = new Book("978-ROLAND-001", "Comment devenir comme Roland", "Roland", 2026);
        Library.addBook(book);

        // Act
        List<Book> results = Library.findByAuthor(null);

        // Assert
        assertEquals(0, results.size(), "Une recherche null retourne une liste vide");
    }

    @Test
    @DisplayName("Vérification de livre avec titre existant - Nouvelle méthode")
    void testHasBookWithTitle_ExistingTitle_ReturnsTrue() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act
        boolean result = Library.hasBookWithTitle("Bible");

        // Assert
        assertTrue(result, "hasBookWithTitle devrait trouver le livre");
    }

    @Test
    @DisplayName("Vérification de livre avec titre inexistant - Nouvelle méthode")
    void testHasBookWithTitle_NonExistingTitle_ReturnsFalse() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act
        boolean result = Library.hasBookWithTitle("Inexistant");

        // Assert
        assertFalse(result, "hasBookWithTitle ne devrait pas trouver de livre");
    }

    @Test
    @DisplayName("Vérification de livre avec titre null - Cas limite")
    void testHasBookWithTitle_NullTitle_ReturnsFalse() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act
        boolean result = Library.hasBookWithTitle(null);

        // Assert
        assertFalse(result, "hasBookWithTitle avec null devrait retourner false");
    }

    @Test
    @DisplayName("Ajout d'un livre null - Validation")
    void testAddBook_NullBook_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            Library.addBook(null);
        }, "addBook avec null devrait lancer une IllegalArgumentException");
    }

    @Test
    @DisplayName("Vérification de livre avec ISBN existant - Nouvelle méthode")
    void testHasBookWithIsbn_ExistingIsbn_ReturnsTrue() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);

        // Act
        boolean result = Library.hasBookWithIsbn("978-BIBLE-001");

        // Assert
        assertTrue(result, "hasBookWithIsbn devrait trouver le livre");
    }

    @Test
    @DisplayName("Vérification de livre avec ISBN inexistant - Nouvelle méthode")
    void testHasBookWithIsbn_NonExistingIsbn_ReturnsFalse() {
        // Act
        boolean result = Library.hasBookWithIsbn("978-INEXISTANT-999");

        // Assert
        assertFalse(result, "hasBookWithIsbn ne devrait pas trouver de livre");
    }

    @Test
    @DisplayName("getLoans retourne une copie défensive - Sécurité")
    void testGetLoans_ReturnsDefensiveCopy() {
        // Arrange
        Book book = new Book("978-BIBLE-001", "Bible", "Lenny", 2025);
        Library.addBook(book);
        Library.borrowBook(book.getIsbn(), "Roland");

        // Act
        Map<String, Borrower> loans1 = Library.getLoans();
        Map<String, Borrower> loans2 = Library.getLoans();

        // Assert
        assertEquals(1, loans1.size(), "getLoans devrait retourner les emprunts");
        assertNotSame(loans1, loans2, "getLoans devrait retourner une nouvelle copie à chaque appel");

        // Modifier la copie ne doit pas affecter l'original
        loans1.clear();
        assertEquals(0, loans1.size(), "La copie est vidée");
        assertEquals(1, Library.getLoans().size(), "L'original n'est pas affecté");
    }

}
