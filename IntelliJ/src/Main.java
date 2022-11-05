import java.sql.*;
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