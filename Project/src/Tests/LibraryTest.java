package Tests;

import LMS.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential Unit Tests for Library.java - IMPROVED VERSION
 * SWE 303 Project - Library Management System
 *
 * Coverage:
 * - setName/getLibraryName
 * - getLibrarian
 * - addClerk
 * - searchForBooks (including documented defects + disabled flaky scenario)
 * - viewHistory
 *
 * Notes:
 * - Library uses Singleton + static fields (persons, librarian), so tests must reset global state.
 * - searchForBooks() uses console I/O and mixes Scanner + BufferedReader (environment-dependent behavior).
 */
class LibraryTest {

    private InputStream originalIn;
    private PrintStream originalOut;
    private Library lib;

    @BeforeEach
    void setUp() throws Exception {
        originalIn = System.in;
        originalOut = System.out;

        resetLibrarySingletonAndStatics();

        // Use a fresh instance after reset
        lib = Library.getInstance();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    // ============================================================================
    // HELPERS
    // ============================================================================

    private void setConsoleInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
        action.run();
        return baos.toString(StandardCharsets.UTF_8);
    }

    /**
     * Because Library uses a Singleton + static fields, we reset:
     * - Library.obj (singleton instance)
     * - Library.persons (static)
     * - Library.librarian (static)
     * - books + loans lists
     * - static counters (Person, Book, Clerk, Librarian)
     */
    private void resetLibrarySingletonAndStatics() throws Exception {
        // reset singleton instance
        Field objField = Library.class.getDeclaredField("obj");
        objField.setAccessible(true);
        objField.set(null, null);

        // initialize a new instance so fields exist
        Library instance = Library.getInstance();

        // reset static state
        Library.librarian = null;
        if (Library.persons != null) {
            Library.persons.clear();
        } else {
            Library.persons = new ArrayList<>();
        }

        // reset instance collections
        instance.getBooks().clear();

        Field loansField = Library.class.getDeclaredField("loans");
        loansField.setAccessible(true);
        ((ArrayList<?>) loansField.get(instance)).clear();

        // reset counters
        Person.setIDCount(0);
        Book.setIDCount(0);
        Clerk.currentdeskNumber = 0;
        Librarian.currentOfficeNumber = 0;
    }

    // ============================================================================
    // SUITE 1: setName() and getLibraryName()
    // ============================================================================

    @Test
    @DisplayName("TC1.1: setName and getLibraryName - normal case")
    void testSetName_normalCase() {
        lib.setName("TBU Central Library");
        assertEquals("TBU Central Library", lib.getLibraryName());
    }

    @Test
    @DisplayName("TC1.2: getLibraryName initially null")
    void testGetLibraryName_initialNull() {
        assertNull(lib.getLibraryName());
    }

    @Test
    @DisplayName("TC1.3: setName multiple times - last value persists")
    void testSetName_multipleTimes() {
        lib.setName("First");
        lib.setName("Second");
        lib.setName("Final");
        assertEquals("Final", lib.getLibraryName());
    }
    @Test
    @DisplayName("TC1.4: setName accepts empty string")
    void testSetName_emptyString() {
        lib.setName("");
        assertEquals("", lib.getLibraryName());
    }
    @Test
    @DisplayName("TC1.5: setName preserves whitespace")
    void testSetName_whitespaceOnly() {
        lib.setName("   ");
        assertEquals("   ", lib.getLibraryName());
    }


    // ============================================================================
    // SUITE 2: getLibrarian()
    // ============================================================================

    @Test
    @DisplayName("TC2.1: getLibrarian initially null")
    void testGetLibrarian_initialNull() {
        assertNull(lib.getLibrarian());
    }


    // ============================================================================
    // SUITE 3: addClerk()
    // ============================================================================

    @Test
    @DisplayName("TC3.1: addClerk adds to persons list")
    void testAddClerk_basic() {
        Clerk clerk = new Clerk(101, "Alice Johnson", "789 Office Blvd", 55001, 35000.0, 5);
        int sizeBefore = lib.getPersons().size();

        lib.addClerk(clerk);

        assertEquals(sizeBefore + 1, lib.getPersons().size());
        assertTrue(lib.getPersons().contains(clerk));
    }
    @Test
    @DisplayName("TC3.2: addClerk stores the same object reference")
    void testAddClerk_storesSameReference() {
        Clerk clerk = new Clerk(102, "Bob Smith", "321 Work St", 55002, 40000.0, 10);

        lib.addClerk(clerk);

        Person added = lib.getPersons().get(lib.getPersons().size() - 1);
        assertSame(clerk, added);
    }


    @Test
    @DisplayName("TC3.3: addClerk multiple clerks - size increases by 2 and contains both")
    void testAddClerk_multiple() {
        Clerk clerk1 = new Clerk(201, "Clerk A", "Addr", 60001, 30000.0, 1);
        Clerk clerk2 = new Clerk(202, "Clerk B", "Addr", 60002, 30000.0, 2);

        int sizeBefore = lib.getPersons().size();

        lib.addClerk(clerk1);
        lib.addClerk(clerk2);

        assertEquals(sizeBefore + 2, lib.getPersons().size());
        assertTrue(lib.getPersons().contains(clerk1));
        assertTrue(lib.getPersons().contains(clerk2));
    }

    @Test
    @DisplayName("TC3.4: addClerk allows null (no validation)")
    void testAddClerk_nullAllowed() {
        int sizeBefore = lib.getPersons().size();

        lib.addClerk(null);

        assertEquals(sizeBefore + 1, lib.getPersons().size());
        assertNull(lib.getPersons().get(lib.getPersons().size() - 1));
    }
    @Test
    @DisplayName("TC3.5: addClerk allows duplicate clerk references")
    void testAddClerk_duplicatesAllowed() {
        Clerk clerk = new Clerk(333, "Dup Clerk", "Addr", 11111, 30000.0, 1);
        int sizeBefore = lib.getPersons().size();

        lib.addClerk(clerk);
        lib.addClerk(clerk);

        assertEquals(sizeBefore + 2, lib.getPersons().size());
    }


    // ============================================================================
    // SUITE 4: searchForBooks()
    // ============================================================================

    @Test
    @DisplayName("TC4.1: searchForBooks by title - exact match")
    void testSearchForBooks_titleMatch() throws Exception {
        lib.addBookinLibrary(new Book(1, "Clean Code", "SE", "Robert Martin", false));

        setConsoleInput("1\nClean Code\n");

        ArrayList<Book> result = lib.searchForBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    @DisplayName("TC4.2: searchForBooks by title - no match returns null")
    void testSearchForBooks_noMatch() throws Exception {
        lib.addBookinLibrary(new Book(1, "Clean Code", "SE", "Author", false));

        setConsoleInput("1\nNonexistent\n");

        ArrayList<Book> result = lib.searchForBooks();

        assertNull(result);
    }

    @Test
    @DisplayName("TC4.3: searchForBooks by author - multiple matches")
    void testSearchForBooks_authorMatch() throws Exception {
        lib.addBookinLibrary(new Book(1, "Book 1", "Subject", "Martin Fowler", false));
        lib.addBookinLibrary(new Book(2, "Book 2", "Subject", "Martin Fowler", false));

        setConsoleInput("3\nMartin Fowler\n");

        ArrayList<Book> result = lib.searchForBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("TC4.4: searchForBooks - empty library returns null")
    void testSearchForBooks_emptyLibrary() throws Exception {
        setConsoleInput("1\nAnything\n");

        ArrayList<Book> result = lib.searchForBooks();

        assertNull(result);
    }

    @Test
    @DisplayName("TC4.5: searchForBooks - Observed behavior: case-sensitive exact match")
    void testSearchForBooks_caseSensitive_defect() throws Exception {
        lib.addBookinLibrary(new Book(1, "Clean Code", "SE", "Author", false));

        setConsoleInput("1\nclean code\n");

        ArrayList<Book> result = lib.searchForBooks();

        // DEFECT: user expectations usually are case-insensitive search
        assertNull(result, "KNOWN DEFECT: Search is case-sensitive");
    }

    @Test
    @DisplayName("TC4.6: searchForBooks - Observed behavior: whitespace not trimmed")
    void testSearchForBooks_whitespaceTrim_defect() throws Exception {
        lib.addBookinLibrary(new Book(1, "Clean Code", "SE", "Author", false));

        setConsoleInput("1\n  Clean Code  \n");

        ArrayList<Book> result = lib.searchForBooks();

        // DEFECT: strict equals() means leading/trailing spaces cause a miss
        assertNull(result, "KNOWN : Search does not trim whitespace");
    }

    @Test
    @DisplayName("TC4.7: searchForBooks - issued books are still searchable")
    void testSearchForBooks_issuedBooks() throws Exception {
        lib.addBookinLibrary(new Book(1, "Book A", "Fiction", "Author", false));
        lib.addBookinLibrary(new Book(2, "Book B", "Fiction", "Author", true));

        setConsoleInput("2\nFiction\n");

        ArrayList<Book> result = lib.searchForBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
    @Test
    @DisplayName("TC4.8: searchForBooks by subject - exact match")
    void testSearchForBooks_subjectMatch() throws Exception {
        lib.addBookinLibrary(new Book(1, "Book A", "AI", "Author1", false));
        lib.addBookinLibrary(new Book(2, "Book B", "SE", "Author2", false));

        setConsoleInput("2\nAI\n");

        ArrayList<Book> result = lib.searchForBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AI", result.get(0).getSubject());
    }

    // ============================================================================
    // SUITE 5: viewHistory()
    // ============================================================================

    @Test
    @DisplayName("TC5.1: viewHistory with no loans prints empty message")
    void testViewHistory_noLoans() {
        String output = captureOutput(lib::viewHistory);
        assertTrue(output.contains("No issued books."));
    }

    @Test
    @DisplayName("TC5.2: viewHistory with one unreturned loan shows placeholders")
    void testViewHistory_oneUnreturnedLoan() {
        Borrower borrower = new Borrower(1, "John Doe", "Address", 12345);
        Clerk issuer = new Clerk(2, "Jane Clerk", "Address", 67890, 30000.0, 1);
        Book book = new Book(3, "Test Book", "Fiction", "Author", false);

        lib.addLoan(new Loan(borrower, book, issuer, null, new Date(), null, false));

        String output = captureOutput(lib::viewHistory);

        assertTrue(output.contains("Test Book"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("--"), "Should show placeholders for receiver/return date/fine paid");
    }

    @Test
    @DisplayName("TC5.3: viewHistory with returned loan includes receiver")
    void testViewHistory_returnedLoan() {
        Borrower borrower = new Borrower(1, "Alice", "Addr", 11111);
        Clerk issuer = new Clerk(2, "Issuer", "Addr", 22222, 30000.0, 1);
        Clerk receiver = new Clerk(3, "Receiver", "Addr", 33333, 30000.0, 2);
        Book book = new Book(4, "Returned Book", "Subject", "Author", false);

        lib.addLoan(new Loan(borrower, book, issuer, receiver, new Date(), new Date(), true));

        String output = captureOutput(lib::viewHistory);

        assertTrue(output.contains("Returned Book"));
        assertTrue(output.contains("Receiver"));
    }

    @Test
    @DisplayName("TC5.4: viewHistory with multiple loans shows all entries")
    void testViewHistory_multipleLoans() {
        Borrower b1 = new Borrower(1, "Borrower 1", "Addr", 10001);
        Borrower b2 = new Borrower(2, "Borrower 2", "Addr", 10002);
        Clerk issuer = new Clerk(3, "Issuer", "Addr", 20001, 30000.0, 1);

        Book book1 = new Book(11, "Book One", "S1", "A1", false);
        Book book2 = new Book(12, "Book Two", "S2", "A2", false);

        lib.addLoan(new Loan(b1, book1, issuer, null, new Date(), null, false));
        lib.addLoan(new Loan(b2, book2, issuer, null, new Date(), null, false));

        String output = captureOutput(lib::viewHistory);

        assertTrue(output.contains("Book One"));
        assertTrue(output.contains("Book Two"));
        assertTrue(output.contains("Borrower 1"));
        assertTrue(output.contains("Borrower 2"));
    }

    @Test
    @DisplayName("TC5.5: viewHistory - same borrower multiple loans appears multiple times")
    void testViewHistory_sameBorrowerMultiple() {
        Borrower borrower = new Borrower(1, "John", "Addr", 10001);
        Clerk issuer = new Clerk(2, "Issuer", "Addr", 20001, 30000.0, 1);

        Book book1 = new Book(11, "First Book", "S1", "A1", false);
        Book book2 = new Book(12, "Second Book", "S2", "A2", false);

        lib.addLoan(new Loan(borrower, book1, issuer, null, new Date(), null, false));
        lib.addLoan(new Loan(borrower, book2, issuer, null, new Date(), null, false));

        String output = captureOutput(lib::viewHistory);

        assertTrue(output.contains("First Book"));
        assertTrue(output.contains("Second Book"));

        // simple check: name appears at least twice
        int count = output.split("John", -1).length - 1;
        assertTrue(count >= 2, "Borrower name should appear for each loan entry");
    }
}

