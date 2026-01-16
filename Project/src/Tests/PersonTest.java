package Tests;

import LMS.Borrower;
import LMS.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Person behavior via a concrete subclass (Borrower),
 * because Person is abstract.
 *
 * Covers:
 * - constructor ID logic (auto vs explicit)
 * - password initialization based on id
 * - getters/setters for name/address/phone
 * - static ID counter (setIDCount)
 */
public class PersonTest {

    @BeforeEach
    void resetIdCounter() {
        Person.setIDCount(0);
    }

    @Test
    @DisplayName("PER-01: Auto-ID assigns sequential IDs when id = -1")
    void constructor_autoId_assignsSequentialIds() {
        Borrower p1 = new Borrower(-1, "A", "Addr", 111);
        Borrower p2 = new Borrower(-1, "B", "Addr", 222);

        assertEquals(1, p1.getID());
        assertEquals(2, p2.getID());
    }

    @Test
    @DisplayName("PER-02: Explicit ID is used when id != -1 (even if counter increments)")
    void constructor_explicitId_isUsed() {
        Borrower p = new Borrower(99, "Alice", "Addr", 111);

        assertEquals(99, p.getID());
    }

    @Test
    @DisplayName("PER-03: Password is always initialized to String.valueOf(id)")
    void constructor_passwordEqualsIdString() {
        Borrower auto = new Borrower(-1, "Auto", "Addr", 111);
        assertEquals(Integer.toString(auto.getID()), auto.getPassword());

        Borrower explicit = new Borrower(77, "Explicit", "Addr", 222);
        assertEquals("77", explicit.getPassword());
    }

    @Test
    @DisplayName("PER-04: Name/Address/Phone getters return constructor values")
    void getters_returnConstructorValues() {
        Borrower p = new Borrower(-1, "Alice", "Street 1", 55555);

        assertEquals("Alice", p.getName());
        assertEquals("Street 1", p.getAddress());
        assertEquals(55555, p.getPhoneNumber());
    }

    @Test
    @DisplayName("PER-05: setName updates the name")
    void setName_updatesName() {
        Borrower p = new Borrower(-1, "Old", "Addr", 111);
        p.setName("New");

        assertEquals("New", p.getName());
    }

    @Test
    @DisplayName("PER-06: setAddress updates the address")
    void setAddress_updatesAddress() {
        Borrower p = new Borrower(-1, "Alice", "Old Addr", 111);
        p.setAddress("New Addr");

        assertEquals("New Addr", p.getAddress());
    }

    @Test
    @DisplayName("PER-07: setPhone updates the phone number")
    void setPhone_updatesPhone() {
        Borrower p = new Borrower(-1, "Alice", "Addr", 111);
        p.setPhone(99999);

        assertEquals(99999, p.getPhoneNumber());
    }

    @Test
    @DisplayName("PER-08: setIDCount affects the next auto-generated ID")
    void setIDCount_affectsNextAutoId() {
        Person.setIDCount(10);

        Borrower p = new Borrower(-1, "Alice", "Addr", 111);

        // Person constructor increments currentIdNumber first, then assigns it when id == -1
        assertEquals(11, p.getID());
        assertEquals("11", p.getPassword());
    }
}
