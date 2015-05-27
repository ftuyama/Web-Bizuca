package code;
import java.sql.*;

public class BancoDados 
{
    final String DRIVER = "com.mysql.jdbc.Driver";
    final String URL = "jdbc:mysql://localhost:3306/players";
    
    public boolean conecta(){
        try{
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL,"root","3426");
            connection.close();
            return true;
        }catch(SQLException erro){}
        catch(ClassNotFoundException erro){}
        return false;
    }
}
