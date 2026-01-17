// HoldRequestTest.java

package Tests;

import LMS.Book;
import LMS.Borrower;
import LMS.HoldRequest;
import LMS.Person;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

class HoldRequestTest {

    @BeforeEach
    void resetIds() {
        Book.setIDCount(0);
        Person.setIDCount(0); // only if Person has this static method in your codebase
    }

    @Test
    void constructor_setsBorrowerBookAndDateCorrectly() {
        Borrower bor = new Borrower(-1, "John", "Addr", 123);
        Book book = new Book(-1, "Title", "Subject", "Author", false);
        Date date = new Date();

        HoldRequest hr = new HoldRequest(bor, book, date);

        assertSame(bor, hr.getBorrower());
        assertSame(book, hr.getBook());
        assertEquals(date, hr.getRequestDate());
    }

    @Test
    void getters_returnExactSameReferences() {
        Borrower bor = new Borrower(-1, "Alice", "Addr", 555);
        Book book = new Book(-1, "AI", "CS", "Russell", false);
        Date date = new Date(1700000000000L); // fixed date for determinism

        HoldRequest hr = new HoldRequest(bor, book, date);

        // Same references matter (no copying)
        assertSame(bor, hr.getBorrower());
        assertSame(book, hr.getBook());
        assertSame(date, hr.getRequestDate());
    }

    @Test
    void print_outputsTitleBorrowerAndDate() {
        Borrower bor = new Borrower(-1, "Bob", "Addr", 111);
        Book book = new Book(-1, "Clean Code", "Software", "Martin", false);
        Date date = new Date(1700000000000L);

        HoldRequest hr = new HoldRequest(bor, book, date);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            hr.print();
        } finally {
            System.setOut(originalOut);
        }

        String out = baos.toString();

        assertTrue(out.contains("Clean Code"));
        assertTrue(out.contains("Bob"));
        assertTrue(out.contains(date.toString()));
    }

    @Test
    void allowsNullDate_ifPassed() {
        Borrower bor = new Borrower(-1, "John", "Addr", 123);
        Book book = new Book(-1, "T", "S", "A", false);

        HoldRequest hr = new HoldRequest(bor, book, null);

        assertNull(hr.getRequestDate());
    }

}
