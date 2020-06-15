/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import notesfx.model.Note;

/**
 *
 * @author RIZAL
 */
public class JdbcService {
    private Connection connection;
    private PreparedStatement statement;
    public Connection getConnection(){
        try {
            if (connection == null) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:notes.db");
            }else{
                if (connection.isClosed()) {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:notes.db");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
        return connection;
    }
    public void close(){
        if (connection != null) {
            try {
                connection.close();
                System.out.println("closing connection session");
            } catch (SQLException ex) {
                Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void createDB(){
        try {
            String createNote = "CREATE TABLE IF NOT EXISTS `note` (" +
                "  id integer primary key autoincrement," +
                "  title varchar," +
                "  txt varchar," +
                "  url_picture varchar," +
                "  color_bg varchar," +
                "  color_txt varchar," +
                "  is_picture varchar," +
                "  x integer," +
                "  y integer," +
                "  width integer," +
                "  height integer" +
                ")";
            statement = getConnection().prepareStatement(createNote);
            statement.executeUpdate();
            addSize();
        } catch (Exception e) {
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private void addSize(){
        try {
            String createWidth = "ALTER TABLE `note`"
                    + " ADD COLUMN width integer";
            statement = getConnection().prepareStatement(createWidth);
            statement.executeUpdate();
            String createHeight = "ALTER TABLE `note`"
                    + " ADD COLUMN height integer";
            statement = getConnection().prepareStatement(createHeight);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public void clearDB(){
        try {
            String deleteNote = "delete from note";
            statement = getConnection().prepareStatement(deleteNote);
            statement.executeUpdate();
            getConnection().close();
        } catch (Exception e) {
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public Note saveNote(Note n){
        try {
            String sql;
            boolean insert = false;
            if (n.getId() > 0) {
                sql = "update note set title=?, txt=?, url_picture=?, color_bg=?, color_txt=?, "
                        + "is_picture=?, x=?, y=?, width=?, height=? where id=?";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, n.getTitle());
                statement.setString(2, n.getText());
                statement.setString(3, n.getUrl_picture());
                statement.setString(4, n.getColor_bg());
                statement.setString(5, n.getColor_txt());
                statement.setString(6, n.getIs_picture());
                statement.setInt(7, n.getX());
                statement.setInt(8, n.getY());
                statement.setInt(9, n.getWidth());
                statement.setInt(10, n.getHeight());
                statement.setInt(11, n.getId());
            }else{
                insert = true;
                sql = "insert into note values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                statement = getConnection().prepareStatement(sql);
                statement.setString(1, n.getTitle());
                statement.setString(2, n.getText());
                statement.setString(3, n.getUrl_picture());
                statement.setString(4, n.getColor_bg());
                statement.setString(5, n.getColor_txt());
                statement.setString(6, n.getIs_picture());
                statement.setInt(7, n.getX());
                statement.setInt(8, n.getY());
                statement.setInt(9, n.getWidth());
                statement.setInt(10, n.getHeight());
            }
            statement.executeUpdate();
            if (insert) {
                statement = getConnection().prepareStatement("select last_insert_rowid()");
                Note p = new Note();
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {             
                    p.setId(rs.getInt("last_insert_rowid()"));
                }
                return p;
            }
        } catch (Exception e) {
            System.out.println("e :"+e.getMessage());
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
        return n;
    }
    public void deleteNote(int id){
        try {
            String sql = "delete from note where id=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("e :"+e.getMessage());
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public List<Note> getNotes(){
        try {
            statement = getConnection().prepareStatement("select * from note order by id asc"); 
            List<Note> notes = new ArrayList<>();
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {                
                Note p = new Note();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setText(rs.getString("txt"));
                p.setColor_bg(rs.getString("color_bg"));
                p.setColor_txt(rs.getString("color_txt"));
                p.setUrl_picture(rs.getString("url_picture"));
                p.setIs_picture(rs.getString("is_picture"));
//                System.out.println(p.getUrl_picture()+", "+p.getIs_picture());
                p.setX(rs.getInt("x"));
                p.setY(rs.getInt("y"));
                p.setWidth(rs.getInt("width"));
                p.setHeight(rs.getInt("height"));
                notes.add(p);
            }
            return notes;
        } catch (Exception e) {
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
        return new ArrayList<>();
    }
}

