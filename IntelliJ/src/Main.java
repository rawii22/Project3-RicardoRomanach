import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library","root","db123");
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("select * from author");
            res.next();
            System.out.println(res.getString("first_name"));

            stmt.close();
            conn.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println("yo this works!");
    }
}
