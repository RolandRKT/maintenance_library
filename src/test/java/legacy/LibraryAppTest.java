package legacy;

import library.Library;
import library.LibraryApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de non-régression - LibraryApp (Console)")
class LibraryAppTest {

    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        // Nettoie la bibliothèque avant chaque test
        Library.clearBooks();
        Library.clearLoans();

        // Capture les sorties console
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restaure System.in et System.out
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);

        // Nettoie
        Library.clearBooks();
        Library.clearLoans();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION 1 : AJOUT DE LIVRE
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 1 : Ajout d'un livre via console")
    void testLibraryApp_AddBook_Success() {
        // Arrange - Simuler l'entrée utilisateur
        String input = "1\n" + // Option 1 : Add book
                "978-TEST-001\n" + // ISBN
                "Bible\n" + // Title
                "Lenny\n" + // Author
                "2025\n" + // Year
                "6\n"; // Option 6 : Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to Library"), "Le message de bienvenue devrait s'afficher");
        assertTrue(output.contains("Added:"), "Le livre devrait être ajouté");
        assertTrue(output.contains("Bible"), "Le titre devrait apparaître");
        assertEquals(1, Library.getBooks().size(), "La bibliothèque devrait contenir 1 livre");
    }

    @Test
    @DisplayName("Option 1 : Ajout avec année invalide (defaulting to 0)")
    void testLibraryApp_AddBook_InvalidYear() {
        // Arrange
        String input = "1\n" +
                "978-TEST-002\n" +
                "Les singes\n" +
                "Le R\n" +
                "pas_un_nombre\n" + // Année invalide
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid year, defaulting to 0"), "Message d'erreur attendu");
        assertEquals(1, Library.getBooks().size());
        assertEquals(0, Library.getBooks().get(0).getYear(), "L'année devrait être 0");
    }

    @Test
    @DisplayName("Option 1 : Ajout avec ISBN dupliqué (warning)")
    void testLibraryApp_AddBook_DuplicateIsbn() {
        // Arrange - Ajouter d'abord un livre
        Library.addBook(new library.Book("978-DUP-001", "Livre 1", "Auteur", 2020));

        String input = "1\n" +
                "978-DUP-001\n" + // Même ISBN
                "Livre 2\n" +
                "Autre auteur\n" +
                "2021\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Warning: similar book already exists"), "Warning attendu");
        assertEquals(2, Library.getBooks().size(), "Les 2 livres devraient être ajoutés");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION 2 : LISTE DES LIVRES
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 2 : Lister les livres")
    void testLibraryApp_ListBooks() {
        // Arrange - Ajouter des livres
        Library.addBook(new library.Book("978-1", "Bible", "Lenny", 2025));
        Library.addBook(new library.Book("978-2", "Les singes", "Le R", 2024));

        String input = "2\n" + // Option 2 : List books
                "6\n"; // Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Bible"), "Le premier livre devrait être affiché");
        assertTrue(output.contains("Les singes"), "Le deuxième livre devrait être affiché");
    }

    @Test
    @DisplayName("Option 2 : Lister livre emprunté [BORROWED]")
    void testLibraryApp_ListBooks_WithBorrowedStatus() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Bible", "Lenny", 2025));
        Library.borrowBook("978-1", "Roland");

        String input = "2\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("[BORROWED]"), "Le statut emprunté devrait s'afficher");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION 3 : EMPRUNTER
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 3 : Emprunter un livre disponible")
    void testLibraryApp_BorrowBook_Success() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Bible", "Lenny", 2025));

        String input = "3\n" + // Option 3 : Borrow
                "Roland\n" + // Borrower name
                "978-1\n" + // ISBN
                "6\n"; // Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("OK."), "L'emprunt devrait réussir");
        assertTrue(Library.isBorrowed("978-1"), "Le livre devrait être emprunté");
    }

    @Test
    @DisplayName("Option 3 : Emprunter livre inexistant")
    void testLibraryApp_BorrowBook_NotFound() {
        // Arrange
        String input = "3\n" +
                "Roland\n" +
                "978-INEXISTANT\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Not found."), "Message 'Not found' attendu");
    }

    @Test
    @DisplayName("Option 3 : Emprunter livre déjà emprunté")
    void testLibraryApp_BorrowBook_AlreadyBorrowed() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Bible", "Lenny", 2025));
        Library.borrowBook("978-1", "Quelqu'un");

        String input = "3\n" +
                "Roland\n" +
                "978-1\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Already borrowed."), "Message 'Already borrowed' attendu");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION 4 : RETOURNER
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 4 : Retourner un livre emprunté")
    void testLibraryApp_ReturnBook_Success() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Bible", "Lenny", 2025));
        Library.borrowBook("978-1", "Roland");

        String input = "4\n" + // Option 4 : Return
                "978-1\n" + // ISBN
                "6\n"; // Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Returned."), "Message 'Returned' attendu");
        assertFalse(Library.isBorrowed("978-1"), "Le livre ne devrait plus être emprunté");
    }

    @Test
    @DisplayName("Option 4 : Retourner livre non emprunté")
    void testLibraryApp_ReturnBook_NotBorrowed() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Bible", "Lenny", 2025));

        String input = "4\n" +
                "978-1\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Not borrowed."), "Message 'Not borrowed' attendu");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION 5 : RECHERCHE PAR AUTEUR
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 5 : Rechercher par auteur")
    void testLibraryApp_FindByAuthor_Found() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Livre 1", "Lenny", 2025));
        Library.addBook(new library.Book("978-2", "Livre 2", "Roland", 2024));

        String input = "5\n" + // Option 5 : Find by author
                "lenny\n" + // Search term
                "6\n"; // Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Livre 1"), "Le livre de Lenny devrait être affiché");
        assertFalse(output.contains("Livre 2"), "Le livre de Roland ne devrait pas être affiché");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION 6 : EXIT
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 6 : Quitter l'application")
    void testLibraryApp_Exit() {
        // Arrange
        String input = "6\n"; // Exit immédiatement
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Bye."), "Message de sortie attendu");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS DE LA CONSOLE - OPTION INVALIDE
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option invalide : message d'erreur")
    void testLibraryApp_InvalidOption() {
        // Arrange
        String input = "99\n" + // Option invalide
                "6\n"; // Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Unknown option."), "Message d'erreur attendu");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST DU CONSTRUCTEUR LibraryApp
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Constructeur LibraryApp - Couverture")
    void testLibraryApp_Constructor() {
        // Act
        library.LibraryApp app = new library.LibraryApp();

        // Assert
        assertNotNull(app, "Une instance de LibraryApp peut être créée");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TESTS POUR TITRES ET AUTEURS DUPLIQUÉS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Option 1 : Ajout avec titre dupliqué (warning)")
    void testLibraryApp_AddBook_DuplicateTitle() {
        // Arrange - Ajouter d'abord un livre
        Library.addBook(new library.Book("978-AAA-001", "Bible", "Auteur", 2020));

        String input = "1\n" +
                "978-BBB-002\n" + // ISBN différent
                "Bible\n" + // Même titre
                "Autre auteur\n" +
                "2021\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Warning: similar book already exists"), "Warning pour titre dupliqué attendu");
        assertEquals(2, Library.getBooks().size());
    }

    @Test
    @DisplayName("Option 5 : Recherche auteur avec livre emprunté")
    void testLibraryApp_FindByAuthor_WithBorrowedBook() {
        // Arrange
        Library.addBook(new library.Book("978-1", "Livre 1", "Lenny", 2025));
        Library.borrowBook("978-1", "Roland"); // Emprunter le livre

        String input = "5\n" + // Option 5 : Find by author
                "lenny\n" + // Search term
                "6\n"; // Exit

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Livre 1"), "Le livre devrait être affiché");
        assertTrue(output.contains("[BORROWED]"), "Le statut emprunté devrait être affiché dans la recherche");
    }

    @Test
    @DisplayName("Option 1 : Warning quand SEULEMENT titre identique (pas ISBN)")
    void testLibraryApp_AddBook_OnlyTitleMatch() {
        // Arrange - Ajouter un livre avec ISBN et titre spécifiques
        Library.addBook(new library.Book("978-ORIGINAL-001", "Bible", "Auteur Original", 2020));

        // On ajoute un livre avec ISBN DIFFÉRENT mais MÊME titre
        String input = "1\n" +
                "978-DIFFERENT-999\n" + // ISBN DIFFÉRENT
                "Bible\n" + // MÊME TITRE (la condition qui manque)
                "Auteur Nouveau\n" +
                "2021\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Warning: similar book already exists"),
                "Warning devrait s'afficher quand le titre match (même si ISBN différent)");
        assertEquals(2, Library.getBooks().size(), "Les 2 livres devraient être ajoutés");
    }

    @Test
    @DisplayName("Option 1 : Ajout livre sans doublon (aucun warning)")
    void testLibraryApp_AddBook_NoDuplicate() {
        // Arrange - Ajouter des livres DIFFÉRENTS
        Library.addBook(new library.Book("978-AAA-001", "Livre A", "Auteur A", 2020));
        Library.addBook(new library.Book("978-BBB-002", "Livre B", "Auteur B", 2021));

        // Ajouter un livre complètement DIFFÉRENT (ni ISBN ni titre ne matchent)
        String input = "1\n" +
                "978-CCC-003\n" + // ISBN différent
                "Livre C\n" + // Titre différent
                "Auteur C\n" +
                "2022\n" +
                "6\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        LibraryApp.main(new String[] {});

        // Assert
        String output = outputStream.toString();
        assertFalse(output.contains("Warning: similar book already exists"),
                "Aucun warning ne devrait s'afficher car aucun doublon");
        assertEquals(3, Library.getBooks().size(), "Les 3 livres devraient être présents");
    }
}
