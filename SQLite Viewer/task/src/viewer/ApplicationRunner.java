package viewer;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ApplicationRunner {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {


        try {
            SwingUtilities.invokeAndWait(SQLiteViewer::new);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    }

