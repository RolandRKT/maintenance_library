package library;

import java.util.*;

public class Library {
    private static List<Book> books = new ArrayList<>();
    private static Map<String, Borrower> loans = new HashMap<>();

    // ═══════════════════════════════════════════════════════════
    // Les Getters
    // ═══════════════════════════════════════════════════════════
    
    public static List<Book> getBooks() {
        return new ArrayList<>(books); // Copie défensive pour protéger la liste
    }
    
    public static Map<String, Borrower> getLoans() {
        return new HashMap<>(loans); // Copie défensive
    }
    
    // ═══════════════════════════════════════════════════════════
    // Les méthodes métiers
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Ajoute un livre à la bibliothèque.
     * cette version n'empêche pas les doublons.
     */
    public static void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Le livre ne peut pas être null");
        }
        books.add(book);
    }
    
    /**
     * Vérifie si un livre avec cet ISBN existe déjà.
     */
    public static boolean hasBookWithIsbn(String isbn) {
        return byIsbn(isbn) != null;
    }
    
    /**
     * Vérifie si un livre avec ce titre existe déjà.
     */
    public static boolean hasBookWithTitle(String title) {
        if (title == null) return false;
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Supprime tous les livres de la bibliothèque.
     */
    public static void clearBooks() {
        books.clear();
    }
    
    // ═══════════════════════════════════════════════════════════
    // MÉTHODES MÉTIER - RECHERCHE
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Recherche un livre par ISBN.
     * Retourne le premier livre trouvé, ou null si aucun.
     */
    public static Book byIsbn(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }
    
    /**
     * Recherche tous les livres d'un auteur (recherche partielle, insensible à la casse).
     */
    public static List<Book> findByAuthor(String author) {
        List<Book> results = new ArrayList<>();
        if (author == null || author.isEmpty()) {
            return results;
        }
        
        String searchLower = author.toLowerCase(Locale.ROOT);
        for (Book b : books) {
            if (b.getAuthor().toLowerCase(Locale.ROOT).contains(searchLower)) {
                results.add(b);
            }
        }
        return results;
    }
    
    // ═══════════════════════════════════════════════════════════
    // MÉTHODES MÉTIER - EMPRUNTS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Vérifie si un livre est emprunté.
     */
    public static boolean isBorrowed(String isbn) {
        return loans.containsKey(isbn);
    }
    
    /**
     * Emprunte un livre.
     * Retourne true si l'emprunt est réussi, false sinon.
     */
    public static boolean borrowBook(String isbn, String borrowerName) {
        // Validation : livre existe ?
        Book book = byIsbn(isbn);
        if (book == null) {
            return false; // Livre non trouvé
        }
        
        // Validation : déjà emprunté ?
        if (isBorrowed(isbn)) {
            return false; // Déjà emprunté
        }
        
        // Emprunt
        Borrower borrower = new Borrower(borrowerName);
        loans.put(isbn, borrower);
        return true;
    }
    
    /**
     * Retourne un livre.
     * Retourne le nom de l'emprunteur si le retour est réussi, null sinon.
     */
    public static String returnBook(String isbn) {
        Borrower borrower = loans.remove(isbn);
        return (borrower != null) ? borrower.getName() : null;
    }
    
    /**
     * Récupère l'emprunteur d'un livre.
     */
    public static Borrower getBorrower(String isbn) {
        return loans.get(isbn);
    }
    
    /**
     * Supprime tous les emprunts.
     */
    public static void clearLoans() {
        loans.clear();
    }
}
