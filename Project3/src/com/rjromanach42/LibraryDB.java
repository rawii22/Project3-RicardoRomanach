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
    //It does not require the title to match perfectly, so it will match the books with the closest matches (in alphabetical order).
    private ResultSet getAuthorsByBook(String title) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select title, first_name, middle_name, last_name from book left join book_author natural join author on book.ISBN = book_author.ISBN where book.title like ? order by title");
        stmt.setString(1, "%" + title + "%");
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

    //This should update a specific book copy's date_returned value to be the current date and time, indicating that it has just been returned
    private void returnBookByMember(String card_no, String barcode) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("update library.borrow set date_returned = current_timestamp() where (date_returned is null and card_no = ? and barcode = ?)");
        stmt.setString(1, card_no);
        stmt.setString(2, barcode);
        stmt.executeUpdate();
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
     * however the ResultSet must be ordered by the book title for this to work.
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

    //This function takes in a title and will print out the authors of books that have a similar name,
    //this way you don't have to perfectly type out the name of the book for a result
    public void printAuthorsByBook(String title) throws SQLException {
        ResultSet authors = getAuthorsByBook(title);
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

    //This function allows a user to return a book. It first prints a list of users who have books checked out
    //and asks the user to select a member. Then, it prints a list of books currently borrowed by that member
    //and asks the user to select a book to return.
    public void returnBook() throws SQLException {
        Scanner input = new Scanner(System.in);
        List cardIDs = new ArrayList();
        List borrowedCopies = new ArrayList();
        String member = "";
        String copy = "";
        Integer count;
        Integer choice = -1;
        boolean validate = true;

        //print list of users currently borrowing books
        System.out.println("Please select a member for whom to return a book:");
        cardIDs = printMembers(getMembersBorrowingBook());
        if (cardIDs.size() <= 0)
        {
            System.out.println("No books are checked out!");
            return;
        }
        count = cardIDs.size();

        //choose a user and validate input
        System.out.println("\nPlease enter a number:");
        while(validate) {
            try {
                System.out.print("> ");
                choice = Integer.parseInt(input.nextLine());
                if (choice < 1 || choice > count) {
                    System.out.println("Please enter a number from the options listed above.");
                    continue;
                }
                validate = false;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from the options listed above.");
            }
        }

        //print list of copies borrowed by chosen user (via card_no)
        System.out.println("This member has borrowed these books:");
        member = cardIDs.get(choice-1).toString(); //associate the user's choice with the corresponding member card_no
        borrowedCopies = printCopies(getCopiesBorrowedByMember(member));

        //choose a book barcode to return and validate input
        System.out.println("\nPlease enter the Copy ID of the book you would like to return (or type \"exit\" to go back):");
        validate = true;
        while(validate)
        {
            System.out.print("> ");
            copy = input.nextLine();
            if(copy.toLowerCase(Locale.ROOT).equals("exit"))
            {
                return;
            }
            if(borrowedCopies.indexOf(copy) == -1)
            {
                System.out.println("Please choose a copy that the selected member has borrowed:");
                continue;
            }
            validate = false;
        }
        returnBookByMember(member, copy);
        System.out.println("Success!");
    }

    //This function should be called whenever this class is done being used
    public void closeConnection() throws SQLException {
        conn.close();
    }
}
