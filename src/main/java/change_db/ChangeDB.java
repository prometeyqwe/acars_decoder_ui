package change_db;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class ChangeDB extends JFrame {
    private JPanel mainPanel;
    private JPanel listRelation;
    private JPanel viewRelation;
    private JList relationList;
    private DefaultListModel relationListModel;

    private final String db_name = "acars_db";
    private final String USER = "root";
    private final String PASSWORD = "root";//это пароль и логин по умолчанию

    private Connection connection = null;

    private ResultSet resultSelectDB;
    private ResultSet resultSelectRelation;
    private ResultSetMetaData resultSelectRelationMD;
    private ResultSet resultSearch;
    private ArrayList<String> nameOfColumns;

    private String relationName;
    private int countColumnsOfTable;

    private DefaultTableModel dataTableOfRelation = null;
    private JTable tableOfRelation = null;

    private static boolean isOpen = false;
    private static boolean isOpenRelation = false;
    private int index;


    public ChangeDB() {//конструктор класса
        setResizable(false);
        setSize(950, 600);
        setLocationRelativeTo(null);
        startGUI();
        setVisible(true);
        //подключение БД-------------------------------

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acars_db?useSSL=false",
                    USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Не удалось соединиться с БД");
        }
        openDB();

        //---------------------------------------------
    }

    public void startGUI() {
        viewRelation = new JPanel();
        relationListModel = new DefaultListModel();
        relationListModel.addElement("**************Список отношений*************\n");
        relationList = new JList(relationListModel);
        relationList.setFocusable(false);
        dataTableOfRelation = new DefaultTableModel();
        //слушатель на список таблиц-------------------
        relationList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {

                index = e.getLastIndex();
                changeList();
            }
        });
        //---------------------------------------------
        //---------------------------------------------

        tableOfRelation = new JTable(dataTableOfRelation); //создание таблицы(отношения)

        //интерфейс------------------------------------
        mainPanel = new JPanel();
        listRelation = new JPanel();
        mainPanel.setLayout(null);

        listRelation.setLayout(new BoxLayout(listRelation, BoxLayout.Y_AXIS));
        listRelation.setBounds(0, 0, 250, 559);
        listRelation.setBackground(Color.BLACK);

        viewRelation.setLayout(new BoxLayout(viewRelation, BoxLayout.Y_AXIS));
        viewRelation.setBounds(250, 0, 700, 559);
        viewRelation.setBackground(Color.red);
        viewRelation.add(new JScrollPane(tableOfRelation), BorderLayout.CENTER);


        listRelation.add(new JScrollPane(relationList), BorderLayout.CENTER);
        mainPanel.add(listRelation);
        mainPanel.add(viewRelation);
        add(mainPanel);
        //---------------------------------------------

        /*-------------------------------***MENU_BAR***-----------------------------------------------------------------------*/

        JMenuBar main_menu = new JMenuBar();

        JMenu dbMenu = new JMenu("Управление БД");

        JMenu relMenu = new JMenu("Управление отношением");
        JMenuItem searchRel = new JMenuItem("Поиск по отношению");// поиск по не ключевому полю
        searchRel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isOpenRelation) {
                    searchOfRel();
                } else JOptionPane.showMessageDialog(null, "Отношение не выбрано");
            }
        });

        JMenuItem delOfRel = new JMenuItem("Удаление по выбранному полю");
        delOfRel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delOfRelFunc();
            }
        });

        JMenuItem newRel = new JMenuItem("Добавить данные");// добавление новых данных
        newRel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newRelFunc();
            }
        });

        final JMenuItem changeRel = new JMenuItem("Обновить кортеж");// обновление кортежа
        changeRel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeRelFunc();
            }
        });

        relMenu.add(searchRel);
        relMenu.add(delOfRel);
        relMenu.add(newRel);
        relMenu.add(changeRel);

        main_menu.add(dbMenu);
        main_menu.add(relMenu);
        setJMenuBar(main_menu);// добавление иеню на менюбар
        /*-------------------------------***//// MENU_BAR***-----------------------------------------------------------------------*/
    }

    public void changeList() {//обновление таблицы
        dataTableOfRelation.setColumnCount(0);
        dataTableOfRelation.setNumRows(0);
        relationName = "";
        nameOfColumns = new ArrayList<String>();
        if (isOpen) {
            relationName = relationListModel.getElementAt(index).toString();
            Statement statementRelation = null;
            try {
                isOpenRelation = true;
                statementRelation = connection.createStatement();
                resultSelectRelation = statementRelation.executeQuery("SELECT * FROM " + relationName);
                resultSelectRelationMD = resultSelectRelation.getMetaData();
                countColumnsOfTable = resultSelectRelationMD.getColumnCount();
                int i = 1;
                while (i <= countColumnsOfTable) {
                    nameOfColumns.add(resultSelectRelationMD.getColumnName(i));
                    dataTableOfRelation.addColumn(nameOfColumns.get(i - 1));
                    i++;
                }
                while (resultSelectRelation.next()) {
                    Vector arr = new Vector();
                    for (int j = 0; j < nameOfColumns.size(); j++) {
                        arr.add(resultSelectRelation.getString(nameOfColumns.get(j)));
                    }
                    dataTableOfRelation.addRow(arr);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void openDB() {//открытие БД
        try {
            relationList.clearSelection();
            relationListModel.removeAllElements();
            relationListModel.addElement("**************Список отношений*************\n");
            relationList.setSelectedIndex(0);
            Statement preparedStatement = connection.createStatement();
            preparedStatement.executeQuery("use " + db_name);
            Statement statement = connection.createStatement();
            resultSelectDB = statement.executeQuery("SHOW TABLES;");
            while (resultSelectDB.next()) {
                relationListModel.addElement(resultSelectDB.getString("Tables_in_" + db_name));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        isOpen = true;

    }

    public void searchOfRel() {//поиск по таблице
        String key = "", value = "";
        key = JOptionPane.showInputDialog(null, "Enter key of search", "Enter key...");
        value = JOptionPane.showInputDialog(null, "Enter value of search", "Enter value...");
        System.out.println(key);
        System.out.println(value);
        try {
            String result = "";
            Statement statement = connection.createStatement();
            try {
                resultSearch = statement.executeQuery("SELECT * FROM " + relationName + " WHERE " + key + " = '" + value + "'");

            } catch (SQLSyntaxErrorException ex) {
                JOptionPane.showMessageDialog(null, "Проверьте правильность ввода ключа поиска");
            }
            try {
                resultSearch.next();
            } catch (NullPointerException ex1) {
            }

            try {
                for (int i = 0; i < countColumnsOfTable; i++) {
                    System.out.println("name" + nameOfColumns.get(i));
                    result += resultSearch.getString(nameOfColumns.get(i)) + "   ";
                }
            } catch (NullPointerException ex) {
            } catch (SQLException ex2) {
                JOptionPane.showMessageDialog(null, "Не найдено");

            }
            if (!result.equals("")) JOptionPane.showMessageDialog(null, result);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void newRelFunc() {//добавление новой записи в таблицу
        if (isOpenRelation) {
            ArrayList<String> newRow = new ArrayList<String>();
            for (int i = 0; i < countColumnsOfTable; i++) {
                newRow.add(JOptionPane.showInputDialog(null, "Введите значение " + (i + 1) + "-го столбца: "));
            }
            try {
                String query = "INSERT INTO" + "`" + relationName + "`" + "(";
                for (int i = 0; i < countColumnsOfTable; i++) {
                    if (i == countColumnsOfTable - 1)
                        query += "`" + nameOfColumns.get(i) + "`) VALUES(";
                    else
                        query += "`" + nameOfColumns.get(i) + "`,";
                }
                for (int j = 0; j < newRow.size(); j++) {
                    if (j != newRow.size() - 1)
                        query += "?,";
                    else
                        query += "?)";
                }
                System.out.println(query);
                PreparedStatement statement = connection.prepareStatement(query);


                for (int i = 0; i < countColumnsOfTable; i++) {
                    statement.setString(i + 1, newRow.get(i));
                }

                try {
                    statement.executeUpdate();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Введите корректрное значение");
                }
                changeList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else JOptionPane.showMessageDialog(null, "Никакое отношение не выбранно");
    }

    public void delOfRelFunc() {//удаление строки из таблицы
        if (isOpenRelation) {
            String key, value;
            JOptionPane.showMessageDialog(null, "Для удаления необходимо ввести ключ и поле для удаления");
            key = JOptionPane.showInputDialog(null, "Введите ключ для удаления");
            value = JOptionPane.showInputDialog(null, "Введите поле для удаления");

            try {
                String query = "DELETE FROM " + "`" + relationName + "`" + "WHERE `" + key + "` = '" + value + "'";
                Statement statement1 = connection.createStatement();
                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
                changeList();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Введите корректные значения");
            }
        } else
            JOptionPane.showMessageDialog(null, "Никакое отношение не выбранно");
    }

    public void changeRelFunc() {//изменение записи в таблице
        if (isOpenRelation) {
            String key;
            String keyTitle;
            String valueChange;
            String valueChangeTitle;
            String query = "";
            keyTitle = JOptionPane.showInputDialog(null, "Введите ключ для изменения");
            key = JOptionPane.showInputDialog(null, "Введите значение ключа для изменения");
            valueChangeTitle = JOptionPane.showInputDialog(null, "Введите значение поле для изменения");
            valueChange = JOptionPane.showInputDialog(null, "Введите значение для изменения");
            try {
                query = "UPDATE " + "`" + relationName + "`" + " SET `" + valueChangeTitle + "` = '" + valueChange + "' WHERE `" + keyTitle + "` =" + "'" + key + "'";
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Введите корректные данные");
            }

            changeList();
        } else
            JOptionPane.showMessageDialog(null, "Никакое отношение не выбранно");
    }
}