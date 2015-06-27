package code;

import java.sql.*;

public class BD 
{
    final String DRIVER = "com.mysql.jdbc.Driver";
    final String URL = "jdbc:mysql://localhost:3306/Players";
    final String LOGIN = "root";
    final String SENHA = "3426";
    boolean connected = true;
    
    public Connection connection;
    
    public boolean getConnection()
    {
        try{
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL,LOGIN,SENHA);
            return true;
        }catch(SQLException | ClassNotFoundException erro){
            connected = false;
        }
        return false;
    }
    public void close()
    {
        if (connected)
            try {
                connection.close();
            } catch (SQLException ex) { }
    }
}
