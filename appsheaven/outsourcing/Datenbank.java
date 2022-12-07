package appsheaven.outsourcing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class Datenbank {

    private String username;
    private String email;
    private String passwort;

    // Datenbank Login
    private final String url = "jdbc:mysql://localhost:3306/jfxdatabasetest";
    private final String user = "root";
    private final String pass = "";
    private Connection connect;
    private Statement statement;
    private ResultSet rs;

    private String einfuegen;
    private String resultId;
    private String resultUsername;
    private String resultEmail;
    private String resultPass;
    private String dbPass;
    private String dbSelect = "SELECT * FROM jfxdatabasetest.userinfo";

    private Alert dbError = new Alert(Alert.AlertType.ERROR);

    // Verbindungs Status
    public boolean checkDB() {
        try {
            connect = DriverManager.getConnection(url, user, pass);
            statement = connect.createStatement();
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }

    // Verbindung aufbauen & schließen
    public void dbLogin() {
        try {
            connect = DriverManager.getConnection(url, user, pass);
            statement = connect.createStatement();
        } catch (SQLException sqle) {
            dbError.setTitle("Verbindung fehlgeschlagen");
            dbError.setHeaderText("Bei der Verbindung zu der Datenbank ist ein Fehler aufgetreten.\nVersuchen Sie es erneut.");
            dbError.showAndWait();
        }
    }

    public void dbLogout() {
        try {
            connect.close();
            statement.close();
        } catch (SQLException sqle) {
            dbError.setTitle("Verbindung trennen fehlgeschlagen");
            dbError.setHeaderText("Bei dem Versuch die Verbindung zu trennen ist ein Fehler aufgetreten.");
            dbError.showAndWait();
        }
    }

    // Neuen Nutzer erstellen, Login, Passwort Recovery
    public void dbCreateUser() {
        einfuegen = "INSERT INTO `jfxdatabasetest`.`userinfo` (`username`, `email`, `passwort`) VALUES (" + "'" + username +  "'" + "," + "'" + email + "'" + "," + "'" + passwort + "'" +")";
        try {
            dbLogin();
            statement.execute(einfuegen);
            dbLogout();
        } catch (SQLException sqle) {
            dbError.setTitle("Nutzer erstellen fehlgeschlagen");
            dbError.setHeaderText("Bei dem Versuch einen neuen Nutzer zu erstellen ist ein Fehler aufgetreten.\nVersuchen Sie es erneut.");
            dbError.showAndWait();
        }
    }

    public void dbUserLogin() {
        dbPass = "SELECT username, passwort FROM jfxdatabasetest.userinfo WHERE username = " + "'" +  username + "'";
        try {
            dbLogin();
            rs = statement.executeQuery(dbPass);
            while (rs.next()) {
                resultUsername = rs.getString(1);
                resultPass = rs.getString(2);
            }
            dbLogout();
        } catch (SQLException sqle) {
            dbError.setTitle("User erstellen fehlgeschlagen");
            dbError.setHeaderText("Bei dem Versuch einen neuen Nutzer zu erstellen ist ein Fehler aufgetreten.\nVersuchen Sie es erneut.");
            dbError.showAndWait();
        }
    }

    public void dbPassRecovery() {
        dbPass = "SELECT passwort, email FROM jfxdatabasetest.userinfo WHERE email = " + "'" + email + "'";
        try {
            dbLogin();
            rs = statement.executeQuery(dbPass);
            while (rs.next()) {
                resultPass = rs.getString(1);
                resultEmail = rs.getString(2);
            }
            dbLogout();
        } catch (SQLException sqle) {
            dbError.setTitle("Überprüfung fehlgeschlagen");
            dbError.setHeaderText("Bei der Überprufung mit der Datenbank ist ein Fehler aufgetreten.\nVersuchen Sie es erneut");
            dbError.showAndWait();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getResultId() {
        return resultId;
    }

    public String getResultUsername() {
        return resultUsername;
    }

    public String getResultEmail() {
        return resultEmail;
    }

    public String getResultPass() {
        return resultPass;
    }

}