package code;

import java.sql.*;

public class DBWorked {

    private Connection connection;
    private PreparedStatement ps;
    private String login, password;
    private ResultSet resultSet;
    DBWorked(String login, String password){
        this.login = login;
        this.password = password;
    }


    public Connection getConnection(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acars_db?useSSL=false",
                    login, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Не удалось соединиться с БД");
        }
        return connection;
    }

    public ResultSet executeSelect(String[] atributes, String from, String where, String where_value){
        String atributresSTR="";
        Statement statement = null;

        for(int i=0; i <atributes.length; i++){
            atributresSTR+=atributes[i];
            if(i<atributes.length-1)
                atributresSTR+=", ";//это она
        }//подготовил строку для запроса

        String query = "SELECT " + atributresSTR + " FROM " + from + " WHERE " + where + " = ' " + where_value + "';";

        try {
            statement = getConnection().createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

}
