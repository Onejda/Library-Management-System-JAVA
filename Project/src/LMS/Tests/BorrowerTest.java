package LMS.Tests;

import LMS.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Borrower class.
 */
public class BorrowerTest {

    @BeforeEach
    void resetState() {
        // reset the global ID counter so IDs start from 1 for each test
        Person.setIDCount(0);
    }

    @Test
    void addAndRemoveBorrowedBook_updatesBorrowedList() {
        Borrower borrower = new Borrower(-1, "Alice", "Addr", 12345);
        assertTrue(borrower.getBorrowedBooks().isEmpty());

        // create supporting objects
        Book book = new Book(-1, "Clean Code", "SE", "Robert Martin", false);
        Clerk clerk = new Clerk(-1, "Clerk", "Desk", 11111, 300.0, -1);
        Loan loan = new Loan(borrower, book, clerk, null, new Date(), null, false);

        borrower.addBorrowedBook(loan);
        assertEquals(1, borrower.getBorrowedBooks().size());
        assertSame(loan, borrower.getBorrowedBooks().get(0));

        borrower.removeBorrowedBook(loan);
        assertTrue(borrower.getBorrowedBooks().isEmpty());
    }

    @Test
    void addAndRemoveHoldRequest_updatesHoldList() {
        Borrower borrower = new Borrower(-1, "Bob", "Addr", 55555);
        assertTrue(borrower.getOnHoldBooks().isEmpty());

        // Since HoldRequest classes are unavailable, use null as a placeholder
        borrower.addHoldRequest(null);
        assertEquals(1, borrower.getOnHoldBooks().size());
        assertNull(borrower.getOnHoldBooks().get(0));

        borrower.removeHoldRequest(null);
        assertTrue(borrower.getOnHoldBooks().isEmpty());
    }
}
