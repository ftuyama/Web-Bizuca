package code;

import java.sql.*;

public class BD 
{
    final String DRIVER = "com.mysql.jdbc.Driver";
    final String URL = "jdbc:mysql://localhost:3306/Players";
    final String LOGIN = "root";
    final String SENHA = "3426";
    
    public Connection connection;
    
    public boolean getConnection()
    {
        try{
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL,LOGIN,SENHA);
            return true;
        }catch(SQLException | ClassNotFoundException erro){}
        return false;
    }
    public void close()
    {
        try {
            connection.close();
        } catch (SQLException ex) { }
    }
}
