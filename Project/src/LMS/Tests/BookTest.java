package LMS;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class BookTest {

    private Book book;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        // Reset static ID counter for predictable testing
        Book.setIDCount(0);

        book = new Book(-1, "Clean Code", "Software Engineering", "Robert Martin", false);
        borrower = new Borrower(1, "Onejda", "123", 123456);
    }

    // ----------------------------
    // Getter & Setter Tests
    // ----------------------------

    @Test
    void testGetTitle() {
        assertEquals("Clean Code", book.getTitle());
    }

    @Test
    void testGetAuthor() {
        assertEquals("Robert Martin", book.getAuthor());
    }

    @Test
    void testGetSubject() {
        assertEquals("Software Engineering", book.getSubject());
    }

    @Test
    void testBookIDAutoIncrement() {
        Book secondBook = new Book(-1, "JUnit", "Testing", "Beck", false);
        assertEquals(1, book.getID());
        assertEquals(2, secondBook.getID());
    }

    @Test
    void testIssuedStatusSetterAndGetter() {
        assertFalse(book.getIssuedStatus());
        book.setIssuedStatus(true);
        assertTrue(book.getIssuedStatus());
    }

    // ----------------------------
    // Hold Request Tests
    // ----------------------------

    @Test
    void testPlaceBookOnHold() {
        book.placeBookOnHold(borrower);

        ArrayList<HoldRequest> holdRequests = book.getHoldRequests();

        assertEquals(1, holdRequests.size());
        assertEquals(borrower, holdRequests.get(0).getBorrower());
        assertEquals(book, holdRequests.get(0).getBook());
    }

    @Test
    void testMakeHoldRequest_FirstTime() {
        book.makeHoldRequest(borrower);

        assertEquals(1, book.getHoldRequests().size());
    }

    @Test
    void testMakeHoldRequest_DuplicateRequestNotAllowed() {
        book.makeHoldRequest(borrower);
        book.makeHoldRequest(borrower);

        assertEquals(1, book.getHoldRequests().size());
    }

    // ----------------------------
    // Static Method Test
    // ----------------------------

    @Test
    void testSetIDCount() {
        Book.setIDCount(10);
        Book newBook = new Book(-1, "Refactoring", "SE", "Fowler", false);

        assertEquals(11, newBook.getID());
    }
}
