package LMS.Tests;

import LMS.*;

import static org.junit.jupiter.api.Assertions.*;
        import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class LibraryTest {

    private Library library;

    @BeforeEach
    void setUp() {
        library = Library.getInstance();

        //clean static shared state
        Library.persons.clear();
    }

    @Test
    void shouldReturnBorrowerWhenIdExists() {
        // Arrange
        Borrower borrower = new Borrower(1, "Alice", "alice@mail.com", 1234);
        library.addBorrower(borrower);

        // Act
        Borrower result = library.findBorrowerById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getID());
    }

    @Test
    void shouldReturnNullWhenIdDoesNotExist() {
        // Arrange
        Borrower borrower = new Borrower(1, "Alice", "alice@mail.com", 1234);
        library.addBorrower(borrower);

        // Act
        Borrower result = library.findBorrowerById(99);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenIdBelongsToClerk() {
        // Arrange
        Clerk clerk = new Clerk(2,  "Admin", "Office", 123456, 3000.0, 1);
        library.addClerk(clerk);

        // Act
        Borrower result = library.findBorrowerById(2);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenPersonsListIsEmpty() {
        // Act
        Borrower result = library.findBorrowerById(1);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldReturnClerkWhenIdExists() {
        Clerk clerk = new Clerk(2, "Admin", "Office", 123456, 3000.0, 1);
        library.addClerk(clerk);

        Clerk result = library.findClerkById(2);
        assertNotNull(result);
        assertEquals(2, result.getID());
    }

    @Test
    void shouldReturnNullWhenClerkIdDoesNotExist() {
        Clerk clerk = new Clerk(2, "Admin", "Office", 123456, 3000.0, 1);
        library.addClerk(clerk);

        Clerk result = library.findClerkById(99);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenBorrower(){
        Borrower borrower = new Borrower(1, "Alice", "Street", 111111);
        library.addBorrower(borrower);

        Clerk result = library.findClerkById(1);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenNoClerksExist() {
        // Act
        Clerk result = library.findClerkById(1);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldPrintMessageWhenNoBooksExist() {
        // Arrange
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        library.viewAllBooks();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Currently, Library has no books"));

        // Cleanup
        System.setOut(originalOut);

    }

    @Test
    void shouldPrintBooksWhenBooksExist() {
        // Arrange
        Book book = new Book(1, "Test Book", "Subject", "Author", false);
        library.addBookinLibrary(book);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        library.viewAllBooks();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Books are"));
        assertTrue(output.contains("Test Book"));

    }

    @Test
    void shouldSetRequestExpiryCorrectly() {
        // Act
        library.setRequestExpiry(5);

        // Assert
        assertEquals(5, library.getHoldRequestExpiry());
    }


    @Test
    void shouldReturnHoldRequestExpiryCorrectly() {
        // Arrange
        library.setRequestExpiry(7);

        // Act
        int result = library.getHoldRequestExpiry();

        // Assert
        assertEquals(7, result);
    }





}
