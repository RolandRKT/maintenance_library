package library;

import java.util.*;

/**
 * Console app for a tiny library.
 * 
 */
public class LibraryApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Library v1.0");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1) Add book  2) List books  3) Borrow  4) Return  5) Find by author  6) Exit");
            System.out.print("> ");
            String choice = sc.nextLine();
            if ("1".equals(choice)) {

                System.out.print("ISBN? ");
                String isbn = sc.nextLine();
                System.out.print("Title? ");
                String title = sc.nextLine();
                System.out.print("Author? ");
                String author = sc.nextLine();
                System.out.print("Year? ");
                String yearStr = sc.nextLine();
                int year = 0;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (Exception ex) {
                    System.out.println("Invalid year, defaulting to 0");
                }

                for (Book b : Library.getBooks()) {
                    if (b.getIsbn().equals(isbn) || b.getTitle().equalsIgnoreCase(title)) {
                        System.out.println("Warning: similar book already exists.");
                    }
                }
                Book b = new Book(isbn, title, author, year);
                Library.addBook(b);
                System.out.println("Added: " + b);
            } else if ("2".equals(choice)) {

                for (Book b : Library.getBooks()) {
                    System.out.println(b + (Library.isBorrowed(b.getIsbn()) ? " [BORROWED]" : ""));
                }
            } else if ("3".equals(choice)) {
                System.out.print("Borrower name? ");
                String n = sc.nextLine();
                System.out.print("ISBN to borrow? ");
                String ib = sc.nextLine();

                Book found = Library.byIsbn(ib);
                
                if (found == null) {
                    System.out.println("Not found.");
                } else if (Library.isBorrowed(ib)) {
                    System.out.println("Already borrowed.");
                } else {
                    Library.borrowBook(ib, n);
                    System.out.println("OK.");
                }
            } else if ("4".equals(choice)) {
                System.out.print("ISBN to return? ");
                String ib = sc.nextLine();
                if (Library.returnBook(ib) != null) {
                    System.out.println("Returned.");
                } else {
                    System.out.println("Not borrowed.");
                }
            } else if ("5".equals(choice)) {
                System.out.print("Author contains? ");
                String a = sc.nextLine();

                List<Book> results = Library.findByAuthor(a);
                for (Book b : results) {
                    System.out.println(b + (Library.isBorrowed(b.getIsbn()) ? " [BORROWED]" : ""));
                }
            } else if ("6".equals(choice)) {
                break;
            } else {
                System.out.println("Unknown option.");
            }
        }

        System.out.println("Bye.");
    }
}
