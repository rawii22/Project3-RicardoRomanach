//Author: Ricardo Romanach
//Date: November 4, 2022
//Purpose:  This class will handle all the communication with the
//          library database and implement the functions required
//          in the instructions.

package com.rjromanach42;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class LibraryDB {
    Connection conn;

    //Start by asking the user to login.
    public LibraryDB()
    {
        System.out.println("Please login to the library database.");
        while ((conn = login()) == null)
        {
            System.out.println("\nInvalid username or password\n");
        }
    }

    //This method prompts the user for login info and return a connection if successful. Otherwise, it will return null.
    private Connection login() {
        Connection test;
        //TODO: Uncomment this for release version
        /*Scanner input = new Scanner(System.in);
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();*/

        //try-catch here to control what it returns
        try {
            //test = DriverManager.getConnection("jdbc:mysql://localhost:3306/library",username,password);
            //TODO: Use the line on top instead for release version
            test = DriverManager.getConnection("jdbc:mysql://localhost:3306/library","root","db123");
            System.out.println("\nSuccess!");
            return test;
        } catch (SQLException e) {
            return null;
        }
    }


    //----Private functions where SQL is remotely executed.

    //This gets all the books and sorts them by title, and also replaces the genre ID with the actual genre name.
    private ResultSet getAllBookData() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select ISBN, title, genre.name, date_published, publisher, edition, description from book left join genre on book.genre_id = genre.genre_id order by title");
    }

    //This gets every individual copy of each book the library holds matched with the name and barcode.
    private ResultSet getAllBookCopies() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select book.ISBN, book.title, copy.barcode from book join copy on book.ISBN = copy.ISBN order by title");
    }

    //This function will fetch the authors associated with a specified book.
    //It does not require the title or ISBN to match perfectly, so it will match the books with the closest matches (in alphabetical order).
    private ResultSet getAuthorsByBook(String titleOrISBN) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select title, first_name, middle_name, last_name from book left join book_author natural join author on book.ISBN = book_author.ISBN where book.title like ? or book.ISBN like ? order by title");
        stmt.setString(1, "%" + titleOrISBN + "%");
        stmt.setString(2, "%" + titleOrISBN + "%");
        return stmt.executeQuery();
    }

    //This gets all the members in the library ordered by last name.
    private ResultSet getAllMembersData() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select card_no, first_name, middle_name, last_name from member order by last_name");
    }

    //This returns a list of every book that is currently borrowed.
    private ResultSet getCopiesCurrentlyBorrowed() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select book.ISBN, book.title, copy.barcode, borrow.date_borrowed, borrow.renewals_no from borrow join copy join book on borrow.barcode = copy.barcode and copy.ISBN = book.ISBN where date_returned is null order by title");
    }

    //This returns a list of users who currently have a book checked out.
    private ResultSet getMembersBorrowingBook() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select member.card_no, first_name, middle_name, last_name from member join borrow on member.card_no = borrow.card_no where date_returned is null group by card_no order by last_name");
    }

    //Similar to getCopiesCurrentlyBorrowed, except the member ID (card_no) can be specified.
    private ResultSet getCopiesBorrowedByMember(String card_no) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select book.ISBN, book.title, copy.barcode, borrow.date_borrowed, borrow.renewals_no from borrow join copy join book on borrow.barcode = copy.barcode and copy.ISBN = book.ISBN where date_returned is null and card_no = ? order by title");
        stmt.setString(1, card_no);
        return stmt.executeQuery();
    }

    //This updates a specific book copy's date_returned value to be the current date and time, indicating that it has just been returned.
    private void returnBookByMember(String card_no, String barcode) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("update library.borrow set date_returned = current_timestamp() where (date_returned is null and card_no = ? and barcode = ?)");
        stmt.setString(1, card_no);
        stmt.setString(2, barcode);
        stmt.executeUpdate();
    }

    //This checks out a book given a member's card_no and a specific barcode. It will print an error if the book copy is already checked out.
    private void checkoutBookByMember(String card_no, String barcode) throws SQLException {
        PreparedStatement check = conn.prepareStatement("select * from borrow where barcode = ? and date_returned is null");
        check.setString(1, barcode);
        ResultSet bookBorrowed = check.executeQuery();

        if(bookBorrowed.next())
        {
            System.out.println("That book is already checked out!");
            return;
        }

        PreparedStatement stmt = conn.prepareStatement("insert into borrow (card_no, barcode, date_borrowed, renewals_no) values ( ? , ? , current_timestamp() , 0)");
        stmt.setString(1, card_no);
        stmt.setString(2, barcode);
        stmt.executeUpdate();
        System.out.println("Success!");
    }

    //This renews a book give a member's card_no and a specific barcode.
    private void renewBookByMember(String card_no, String barcode) throws SQLException {
        PreparedStatement check = conn.prepareStatement("select * from borrow where card_no = ? and barcode = ? and date_returned is null");
        check.setString(1, card_no);
        check.setString(2, barcode);
        ResultSet bookBorrowed = check.executeQuery();

        if(bookBorrowed.next() && bookBorrowed.getInt("renewals_no") >= 2)
        {
            System.out.println("You cannot renew a book more than two times!");
            return;
        }

        PreparedStatement stmt = conn.prepareStatement("update library.borrow set renewals_no = renewals_no + 1 where (date_returned is null and card_no = ? and barcode = ?)");
        stmt.setString(1, card_no);
        stmt.setString(2, barcode);
        stmt.executeUpdate();

        System.out.println("Successfully renewed!");
    }


    //----Public functions for printing or updating

    //Prints all the data about every book in the library database
    public void printAllBookData() throws SQLException {
        ResultSet books = getAllBookData();

        while(books.next())
        {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Title:\t" + books.getString("title"));
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("\tISBN:\t\t\t" + books.getString("ISBN"));
            System.out.println("\tGenre:\t\t\t" + (books.getString("name") == null ? "N/A" : books.getString("name")));
            System.out.println("\tDate Published:\t" + books.getString("date_published"));
            System.out.println("\tPublisher:\t\t" + books.getString("publisher"));
            System.out.println("\tEdition:\t\t" + books.getString("edition") + " ");
            System.out.println("\tDescription:\t" + (books.getString("description") == null ? "N/A" : books.getString("description")));
            System.out.println();
        }
    }

    //Lists out every book in the database followed by a list of every copy the library owns (by barcode ID).
    public void printAllBookCopies() throws SQLException {
        printCopies(getAllBookCopies());
    }

    /**
     * Generic function that prints copies of books, specifically the title,
     * followed by the ISBN and copy IDs (The title of a book is not repeated if there is more than one copy,
     * however the ResultSet must be ordered by the book title for this to work).
     * @param copies - This ResultSet REQUIRES columns of name "title", "ISBN", and "barcode"
     * @return List containing barcodes of specified book copies
     * @throws SQLException
     */
    public List printCopies(ResultSet copies) throws SQLException {
        List copyList = new ArrayList();
        String prevTitle = "";
        String currTitle;

        while(copies.next()) {
            currTitle = copies.getString("title");
            if (!currTitle.equals(prevTitle)) //This prevents the title of a book from being printed many times if it has more than one author
            {
                System.out.println("\n\n-----------------------------------------------------------------------------------");
                System.out.println("Title:\t" + currTitle);
                System.out.println("-----------------------------------------------------------------------------------");
                System.out.println("\tISBN:\t\t" + copies.getString("ISBN"));
                System.out.print("\tCopy ID(s):\t");
            }
            else
            {
                System.out.print(", ");
            }
            System.out.print(copies.getString("barcode"));
            copyList.add(copies.getString("barcode"));

            prevTitle = currTitle;
        }
        System.out.println();
        return copyList;
    }

    //This function takes in a title or ISBN and will print out the authors of books that have a similar name or ISBN,
    //this way you don't have to perfectly type out the name of the book for a result
    public void printAuthorsByBook(String titleOrISBN) throws SQLException {
        ResultSet authors = getAuthorsByBook(titleOrISBN);
        String prevTitle = "";
        String currTitle;

        while(authors.next())
        {
            String first = authors.getString("first_name");
            String middle = authors.getString("middle_name");
            String last = authors.getString("last_name");
            currTitle = authors.getString("title");

            if (!currTitle.equals(prevTitle)) //This prevents the title of a book from being printed many times if it has more than one author
            {
                System.out.println("\n-----------------------------------------------------------------------------------");
                System.out.println("Title:\t" + currTitle);
                System.out.println("-----------------------------------------------------------------------------------");
            }
            if(first == null && middle == null && last == null)
            {
                System.out.println("No author are specified.");
            }
            else
            {
                System.out.println("Author: " +
                        first + " " +
                        (middle == null ? "" : middle + " ") +
                        last);
            }

            prevTitle = currTitle;
        }
    }

    //This function prints out the data about every member in the database
    public void printAllMembersData() throws SQLException {
        ResultSet members = getAllMembersData();

        System.out.println("Members by last name:\n");
        System.out.printf("%-23s", "Last name:");
        System.out.printf("%-23s", "First name:");
        System.out.printf("%-23s", "Middle name:");
        System.out.println("Card ID:");
        while(members.next())
        {
            System.out.printf("%-23s", members.getString("last_name"));
            System.out.printf("%-23s", members.getString("first_name"));
            System.out.printf("%-23s", members.getString("middle_name") == null ? "N/A" : members.getString("middle_name"));
            System.out.println(members.getString("card_no"));
        }
    }

    /**
     * Generic function that prints out the names of members in the format #. last_name, first_name middle_name
     * each on their own line. (# starts with 1, so any given member's card_no can be accessed with cardIDs[#-1])
     * @param members - This ResultSet REQUIRES columns of name "card_no", "first_name", "middle_name", and "last_name"
     * @return List containing the card_no's of each member in the order they were printed.
     * @throws SQLException
     */
    public List printMembers(ResultSet members) throws SQLException {
        List cardIDs = new ArrayList();
        Integer count = 1;
        while(members.next())
        {
            System.out.println(count + ". " + members.getString("last_name") + ", " +
                    members.getString("first_name") + " " +
                    (members.getString("middle_name") == null ? "" : members.getString("middle_name")));
            cardIDs.add(members.getString("card_no"));
            count++;
        }
        if (cardIDs.size() <= 0) {
            System.out.println("No members in the library!");
        }
        return cardIDs;
    }

    //This prints all book copies that are currently borrowed
    public void printCopiesCurrentlyBorrowed() throws SQLException {
        ResultSet copies = getCopiesCurrentlyBorrowed();

        while(copies.next())
        {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Title:\t" + copies.getString("title"));
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("\tISBN:\t\t\t\t\t" + copies.getString("ISBN"));
            System.out.println("\tBarcode:\t\t\t\t" + copies.getString("barcode"));
            System.out.println("\tDate borrowed:\t\t\t" + copies.getString("date_borrowed"));
            System.out.println("\tNumber of renewals:\t\t" + copies.getString("renewals_no"));
            System.out.println();
        }
    }

    //This function allows a user to return a book. It first prints a list of members who have books checked out
    //and asks the user to select a member. Then, it prints a list of books currently borrowed by that member
    //and asks the user to select a book to return.
    public void returnBook() throws SQLException {
        List cardIDs;
        List borrowedCopies;
        String member;
        String copy;

        //print list of users currently borrowing books
        System.out.println("Please select a member for whom to return a book:");
        cardIDs = printMembers(getMembersBorrowingBook());
        if (cardIDs.size() <= 0)
        {
            System.out.println("No books are checked out!");
            return;
        }

        //choose a user
        member = chooseMember(cardIDs); //associate the user's choice with the corresponding member card_no

        //print list of copies borrowed by chosen member (via card_no)
        System.out.println("This member has borrowed these books:");
        borrowedCopies = printCopies(getCopiesBorrowedByMember(member));

        //choose a book barcode from the list to return
        System.out.println("\nPlease enter the Copy ID of the book you would like to return (or type \"exit\" to go back):");
        copy = chooseCopy(borrowedCopies);
        if (copy == null)
        {
            return;
        }

        //return the book and print message
        returnBookByMember(member, copy);
        System.out.println("Success!");
    }

    //This function allows a user to check out a book. It first prints the list of members and asks the user to
    //specify which member wants to check out a book. Then, it prints the list of books in the library, and asks
    //the user to select which copy ID they would like to check out.
    public void checkoutBook() throws SQLException {
        List cardIDs;
        String member;
        String copy;

        //print list of users
        System.out.println("Please select the member checking out a book:");
        cardIDs = printMembers(getAllMembersData());
        if (cardIDs.size() <= 0)
        {
            return;
        }

        //choose a user
        member = chooseMember(cardIDs);

        //print list of all copies and ask user to choose copy ID
        System.out.println("\nPlease enter the Copy ID of the book you would like to check out (or type \"exit\" to go back):");
        copy = chooseCopy(printCopies(getAllBookCopies()));
        if (copy == null)
        {
            return;
        }

        //check out the book
        checkoutBookByMember(member, copy);
    }

    //This function allows a user to renew a book they are currently borrowing. It starts by listing the members
    //who are currently borrowing a book. After choosing a member, it will print out the book that that member is
    // currently borrowing. Then it will ask the user to select which copy they would like to renew.
    public void renewBook() throws SQLException {
        List cardIDs;
        List borrowedCopies;
        String member;
        String copy;

        //print list of users currently borrowing books
        System.out.println("Please select a member for whom to renew a book:");
        cardIDs = printMembers(getMembersBorrowingBook());
        if (cardIDs.size() <= 0)
        {
            System.out.println("No books are checked out!");
            return;
        }

        //choose a user
        member = chooseMember(cardIDs);

        //print list of copies borrowed by chosen member (via card_no)
        System.out.println("This member has borrowed these books:");
        borrowedCopies = printCopies(getCopiesBorrowedByMember(member));

        //choose a book barcode from the list to renew
        System.out.println("\nPlease enter the Copy ID of the book you would like to renew (or type \"exit\" to go back):");
        copy = chooseCopy(borrowedCopies);

        if (copy == null)
        {
            return;
        }

        //renew the book and print message
        renewBookByMember(member, copy);
    }


    //----Private helper and validation functions

    /**
     * This asks the user to enter a number that is within the size of the specified List. Once the user enters a
     * valid number, it will return the corresponding member card_no.
     * @param cardIDs - A list of card_no's for the user to select from
     * @return
     */
    private String chooseMember(List cardIDs) {
        Scanner input = new Scanner(System.in);
        Integer choice = -1;
        boolean validate = true;

        System.out.println("\nPlease enter a number:");
        while(validate) {
            try {
                System.out.print("> ");
                //TODO: create case here in case user wants to exit. store input as string first, check if "exit" and return, otherwise try to parse it
                choice = Integer.parseInt(input.nextLine());
                if (choice < 1 || choice > cardIDs.size()) {
                    System.out.println("Please enter a number from the options listed above.");
                    continue;
                }
                validate = false;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from the options listed above.");
            }
        }

        return cardIDs.get(choice-1).toString();
    }

    /**
     * This asks the user to enter a copy ID that is in the specified List. Once the user enters a valid copy ID,
     * it will return that copy ID. If they choose to exit, it will return null.
     * @param copies - List of copies for the user to select from
     * @return
     */
    private String chooseCopy(List copies) {
        Scanner input = new Scanner(System.in);
        boolean validate = true;
        String copy = "";

        while(validate)
        {
            System.out.print("> ");
            copy = input.nextLine();
            if(copy.toLowerCase(Locale.ROOT).equals("exit"))
            {
                return null;
            }
            if(copies.indexOf(copy) == -1)
            {
                System.out.println("Please choose a valid copy ID:");
                continue;
            }
            validate = false;
        }

        return copy;
    }

    //This function should be called whenever this class is done being used
    public void closeConnection() throws SQLException {
        conn.close();
    }
}
