package code;
import java.sql.*;

public class Data 
{
    private BD bd = new BD();
    public PreparedStatement command;
    public ResultSet result;
    
    public boolean connected;
    
    public boolean connect(){
        connected = bd.getConnection();
        return connected;
    }
    public void close(){
        bd.close();
    }
    public void show ()
    {
        try 
        {
            String sql = "SELECT * FROM players";
            command = bd.connection.prepareStatement(sql);
            result = command.executeQuery();
        } catch (SQLException ex) {}
    }

    public void addPlayer (String login, String senha)
    {
        try 
        {
            String sql = "INSERT INTO players(login, senha, Njogos) VALUES (?,?,?)";
            command = bd.connection.prepareStatement(sql);
            command.setString(1, login);
            command.setString(2, senha);
            command.setString(3, "1");
            command.execute();
            command.close();
        } catch (SQLException erro) {}
    }
    
    public void NjogosPlusPlus (String N, String nick, String senha)
    {
        try {   
            String sql = "UPDATE players SET Njogos = ? WHERE login = ? AND senha = ? ";
            command = bd.connection.prepareStatement(sql);
            command.setString(1, ""+(Integer.parseInt(N)+1));
            command.setString(2, nick);
            command.setString(3, senha);
            command.execute();
            command.close();
        } catch (SQLException erro) {}
    }
    
    public int contain (String nick, String pass)
    {
        String Njogos = "0";
        if (connected)
        {
            try {
                String sql = "SELECT Njogos FROM players WHERE login = ? AND senha = ?";
                command = bd.connection.prepareStatement(sql);
                command.setString(1, nick);
                command.setString(2, pass);
                result = command.executeQuery();
                if (result.next())
                {
                    Njogos = result.getString("Njogos");
                    NjogosPlusPlus(Njogos, nick, pass);
                }
                else addPlayer(nick, pass);
                result.close();
                command.close();
            }
            catch (SQLException erro) {
                System.out.println(erro);
            }
        }
        return Integer.parseInt(Njogos);
    }
}
