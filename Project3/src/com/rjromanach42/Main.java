//Author: Ricardo Romanach
//Date: November 11, 2022
//Purpose:  This class handles the flow of the whole program and
//          offers some features to users to interact with the
//          library database via Command Line Interface.

package com.rjromanach42;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try
        {
            System.out.println("Welcome to the library database system!\n");
            LibraryDB library = new LibraryDB();
            Scanner input = new Scanner(System.in);
            int menuChoice = 0;

            while(menuChoice != 2)
            {
                System.out.println("\nPlease select an option:");
                System.out.println("1. Display list of all books.");
                System.out.println("2. Quit\n");
                System.out.print("> ");

                try
                {
                    menuChoice = Integer.parseInt(input.nextLine());
                    if (menuChoice < 1 || menuChoice > 2)
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
                        System.out.println("\nThank you for using the library database system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }

            library.closeConnection();
        }
        catch (SQLException e)
        {
            System.out.println("SQLException");
            System.out.println(e);
        }
        catch (Exception e)
        {
            System.out.println("Exception");
            System.out.println(e);
        }
    }
}