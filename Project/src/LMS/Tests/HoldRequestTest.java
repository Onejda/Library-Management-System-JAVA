package LMS;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class HoldRequestTest {

    private Borrower borrower;
    private Book book;
    private HoldRequest holdRequest;
    private Date requestDate;

    @BeforeEach
    void setUp() {
        borrower = new Borrower(1,"Onejda", "123", 123456);
        book = new Book(-1, "Clean Code", "Software Engineering", "Robert Martin", false);
        requestDate = new Date();

        holdRequest = new HoldRequest(borrower, book, requestDate);
    }

    // ----------------------------
    // Constructor & Getter Tests
    // ----------------------------

    @Test
    void testGetBorrower() {
        assertEquals(borrower, holdRequest.getBorrower());
    }

    @Test
    void testGetBook() {
        assertEquals(book, holdRequest.getBook());
    }

    @Test
    void testGetRequestDate() {
        assertEquals(requestDate, holdRequest.getRequestDate());
    }
}
