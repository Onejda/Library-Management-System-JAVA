package Tests;

import LMS.Librarian;
import LMS.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Librarian class.
 */
public class LibrarianTest {

    @BeforeEach
    void resetState() {
        // Reset static state to ensure test isolation
        Librarian.currentOfficeNumber = 0;
        Library.librarian = null;
        Library.persons = new ArrayList<>();
    }

    @Test
    @DisplayName("LIB-01: Auto office assignment gives sequential office numbers")
    void constructorAutoOffice_assignsSequentialNumbers() {
        Librarian l1 = new Librarian(-1, "Alice", "Addr", 123, 1000.0, -1);
        Librarian l2 = new Librarian(-1, "Bob", "Addr", 456, 1100.0, -1);

        assertEquals(0, l1.officeNo);
        assertEquals(1, l2.officeNo);
    }

    @Test
    @DisplayName("LIB-02: Explicit office number is kept as provided")
    void constructorExplicitOffice_usesProvidedNumber() {
        Librarian l = new Librarian(-1, "Clara", "Addr", 789, 1200.0, 15);

        assertEquals(15, l.officeNo);
    }

    @Test
    @DisplayName("LIB-03: First addLibrarian call sets the library librarian")
    void addLibrarian_firstCall_setsLibraryLibrarian() {
        Librarian librarian = new Librarian(-1, "Chief", "Addr", 321, 1300.0, -1);

        boolean added = Librarian.addLibrarian(librarian);

        assertTrue(added);
        assertSame(librarian, Library.librarian);
        assertEquals(1, Library.persons.size());
        assertSame(librarian, Library.persons.get(0));
    }

    @Test
    @DisplayName("LIB-04: Second addLibrarian call fails and does not replace existing librarian")
    void addLibrarian_secondCall_returnsFalseAndDoesNotReplace() {
        Librarian first = new Librarian(-1, "Chief", "Addr", 321, 1300.0, -1);
        Librarian second = new Librarian(-1, "Deputy", "Addr", 654, 1400.0, -1);

        Librarian.addLibrarian(first);
        boolean addedSecond = Librarian.addLibrarian(second);

        assertFalse(addedSecond);
        assertSame(first, Library.librarian);
        assertEquals(1, Library.persons.size());
        assertSame(first, Library.persons.get(0));
    }
}
