//Author: Ricardo Romanach
//Date: November 11, 2022
//Purpose:  This class will handle all the communication with the
//          library database and implement the functions required
//          in the instructions.

package com.rjromanach42;

import java.sql.*;
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
    private Connection login()
    {
        Connection test;
        //TODO: Uncomment this for release version
        /*Scanner input = new Scanner(System.in);
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();*/

        //try/catch here to control what it returns
        try {
            //test = DriverManager.getConnection("jdbc:mysql://localhost:3306/library",username,password);
            //TODO: Use the line on top instead for release version
            test = DriverManager.getConnection("jdbc:mysql://localhost:3306/library","root","db123");
        } catch (SQLException e) {
            return null;
        }
        System.out.println("\nSuccess!");
        return test;
    }

    public ResultSet getAllBookData() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("select ISBN, title, genre.name, date_published, publisher, edition, description from book left join genre on book.genre_id = genre.genre_id order by title;");
        return res;
    }

    public void printAllBookData() throws SQLException {
        ResultSet books = getAllBookData();

        /*System.out.printf("%-19s", "ISBN");
        System.out.printf("%-82s", "Title");
        System.out.printf("%-36s", "Genre");
        System.out.printf("%-20s", "Date published");
        System.out.printf("%-40s", "Publisher");
        System.out.printf("%-9s", "Edition");
        System.out.println("Description");*/

        while(books.next())
        {
            /*System.out.printf("%-19s", books.getString("ISBN"));
            System.out.printf("%-82s", books.getString("title"));
            System.out.printf("%-36s", books.getString("name") == null ? "N/A" : books.getString("name"));
            System.out.printf("%-20s", books.getString("date_published"));
            System.out.printf("%-40s", books.getString("publisher"));
            System.out.printf("%-9s", books.getString("edition") + " ");
            System.out.println(books.getString("description") == null ? "N/A" : books.getString("description"));*/

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

    public void closeConnection() throws SQLException {
        conn.close();
    }
}
