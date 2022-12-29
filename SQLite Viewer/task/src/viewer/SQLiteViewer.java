package viewer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.List;

public class SQLiteViewer extends JFrame {
    private static JButton openFileButton;
    private static JTextComponent fileNameTextField;
    private static JButton executeQueryButton;
    private static JComboBox<String> comboBox;
    private static JTextComponent queryTextArea;
    private static List<String> table1 = new ArrayList<>();
    private static String url = "jdbc:sqlite:";
    private static Connection connection;


    public SQLiteViewer() {
        super("SQLite Viewer");


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 2000);
        setLayout(null);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

//        table.getModel().addTableModelListener(new CustomListener());
        table.setName("Table");
//        table.setVisible(true);
        JScrollPane spp = new JScrollPane(table);
        spp.setBounds(10, 200, 500, 400);
        add(spp);
        spp.setVisible(true);

        openFileButton = new JButton("Open");
        openFileButton.setBounds(450, 20, 100, 30);
        openFileButton.setName("OpenFileButton");
        System.out.println(openFileButton.getName());
        add(openFileButton);

        fileNameTextField = new JTextField();
        fileNameTextField.setName("FileNameTextField");
        fileNameTextField.setBounds(10, 20, 400, 30);

        add(fileNameTextField);

        queryTextArea = new JTextArea();
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setBounds(10, 100, 350, 70);
        queryTextArea.setEnabled(false);
        add(queryTextArea);

        executeQueryButton = new JButton("Execute");
        executeQueryButton.setBounds(450, 100, 100, 30);
        executeQueryButton.setName("ExecuteQueryButton");
        executeQueryButton.setEnabled(false);
        System.out.println(executeQueryButton.getName());
        add(executeQueryButton);


        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);


        comboBox = new JComboBox<>();
        comboBox.setBounds(10, 60, 500, 30);
        comboBox.setName("TablesComboBox");
        System.out.println(comboBox.getName());
        comboBox.setVisible(true);
        comboBox.setEditable(true);
        add(comboBox);

// for openFileButton
        extracted();


        comboBox.addActionListener(actionEvent -> {
            if(queryTextArea.isEnabled()){
            queryTextArea.setText("SELECT * FROM " + (String) comboBox.getSelectedItem() + ";");
            executeQueryButton.setEnabled(true);
        }});


        executeQueryButton.addActionListener(e -> {

            String name = queryTextArea.getText();
            System.out.println(name);
            Statement statement;

            try {
                statement = connection.createStatement();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            DatabaseCreation creation = new DatabaseCreation(connection);
//            try {
//                creation.insertNewDoctor("dfe") ;
//            } catch (SQLException ee) {
//                throw new RuntimeException(ee);
//            }

            Object[] columns;
            Object[][] data;

            List<List<Object>> listOfObjects = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery(name)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int numberOfColumns = metaData.getColumnCount();
                columns = new Object[numberOfColumns];

                for (int i = 0; i < numberOfColumns; i++) {
                    columns[i] = metaData.getColumnName(i + 1);
                    System.out.println("out: " + metaData.getColumnName(i + 1));
                }

            } catch (Exception ex) {
                throw new RuntimeException("Failed to findAll  "
                        + "error =" + ex.toString(), ex);

            }


            DefaultTableModel model1 = new DefaultTableModel();
            model1.setColumnIdentifiers(columns);

            try (ResultSet resultSet = statement.executeQuery(name)) {
                ResultSetMetaData metaData = resultSet.getMetaData();

                int numberOfColumns = metaData.getColumnCount();
                int rowsCount = 0;
                while (resultSet.next()) {
                    rowsCount++;
                    List<Object> objects = new ArrayList<>();
                    for (int i = 0; i < numberOfColumns; i++) {

                        System.out.print(" out: " + resultSet.getObject(metaData.getColumnName(i + 1)));
                        objects.add(resultSet.getObject(metaData.getColumnName(i + 1)));
                        System.out.println();
                    }
                    listOfObjects.add(objects);

                }

                data = new Object[rowsCount][numberOfColumns];
                int count = 0;
                for (List<Object> item : listOfObjects) {
                    for (int i = 0; i < item.size(); i++) {
                        data[count][i] = item.get(i);
                    }
                    count++;
                }


                int it = 0;
                for (Object[] row : data) {

                    model1.addRow(row);
                    it++;
                }

            } catch (Exception ex) {
                throw new RuntimeException("Failed to findAll  "
                        + "error =" + ex.toString(), ex);

            }

            table.setModel( model1 );
//            model1.fireTableDataChanged();

        });
    }


    private static Connection connect(String name) {
        try {
            connection = DriverManager.getConnection(url + name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return connection;
    }

    public static void extracted() {

        openFileButton.addActionListener(e -> {
            List<String> tables = new ArrayList<>();

            String name = fileNameTextField.getText();
            boolean exists = Files.exists(Path.of(name));
            if(!exists){ JOptionPane.showMessageDialog(new Frame(), "ERROR MESSAGE");
                queryTextArea.setText("");
                queryTextArea.setEnabled(false);

                executeQueryButton.setEnabled(false);return; }
            queryTextArea.setEnabled(true);
            System.out.println(name);


            Statement statement;
            if (name != null && name.trim().length() > 0) {
                connection = connect(name);
            }
            try {
                statement = connection.createStatement();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try (ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master " +
                    "WHERE type ='table' AND name NOT LIKE 'sqlite_%'")) {


                while (resultSet.next()) {

                    tables.add(resultSet.getString(1));
//                    System.out.println("out: " + resultSet.getString(1));


                }

            } catch (Exception ex) {
                throw new RuntimeException("Failed to findAll  "
                        + "error =" + ex.toString(), ex);

            }

            comboBox.removeAllItems();

            for (int i = 0; i < tables.size(); i++) {
                System.out.println(tables.get(i));

                comboBox.addItem(tables.get(i));
            }


        });
    }

}

class CustomListener implements TableModelListener {
    @Override
    public void tableChanged(TableModelEvent e) {
        System.out.println("Table Updated!");

    }
}
