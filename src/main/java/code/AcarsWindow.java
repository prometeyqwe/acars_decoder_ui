package code;

import change_db.ChangeDB;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.DDEMLException;
import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;

import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

//интерфейс
public class AcarsWindow{

    private static int i;
    private String[] result_decod;

    JFrame frame;
    DefaultListModel m1;
    JList list1;
    JLabel aircraft_reg;
    JLabel aircraft_name;
    JLabel aircraft_airline;
    JLabel message_label;
    JLabel flight_id;
    JLabel flight_id_from;
    JLabel flight_id_to;
    JLabel image_aircraft;
    ImageIcon icon_image_aircraft_label;
    ImageIcon extend_icon = new ImageIcon("./images/aircraft_Image/big/deafault.jpg");
    DBWorked worked;
    String acars_message;


    JButton extendImage;// кнопка, увеличивающая картинку


    public AcarsWindow(){
        frame = new JFrame();
        worked = new DBWorked("root", "root");
        startGUI();
    }

    /*-----------------------------***DECODER***--------------------------------------------------------------------------*/
    private String[] decoder(String qwe){
        String s1="";
        String s2="";
        String s3="";
        String s1_result="";
        String s2_result="";
        String s3_result="";
        String[] words = qwe.split("\n");
        String[] result;
        for(int ii=0; ii < words.length; ii++){
            if(words[ii].contains("Aircraft reg:")) {
                s1 = words[ii];
            }
            else if(words[ii].contains("Flight id:")) {
                s2 = words[ii];
            }
            else if(words[ii].contains("Message label:")) {
                s3 = words[ii];
            }
        }

        String ss1[] = s1.split(" ");
        String ss2[] = s2.split(" ");
        String ss3[] = s3.split(" ");

        for(int j=0; j < ss1.length-2; j++){
            if(ss1[j].equals("Aircraft") && ss1[j+1].equals("reg:")){
                s1_result=ss1[j+2];
            }
        }
        for(int j=0; j < ss2.length-2; j++){
            if(ss2[j].equals("Flight") && ss2[j+1].equals("id:")){
                s2_result=ss2[j+2];
            }
        }
        for(int j=0; j < ss3.length-2; j++){
            if(ss3[j].equals("Message") && ss3[j+1].equals("label:")){
                s3_result=ss3[j+2];
            }
        }

        result= new String[]{s1_result, s2_result, s3_result};
        return result;
    }
    /*--------------------------------------------------------------------------------------------------------------------*/

    public void startGUI() {
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setSize(1100,599);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*------------------------------***INTERFACE_APP***-------------------------------------------------------------------*/
        JPanel main_panel = new JPanel();
        JPanel list_panel = new JPanel();

        main_panel.setLayout(null);
        main_panel.setBackground(Color.blue);
        list_panel.setLayout(new BoxLayout(list_panel, BoxLayout.Y_AXIS));
        list_panel.setBounds(0,0,550,563);

        m1 = new DefaultListModel();
        m1.addElement("************************************Список сообщений******************************************************\n");
//
        list1 = new JList(m1);
        list1.setSelectedIndex(0);
        list1.setFocusable(false);
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                result_decod = decoder(m1.getElementAt(e.getLastIndex()).toString());

                ResultSet resultSetAircraftReg = worked.executeSelect(new String[]{"aircraft_type", "image_aircraft", "airline", "aircraft_name"}, "type_aircraft", "aircraft_reg", result_decod[0]);
                ResultSet resultSetFlightId = worked.executeSelect(new String[]{"from_from", "to_to"}, "type_flight_id", "flight_id", result_decod[1]);
                ResultSet resultSetMessage = worked.executeSelect(new String[]{"message"}, "type_message", "code", result_decod[2]);

                try {
                    if(resultSetAircraftReg.next()) {
                        aircraft_reg.setText("Aircraft_reg: " + result_decod[0] + " :  " + resultSetAircraftReg.getString("aircraft_type"));
                        aircraft_airline.setText("Aircraft_airline: "+ resultSetAircraftReg.getString("airline"));

                        if(!resultSetAircraftReg.getString("image_aircraft").equals(" ")){
                            message_label.setBounds(10, 412, 400,20);
                            flight_id.setBounds(10, 442, 400,20);
                            int numberImage1 = resultSetAircraftReg.getInt("image_aircraft");
                            ImageIcon imIc = new ImageIcon("./images/aircraft_Image/"+numberImage1+".jpg");
                            image_aircraft.setIcon(imIc);
                        }
                        else {
                            image_aircraft.setIcon(icon_image_aircraft_label);
                            message_label.setBounds(10, 412, 400,20);
                            flight_id.setBounds(10, 442, 400,20);
                            flight_id_from.setBounds(25, 472,500,20);
                            flight_id_to.setBounds(25, 502,500,20);

                        }

                        if(!(resultSetAircraftReg.getString("aircraft_name").equals(" "))) {
                            aircraft_name.setText("Aircraft_name: " + resultSetAircraftReg.getString("aircraft_name"));
                            message_label.setBounds(10, 412, 400,20);
                            flight_id.setBounds(10, 442, 400,20);
                            flight_id_from.setBounds(25, 472,500,20);
                            flight_id_to.setBounds(25, 502,500,20);
                        }
                        else {
                            aircraft_name.setText(" ");
                            message_label.setBounds(10, 382, 400, 30);
                            flight_id.setBounds(10, 412, 400, 30);
                            flight_id_from.setBounds(25, 442,500,20);
                            flight_id_to.setBounds(25, 472,500,20);
                        }
                    }
                    else {
                        message_label.setBounds(10, 412, 400,20);
                        flight_id.setBounds(10, 442, 400,20);
                        flight_id_from.setBounds(25, 472,500,20);
                        flight_id_to.setBounds(25, 502,500,20);
                        image_aircraft.setIcon(icon_image_aircraft_label);
                        aircraft_reg.setText("Aircraft_reg: " + result_decod[0] +" :  Значнеие не определено");
                        aircraft_name.setText("Aircraft_name: "+"Значние не определено");
                        aircraft_airline.setText("Aircraft_airline: "+"Значние не определено");
                    }


                    if(resultSetFlightId.next()){
                        flight_id.setText("Flight_id: "+result_decod[1] + " :  ");
                        flight_id_from.setText("from: " + resultSetFlightId.getString("from_from"));
                        flight_id_to.setText("to: " + resultSetFlightId.getString("to_to"));
                    }
                    else{
                        flight_id.setText("Flight_id: "+result_decod[1]+" :  Значнеие не определено");
                        flight_id_from.setText("from :  Значнеие не определено");
                        flight_id_to.setText("to :  Значнеие не определено");
                    }


                    if(resultSetMessage.next())
                        message_label.setText("Message_label: "+result_decod[2]+" :  "+resultSetMessage.getString("message"));
                    else
                        message_label.setText("Message_label: " +result_decod[2]+ " :  Значение не определено");

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        list_panel.add(new JScrollPane(list1), BorderLayout.CENTER);
        main_panel.add(list_panel);
        /*--------------------------------------------------------------------------------------------------------------------*/

        /*-------------------------------***MENU_BAR***-----------------------------------------------------------------------*/
        JMenuBar main_menu = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        main_menu.add(fileMenu);

        JMenuItem fileOpen = new JMenuItem("Открыть");
        fileMenu.add(fileOpen);
        fileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen1 = new JFileChooser();
                int ret = fileopen1.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileopen1.getSelectedFile()))))
                    {
                        StringBuilder main_line = new StringBuilder();
                        String line;
                        while (true) {
                            while((line = reader.readLine())!=null&&line.contains("--------------")==false){
                                main_line.append(line+"\n");
                            }
                            if(line==null) break;
                            m1.addElement(main_line.toString());
                            main_line.delete(0,main_line.length());
                        }
                    }
                    catch(IOException ex){
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });

        JMenuItem fileSave = new JMenuItem("Сохранить");
        fileMenu.add(fileSave);
        fileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                System.out.println("");
                if ( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ){
                    try (FileWriter fw = new FileWriter(fc.getSelectedFile()+".txt", false) ){
                        {
                            System.out.println(fc.getSelectedFile()+".txt");
                            for(int i = 1; i < m1.getSize(); i++ ){
                                fw.write("\n"+m1.getElementAt(i).toString());
                                fw.write("------------------------------------------");
                                fw.write("\n");
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        JMenuItem clearSpisok = new JMenuItem("Очистить");
        fileMenu.add(clearSpisok);
        clearSpisok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int k = m1.getSize();
                for(int i =m1.getSize()-1; i > 0; i--){
                    m1.remove(i);
                }
            }
        });

        fileMenu.addSeparator();

        final JMenuItem exitAll = new JMenuItem("Выход");
        fileMenu.add(exitAll);

        exitAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JMenu settingRun = new JMenu("Управление");
        main_menu.add(settingRun);

        JMenuItem runScan = new JMenuItem("Начать сканирование");
        settingRun.add(runScan);
        runScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //dde_connection();
            }
        });

        JMenuItem stopScan = new JMenuItem("Остановить сканирование");
        settingRun.add(stopScan);
        stopScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //dde_disconnection();
            }
        });

        JMenuItem changeDB = new JMenuItem("Изменение БД");
        changeDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChangeDB();
            }
        });
        main_menu.add(changeDB);


        frame.setJMenuBar(main_menu);
        /*--------------------------------------------------------------------------------------------------------------------*/

        JPanel right_panel = new JPanel();
        right_panel.setBounds(550,0,550,556);

        right_panel.setLayout(null);

        aircraft_reg = new JLabel();
        aircraft_name = new JLabel();
        aircraft_airline = new JLabel();
        message_label = new JLabel();
        flight_id = new JLabel();
        flight_id_from = new JLabel();
        flight_id_to = new JLabel();
        image_aircraft = new JLabel();
        icon_image_aircraft_label = new ImageIcon("./images/aircraft_Image/deafault.jpg");
        extendImage = new JButton();

        aircraft_reg.setText("Aircraft_reg: ");
        aircraft_reg.setBounds(10, 322, 400,20);

        aircraft_airline.setText("Aircraft_airline: ");
        aircraft_airline.setBounds(10, 352, 400, 20);

        aircraft_name.setText("Aircraft_name: ");
        aircraft_name.setBounds(10, 382, 400, 20);


        message_label.setText("Message_label: ");
        message_label.setBounds(10, 412, 400,20);

        flight_id.setText("Flight_id: ");
        flight_id.setBounds(10, 442, 400,20);

        flight_id_from.setText("from: ");
        flight_id_from.setBounds(25, 472, 500, 20);


        flight_id_to.setText("to: ");
        flight_id_to.setBounds(25,502, 500, 20);

        image_aircraft.setIcon(icon_image_aircraft_label);
        image_aircraft.setBounds(10,10,523,301);

        extendImage.setBounds(420, 320, 113, 20);
        extendImage.setText("Увеличить");
        extendImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame qwe = new JFrame();
                JPanel qweqwe = new JPanel();
                qweqwe.setBackground(Color.BLACK);
                qwe.setContentPane(qweqwe);
                qwe.setVisible(true);
                qwe.setSize(1200,800);
                qwe.setResizable(false);
                qwe.setLocationRelativeTo(null);
                qwe.setLayout(null);
                JLabel extendImg = new JLabel();
                extendImg.setIcon(icon_image_aircraft_label);
                extendImg.setBounds(0,0, 1200, 800);
                qweqwe.add(extendImg);

                ResultSet resultSetAircraftReg = null;
                if(result_decod!=null){
                    resultSetAircraftReg = worked.executeSelect(new String[]{"image_aircraft", }, "type_aircraft", "aircraft_reg", result_decod[0]);
                    try {
                        if (resultSetAircraftReg.next()) {
                            if (!resultSetAircraftReg.getString("image_aircraft").equals(" ")) {
                                int numberImage1 = resultSetAircraftReg.getInt("image_aircraft");
                                extend_icon = new ImageIcon("./images/aircraft_Image/big/" + numberImage1 + ".jpg");
                                extendImg.setIcon(extend_icon);
                            }
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                else{
                    extend_icon = new ImageIcon("./images/aircraft_Image/big/deafault.jpg");
                    extendImg.setIcon(extend_icon);

                }
            }
        });

        right_panel.add(image_aircraft);
        right_panel.add(aircraft_reg);
        right_panel.add(aircraft_name);
        right_panel.add(aircraft_airline);
        right_panel.add(message_label);
        right_panel.add(flight_id);
        right_panel.add(flight_id_from);
        right_panel.add(flight_id_to);
        right_panel.add(extendImage);

        main_panel.add(right_panel);
        /*--------------------------------------------------------------------------------------------------------------------*/
        frame.add(main_panel);
        frame.setVisible(true);
        /*--------------------------------------------------------------------------------------------------------------------*/
    }
//
//
//
//    /*------------------------------***DDE_CONNECTING***------------------------------------------------------------------*/
    public void dde_connection(){
        try
        {
            // DDE client
            final DDEClientConversation conversation = new DDEClientConversation();
            // We can use UNICODE format if server prefers it
            //conversation.setTextFormat(ClipboardFormat.CF_UNICODETEXT);

            conversation.setEventListener(new DDEClientEventListener()
            {
                public void onDisconnect()
                {
                    System.out.println("onDisconnect()");
                }

                public void onItemChanged(String topic, String item, String data)
                {
                    System.out.println("onItemChanged(" + topic + "," + item + "," + data.trim() + ")");
                    acars_message = data.trim();
                    m1.addElement(acars_message);
                    int index = m1.size() - 1;
                    list1.setSelectedIndex(index);
                    list1.ensureIndexIsVisible(index);

                }
            });

            System.out.println("Connecting...");
            conversation.connect("ANAD", "ACARS");
            try
            {
                conversation.startAdvice("LiveData");

                System.out.println("Press Enter to quit");
                System.in.read();

                conversation.stopAdvice("LiveData");
            }
            finally
            {
                conversation.disconnect();
            }
        }
        catch (DDEMLException e)
        {
            System.out.println("DDEMLException: 0x" + Integer.toHexString(e.getErrorCode()) + " " + e.getMessage());
        }
        catch (DDEException e)
        {
            System.out.println("DDEClientException: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e);
        }
    }
///*--------------------------------------------------------------------------------------------------------------------*/
}
