package Tests;

import LMS.*;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BorrowerTest {

    @BeforeEach
    void resetState() {
        Person.setIDCount(0);
        Book.setIDCount(0);
        Clerk.currentdeskNumber = 0;
        Librarian.currentOfficeNumber = 0;
    }

    @Test
    @DisplayName("BOR-01: Constructor initializes empty borrowedBooks and onHoldBooks lists")
    void constructor_initializesEmptyLists() {
        Borrower borrower = new Borrower(-1, "Alice", "Addr", 12345);

        assertNotNull(borrower.getBorrowedBooks());
        assertNotNull(borrower.getOnHoldBooks());
        assertTrue(borrower.getBorrowedBooks().isEmpty());
        assertTrue(borrower.getOnHoldBooks().isEmpty());
    }

    @Test
    @DisplayName("BOR-02: Auto-ID increments when id = -1")
    void constructor_autoIdIncrements() {
        Borrower b1 = new Borrower(-1, "A", "Addr", 1);
        Borrower b2 = new Borrower(-1, "B", "Addr", 2);

        assertEquals(1, b1.getID());
        assertEquals(2, b2.getID());
        assertEquals("1", b1.getPassword());
        assertEquals("2", b2.getPassword());
    }

    @Test
    @DisplayName("BOR-03: addBorrowedBook and removeBorrowedBook update borrowedBooks list")
    void addAndRemoveBorrowedBook_updatesBorrowedList() {
        Borrower borrower = new Borrower(-1, "Alice", "Addr", 12345);
        assertTrue(borrower.getBorrowedBooks().isEmpty());

        Book book = new Book(-1, "Clean Code", "SE", "Robert Martin", false);
        Clerk issuer = new Clerk(-1, "Clerk", "Desk", 11111, 300.0, -1);
        Loan loan = new Loan(borrower, book, issuer, null, new Date(), null, false);

        borrower.addBorrowedBook(loan);
        assertEquals(1, borrower.getBorrowedBooks().size());
        assertSame(loan, borrower.getBorrowedBooks().get(0));

        borrower.removeBorrowedBook(loan);
        assertTrue(borrower.getBorrowedBooks().isEmpty());
    }

    @Test
    @DisplayName("BOR-04: removeBorrowedBook on non-existing loan leaves list unchanged")
    void removeBorrowedBook_nonExisting_noChange() {
        Borrower borrower = new Borrower(-1, "Alice", "Addr", 12345);

        Book book = new Book(-1, "Book", "S", "A", false);
        Clerk issuer = new Clerk(-1, "Clerk", "Desk", 11111, 300.0, -1);
        Loan loan = new Loan(borrower, book, issuer, null, new Date(), null, false);

        // remove without adding first
        borrower.removeBorrowedBook(loan);

        assertTrue(borrower.getBorrowedBooks().isEmpty());
    }

    @Test
    @DisplayName("BOR-05: addBorrowedBook allows duplicates (same reference can appear twice)")
    void addBorrowedBook_allowsDuplicates() {
        Borrower borrower = new Borrower(-1, "Alice", "Addr", 12345);

        Book book = new Book(-1, "Book", "S", "A", false);
        Clerk issuer = new Clerk(-1, "Clerk", "Desk", 11111, 300.0, -1);
        Loan loan = new Loan(borrower, book, issuer, null, new Date(), null, false);

        borrower.addBorrowedBook(loan);
        borrower.addBorrowedBook(loan);

        assertEquals(2, borrower.getBorrowedBooks().size());
        assertSame(loan, borrower.getBorrowedBooks().get(0));
        assertSame(loan, borrower.getBorrowedBooks().get(1));
    }

    @Test
    @DisplayName("BOR-06: addHoldRequest and removeHoldRequest update onHoldBooks list (null placeholder)")
    void addAndRemoveHoldRequest_updatesHoldList() {
        Borrower borrower = new Borrower(-1, "Bob", "Addr", 55555);
        assertTrue(borrower.getOnHoldBooks().isEmpty());

        borrower.addHoldRequest(null);
        assertEquals(1, borrower.getOnHoldBooks().size());
        assertNull(borrower.getOnHoldBooks().get(0));

        borrower.removeHoldRequest(null);
        assertTrue(borrower.getOnHoldBooks().isEmpty());
    }
}
