// BookTest.java

package Tests;

import LMS.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class BookTest {

    @BeforeEach
    void resetStaticState() {
        // make IDs predictable across tests
        Book.setIDCount(0);

        // ensure Library singleton has safe defaults for any fine/expiry logic
        Library.getInstance().setReturnDeadline(0);
        Library.getInstance().setFine(0.0);
        Library.getInstance().setRequestExpiry(365); // long enough so nothing expires during tests
    }

    private Borrower borrower(int id) {
        return new Borrower(id, "John", "Address", 123456);
    }

    private Staff staff(int id) {
        return new Staff(id, "Staff1", "Office", 999, 1000.0);
    }

    // -------------------------
    // 1) Constructor & ID logic
    // -------------------------

    @Test
    void constructor_autoId_whenIdIsMinusOne_incrementsAndAssigns() {
        Book b1 = new Book(-1, "T1", "S1", "A1", false);
        Book b2 = new Book(-1, "T2", "S2", "A2", false);

        assertEquals(1, b1.getID());
        assertEquals(2, b2.getID());
    }

    @Test
    void constructor_usesProvidedId_whenIdNotMinusOne() {
        Book b = new Book(99, "T", "S", "A", false);
        assertEquals(99, b.getID());
    }

    // -------------------------
    // 2) Basic getters/setters
    // -------------------------

    @Test
    void getters_returnConstructorValues() {
        Book b = new Book(-1, "Clean Code", "Software", "Robert Martin", false);

        assertEquals("Clean Code", b.getTitle());
        assertEquals("Software", b.getSubject());
        assertEquals("Robert Martin", b.getAuthor());
        assertFalse(b.getIssuedStatus());
    }

    @Test
    void setIssuedStatus_updatesIssuedStatus() {
        Book b = new Book(-1, "T", "S", "A", false);

        b.setIssuedStatus(true);
        assertTrue(b.getIssuedStatus());

        b.setIssuedStatus(false);
        assertFalse(b.getIssuedStatus());
    }

    // -------------------------
    // 3) Hold request behaviors
    // -------------------------

    @Test
    void placeBookOnHold_addsHoldRequestToBookAndBorrower() {
        Book b = new Book(-1, "T", "S", "A", false);
        Borrower bor = borrower(1);

        assertEquals(0, b.getHoldRequests().size());
        assertEquals(0, bor.getOnHoldBooks().size());

        b.placeBookOnHold(bor);

        assertEquals(1, b.getHoldRequests().size());
        assertEquals(1, bor.getOnHoldBooks().size());

        HoldRequest hrBookSide = b.getHoldRequests().get(0);
        HoldRequest hrBorSide  = bor.getOnHoldBooks().get(0);

        assertSame(bor, hrBookSide.getBorrower());
        assertSame(b, hrBookSide.getBook());

        assertSame(bor, hrBorSide.getBorrower());
        assertSame(b, hrBorSide.getBook());
    }

    @Test
    void makeHoldRequest_preventsDuplicateRequestFromSameBorrower() {
        Book b = new Book(-1, "T", "S", "A", false);
        Borrower bor = borrower(1);

        b.makeHoldRequest(bor);
        int sizeAfterFirst = b.getHoldRequests().size();

        b.makeHoldRequest(bor); // should NOT add again
        int sizeAfterSecond = b.getHoldRequests().size();

        assertEquals(1, sizeAfterFirst);
        assertEquals(1, sizeAfterSecond);
        assertEquals(1, bor.getOnHoldBooks().size());
    }

    @Test
    void makeHoldRequest_blocksIfBorrowerAlreadyBorrowedSameBook() {
        Book b = new Book(-1, "T", "S", "A", true);
        Borrower bor = borrower(1);

        // Borrower already has a loan for THIS book
        Loan existingLoan = new Loan(bor, b, staff(10), null, new Date(), null, false);
        bor.addBorrowedBook(existingLoan);

        int before = b.getHoldRequests().size();
        b.makeHoldRequest(bor);
        int after = b.getHoldRequests().size();

        assertEquals(before, after);
        assertEquals(0, bor.getOnHoldBooks().size());
    }

    @Test
    void serviceHoldRequest_removesFromBookQueue_andFromBorrowerList() {
        Book b = new Book(-1, "T", "S", "A", false);
        Borrower bor = borrower(1);

        // create exactly one hold request
        b.placeBookOnHold(bor);
        assertEquals(1, b.getHoldRequests().size());
        assertEquals(1, bor.getOnHoldBooks().size());

        HoldRequest hr = b.getHoldRequests().get(0);
        b.serviceHoldRequest(hr);

        assertEquals(0, b.getHoldRequests().size());
        assertEquals(0, bor.getOnHoldBooks().size());
    }

    // -------------------------
    // 4) Return flow (safe path)
    // -------------------------

    @Test
    void returnBook_setsBookNotIssued_setsLoanReturnData_removesLoanFromBorrower() {
        Book b = new Book(-1, "T", "S", "A", true);
        Borrower bor = borrower(1);
        Staff receiver = staff(20);

        // Loan issued today -> computeFine1() should be 0 (deadline/fine set to 0 in setup),
        // so payFine() will NOT ask for input and will set finePaid=true.
        Loan loan = new Loan(bor, b, staff(10), null, new Date(), null, false);
        bor.addBorrowedBook(loan);

        assertTrue(b.getIssuedStatus());
        assertEquals(1, bor.getBorrowedBooks().size());
        assertNull(loan.getReturnDate());
        assertNull(loan.getReceiver());

        b.returnBook(bor, loan, receiver);

        assertFalse(b.getIssuedStatus());
        assertEquals(0, bor.getBorrowedBooks().size());
        assertNotNull(loan.getReturnDate());
        assertSame(receiver, loan.getReceiver());
        assertTrue(loan.getFineStatus()); // should become true when no fine is generated
    }
}
