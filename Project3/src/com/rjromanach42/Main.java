//Author: Ricardo Romanach
//Date: November 4, 2022
//Purpose:  This class handles the flow of the whole program and
//          offers some features to users to interact with the
//          library database via Command Line Interface.

package com.rjromanach42;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the library database system!\n");
        LibraryDB library = new LibraryDB();
        Scanner input = new Scanner(System.in);
        int menuChoice = 0;
        final int quit = 13;

        while(menuChoice != quit)
        {
            try
            {
                System.out.println("\nPlease select an option:");
                System.out.println("1.  Display list of all books");
                System.out.println("2.  Get author(s) of book");
                System.out.println("3.  Display list of all book copies");
                System.out.println("4.  Display list of all library members");
                System.out.println("5.  Display list of currently borrowed books");
                System.out.println("6.  Return a book");
                System.out.println("7.  Check out a book");
                System.out.println("8.  Renew a book");
                System.out.println("9.  Get money owed by member");
                System.out.println("10. Get money owed by member per book");
                System.out.println("11. Make payment on late fee");
                System.out.println("12. Logout");
                System.out.println(quit + ". Quit\n");
                System.out.print("> ");

                try
                {
                    menuChoice = Integer.parseInt(input.nextLine());
                    if (menuChoice < 1 || menuChoice > quit)
                    {
                        System.out.println("Please enter a number from the options listed above.");
                        continue;
                    }
                }
                catch(NumberFormatException e)
                {
                    System.out.println("Please enter a number from the options listed above.");
                    continue;
                }

                switch(menuChoice)
                {
                    case 1:
                        library.printAllBookData();
                        break;
                    case 2:
                        System.out.print("Please enter title or ISBN of book: ");
                        library.printAuthorsByBook(input.nextLine());
                        break;
                    case 3:
                        library.printAllBookCopies();
                        break;
                    case 4:
                        library.printAllMembersData();
                        break;
                    case 5:
                        library.printCopiesCurrentlyBorrowed();
                        break;
                    case 6:
                        library.returnBook();
                        break;
                    case 7:
                        library.checkoutBook();
                        break;
                    case 8:
                        library.renewBook();
                        break;
                    case 9:
                        library.printMoneyOwedByMember();
                        break;
                    case 10:
                        library.printFeePerBookByMember(true); // true here to include books that the member has not returned
                        break;
                    case 11:
                        library.makePaymentOnLateFee();
                        break;
                    case 12:
                        try
                        {
                            library.closeConnection();
                            System.out.println("Logged out successfully!\n");
                            library = new LibraryDB();
                        }
                        catch (SQLException e)
                        {
                            System.out.println("SQLException");
                            System.out.println(e);
                        }
                        break;
                    case quit:
                        System.out.println("\nThank you for using the library database system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }
            catch (SQLException e)
            {
                System.out.println("SQL Exception");
                System.out.println(e);
            }
            catch (Exception e)
            {
                System.out.println("Exception");
                System.out.println(e);
            }

            //TODO: Ask for user to press any key to continue.
        }

        try
        {
            library.closeConnection();
            System.out.println("Logged out successfully!\n");
        }
        catch (SQLException e)
        {
            System.out.println("SQLException");
            System.out.println(e);
        }
    }
}