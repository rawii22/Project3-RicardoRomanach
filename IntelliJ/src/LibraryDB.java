import java.sql.*;
import java.util.Scanner;

public class LibraryDB {
    Connection conn;

    LibraryDB()
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
        Scanner input = new Scanner(System.in);
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();

        //try/catch here to control what it returns
        try {
            test = DriverManager.getConnection("jdbc:mysql://localhost:3306/library",username,password);
        } catch (SQLException e) {
            return null;
        }
        System.out.println("\nSuccess!\n");
        return test;
    }

    void doSomething() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("select * from author");
        res.next();
        System.out.println(res.getString("first_name"));
        stmt.close();
    }

    void closeConnection() throws SQLException {
        conn.close();
    }
}
