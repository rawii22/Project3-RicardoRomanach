//Author: Ricardo Romanach
//Date: November 11, 2022
//Purpose:  This class handles the flow of the whole program and
//          offers some features to users to interact with the
//          library database via Command Line Interface.

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try
        {
            LibraryDB library = new LibraryDB();
            library.doSomething();

            //TODO: Create CLI

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