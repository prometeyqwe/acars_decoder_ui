package code;

import java.sql.SQLException;

//мэйн, обработчик сообщения(мб)
public class Acars {

    public static void main(String[] args){
       AcarsWindow acars = new AcarsWindow();
       acars.dde_connection();
    }

}
