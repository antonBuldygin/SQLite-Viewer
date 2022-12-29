package viewer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreation {

    String createDoctorTable = "CREATE TABLE IF NOT EXISTS doctor(" +
            "idDoc BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "doctorName VARCHAR(255)  NOT NULL, " +
//            "CONSTRAINT docName_unique UNIQUE (doctorName)" +
            ")";

    String createAppointmentTable = "CREATE TABLE  IF NOT EXISTS appointment (" +
            "idApp BIGINT AUTO_INCREMENT PRIMARY KEY, idD BIGINT NOT NULL, idP BIGINT NOT NULL, dat DATE, time TIME, " +
            "FOREIGN KEY (idD) REFERENCES doctor (idDoc), FOREIGN KEY (idP) REFERENCES patient (idP) )";

    String createPatientTable = "CREATE TABLE IF NOT EXISTS patient(" +
            "idP BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "patientName VARCHAR(255) NOT NULL )";

    public static final String INSERT_DOCTOR = "INSERT INTO DOCTOR (idDoc, doctorName) VALUES(?,?)";
    public static final String INSERT_PATIENT = "INSERT INTO PATIENT (patientName) VALUES(?)";
    public static final String INSERT_APPOINMENT = "INSERT INTO appointment (idD, idP, dat, time) VALUES(?,?,?,?)";

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    Connection connection;

    public DatabaseCreation(Connection connection) {
        this.connection = connection;
    }


    public void createTablesofDB() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(createDoctorTable);
        statement.execute(createPatientTable);
        statement.execute(createAppointmentTable);

    }

    public void insertNewDoctor(String docName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_DOCTOR);
        for (int i = 1; i < 5; i++) {


            statement.setString(2, "Charlie Sheen123345"+i);
            statement.setLong(1, 5 + i);
            statement.execute();
        }
    }


}
