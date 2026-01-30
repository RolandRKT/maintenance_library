package library;

import java.util.*;

/**
 * Console app for a tiny library.
 * 
 */
public class LibraryApp {
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("Welcome to Library v1.0");
        scanner = new Scanner(System.in);

        while (true) {
            displayMenu();
            String choice = scanner.nextLine();

            if (!handleChoice(choice)) {
                break; // Exit
            }
        }

        System.out.println("Bye.");
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n1) Add book  2) List books  3) Borrow  4) Return  5) Find by author  6) Exit");
        System.out.print("> ");
    }

    private static boolean handleChoice(String choice) {
        switch (choice) {
            case "1":
                handleAddBook();
                return true;
            case "2":
                handleListBooks();
                return true;
            case "3":
                handleBorrowBook();
                return true;
            case "4":
                handleReturnBook();
                return true;
            case "5":
                handleFindByAuthor();
                return true;
            case "6":
                return false; // Exit
            default:
                System.out.println("Unknown option.");
                return true;
        }
    }

    private static void handleAddBook() {
        System.out.print("ISBN? ");
        String isbn = scanner.nextLine();
        System.out.print("Title? ");
        String title = scanner.nextLine();
        System.out.print("Author? ");
        String author = scanner.nextLine();
        System.out.print("Year? ");
        int year = readYear(scanner.nextLine());

        checkForDuplicates(isbn, title);

        Book book = new Book(isbn, title, author, year);
        Library.addBook(book);
        System.out.println("Added: " + book);
    }

    private static int readYear(String yearStr) {
        try {
            return Integer.parseInt(yearStr);
        } catch (Exception ex) {
            System.out.println("Invalid year, defaulting to 0");
            return 0;
        }
    }

    private static void checkForDuplicates(String isbn, String title) {
        for (Book b : Library.getBooks()) {
            if (b.getIsbn().equals(isbn) || b.getTitle().equalsIgnoreCase(title)) {
                System.out.println("Warning: similar book already exists.");
                break;
            }
        }
    }

    private static void handleListBooks() {
        for (Book b : Library.getBooks()) {
            displayBookWithStatus(b);
        }
    }

    private static void handleBorrowBook() {
        System.out.print("Borrower name? ");
        String borrowerName = scanner.nextLine();
        System.out.print("ISBN to borrow? ");
        String isbn = scanner.nextLine();

        Book found = Library.byIsbn(isbn);

        if (found == null) {
            System.out.println("Not found.");
        } else if (Library.isBorrowed(isbn)) {
            System.out.println("Already borrowed.");
        } else {
            Library.borrowBook(isbn, borrowerName);
            System.out.println("OK.");
        }
    }

    private static void handleReturnBook() {
        System.out.print("ISBN to return? ");
        String isbn = scanner.nextLine();

        if (Library.returnBook(isbn) != null) {
            System.out.println("Returned.");
        } else {
            System.out.println("Not borrowed.");
        }
    }

    private static void handleFindByAuthor() {
        System.out.print("Author contains? ");
        String author = scanner.nextLine();

        List<Book> results = Library.findByAuthor(author);
        for (Book b : results) {
            displayBookWithStatus(b);
        }
    }

    // Refacto 2 : Extract Method du cm pour éliminer duplication
    private static void displayBookWithStatus(Book book) {
        String status = Library.isBorrowed(book.getIsbn()) ? " [BORROWED]" : "";
        System.out.println(book + status);
    }
}
