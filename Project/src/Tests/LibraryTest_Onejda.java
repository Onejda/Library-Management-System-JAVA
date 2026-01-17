package Tests;

import LMS.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest_Onejda {

    private Library library;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        library = Library.getInstance();

        // clean shared singleton state
        Library.persons.clear();
        library.getBooks().clear();

        originalOut = System.out;
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ------------------------
    // Borrower search (byId)
    // ------------------------

    @Test
    void shouldReturnBorrowerWhenIdExists() {
        Borrower borrower = new Borrower(1, "Alice", "Street", 1234);
        library.addBorrower(borrower);

        Borrower result = library.findBorrowerById(1);

        assertNotNull(result);
        assertEquals(1, result.getID());
        assertEquals("Alice", result.getName());
    }

    @Test
    void shouldReturnNullWhenBorrowerIdDoesNotExist() {
        Borrower borrower = new Borrower(1, "Alice", "Street", 1234);
        library.addBorrower(borrower);

        Borrower result = library.findBorrowerById(99);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenIdBelongsToClerk_inBorrowerSearch() {
        Clerk clerk = new Clerk(2, "Admin", "Office", 123456, 3000.0, 1);
        library.addClerk(clerk);

        Borrower result = library.findBorrowerById(2);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenPersonsListIsEmpty_inBorrowerSearch() {
        Borrower result = library.findBorrowerById(1);
        assertNull(result);
    }

    // ✅ REALLY USEFUL EDGE CASE:
    // If duplicate IDs exist, method returns the first matching Borrower in the list.
    @Test
    void shouldReturnFirstMatchingBorrowerWhenDuplicateIdsExist() {
        Borrower first  = new Borrower(1, "First", "A", 111);
        Borrower second = new Borrower(1, "Second", "B", 222);

        library.addBorrower(first);
        library.addBorrower(second);

        Borrower result = library.findBorrowerById(1);

        assertNotNull(result);
        assertSame(first, result); // proves deterministic "first match" behavior
    }

    // ------------------------
    // Clerk search (byId)
    // ------------------------

    @Test
    void shouldReturnClerkWhenIdExists() {
        Clerk clerk = new Clerk(2, "Admin", "Office", 123456, 3000.0, 1);
        library.addClerk(clerk);

        Clerk result = library.findClerkById(2);

        assertNotNull(result);
        assertEquals(2, result.getID());
        assertEquals("Admin", result.getName());
    }

    @Test
    void shouldReturnNullWhenClerkIdDoesNotExist() {
        Clerk clerk = new Clerk(2, "Admin", "Office", 123456, 3000.0, 1);
        library.addClerk(clerk);

        Clerk result = library.findClerkById(99);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenIdBelongsToBorrower_inClerkSearch() {
        Borrower borrower = new Borrower(1, "Alice", "Street", 111111);
        library.addBorrower(borrower);

        Clerk result = library.findClerkById(1);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenPersonsListIsEmpty_inClerkSearch() {
        Clerk result = library.findClerkById(1);
        assertNull(result);
    }

    // ✅ REALLY USEFUL EDGE CASE:
    // If duplicate IDs exist, method returns the first matching Clerk in the list.
    @Test
    void shouldReturnFirstMatchingClerkWhenDuplicateIdsExist() {
        Clerk first  = new Clerk(5, "FirstClerk", "A", 111, 2000.0, 1);
        Clerk second = new Clerk(5, "SecondClerk", "B", 222, 2500.0, 2);

        library.addClerk(first);
        library.addClerk(second);

        Clerk result = library.findClerkById(5);

        assertNotNull(result);
        assertSame(first, result);
    }

    // ------------------------
    // addBookinLibrary (yours)
    // ------------------------

    @Test
    void addBookinLibrary_shouldAddBookToLibraryList() {
        assertEquals(0, library.getBooks().size());

        Book book = new Book(1, "Test Book", "Subject", "Author", false);
        library.addBookinLibrary(book);

        assertEquals(1, library.getBooks().size());
        assertSame(book, library.getBooks().get(0));
    }

    // ------------------------
    // viewAllBooks (printing)
    // ------------------------

    @Test
    void shouldPrintMessageWhenNoBooksExist() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        library.viewAllBooks();

        String output = outContent.toString();
        assertTrue(output.contains("Currently, Library has no books"));
    }

    @Test
    void shouldPrintBooksWhenBooksExist() {
        Book book = new Book(1, "Test Book", "Subject", "Author", false);
        library.addBookinLibrary(book);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        library.viewAllBooks();

        String output = outContent.toString();
        assertTrue(output.contains("Books are"));
        assertTrue(output.contains("Test Book"));
    }

    // ----------------------------------
    // set/get Hold Request Expiry (yours)
    // ----------------------------------

    @Test
    void shouldSetAndGetRequestExpiryCorrectly() {
        library.setRequestExpiry(7);
        assertEquals(7, library.getHoldRequestExpiry());
    }
}
