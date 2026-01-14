package LMS.Tests;

import LMS.Clerk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Clerk class.
 */
public class ClerkTest {

    @BeforeEach
    void resetDeskCounter() {
        // reset the static desk counter so auto‑assignment starts at 0
        Clerk.currentdeskNumber = 0;
    }

    @Test
    void constructorAutoDesk_assignsSequentialNumbers() {
        Clerk c1 = new Clerk(-1, "Alice", "Addr", 123, 500.0, -1);
        Clerk c2 = new Clerk(-1, "Bob", "Addr", 456, 600.0, -1);
        assertEquals(0, c1.deskNo);
        assertEquals(1, c2.deskNo);
    }

    @Test
    void constructorExplicitDesk_usesProvidedNumber() {
        Clerk c = new Clerk(-1, "Clara", "Addr", 789, 700.0, 10);
        assertEquals(10, c.deskNo);
    }

    @Test
    void getSalary_returnsAssignedSalary() {
        Clerk c = new Clerk(-1, "Dave", "Addr", 111, 550.0, -1);
        assertEquals(550.0, c.getSalary());
    }
}

