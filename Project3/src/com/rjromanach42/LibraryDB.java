//Author: Ricardo Romanach
//Date: November 4, 2022
//Purpose:  This class will handle all the communication with the
//          library database and implement the functions required
//          in the instructions.

package com.rjromanach42;

import java.sql.*;

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
    private Connection login()
    {
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

    public ResultSet getAllBookData() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select ISBN, title, genre.name, date_published, publisher, edition, description from book left join genre on book.genre_id = genre.genre_id order by title;");
    }

    public ResultSet getAllBookCopies() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select book.ISBN, book.title, copy.barcode from book join copy on book.ISBN = copy.ISBN order by title");
    }

    public ResultSet getAuthorsByBook(String title) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select title, first_name, middle_name, last_name from author natural join book natural join book_author where book.title like ? order by title");
        stmt.setString(1, "%" + title + "%");
        return stmt.executeQuery();
    }

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

    public void printAllBookCopies() throws SQLException {
        ResultSet copies = getAllBookCopies();
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
                System.out.print("\tCopy IDs:\t");
            }
            else
            {
                System.out.print(", ");
            }
            System.out.print(copies.getString("barcode"));

            prevTitle = currTitle;
        }
        System.out.println();
    }

    //This function takes in a title and will print out the authors of books that have a similar name, this way you don't have to perfectly type out the name of the book for a result
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
            System.out.println("Author: " +
                    first  + " " +
                    (middle == null ? "" : middle + " ") +
                    last);

            prevTitle = currTitle;
        }
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }
}
