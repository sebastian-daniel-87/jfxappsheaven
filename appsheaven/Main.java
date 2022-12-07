package appsheaven;

import appsheaven.outsourcing.Datenbank;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Random;


public class Main extends Application {

    Datenbank db = new Datenbank();

    private static BorderPane bpAppHeavensStart, bpAdminBereich;

    private static VBox vbWilkommen;

    private static Stage stageAppsHeaven, hilfeUeber;

    private static Scene szeneAppsHeaven, szeneUeber;

    private static TabPane tabRoot;
    private static Tab tabRegistry;
    private static Tab tabRecovery;

    private final SeparatorMenuItem spProfil = new SeparatorMenuItem();
    private final SeparatorMenuItem spHilfe = new SeparatorMenuItem();

    // Alarm deklaration
    private static final Alert error = new Alert(Alert.AlertType.ERROR);
    private static final Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    private static final Alert warning = new Alert(Alert.AlertType.WARNING);

    private static Label logPassRecovery;

    // Text und Password Felder
    private static TextField logUserTF, regUserTF, regEmailTF, recEmailTF;
    private static PasswordField logPasswortPF, regPasswortPF, regPassWdhPF;

    private static int spielerZaehler;
    private static int computerZaehler;
    private static int unentschiedenZaehler;


    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();
        root.setTop(tabLogReg());
        root.setBottom(dbStatus());

        primaryStage.setTitle("Apps-Heaven Login");
        primaryStage.setScene(new Scene(root, 500, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Apps-Heaven Login, Registry & Passwort vergessen?
    public TabPane tabLogReg() {
        tabRoot = new TabPane();
        tabRoot.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabLogin = new Tab("Login");
        tabRegistry = new Tab("Registrieren");
        tabRecovery = new Tab("Passwort vergessen?");

        tabRoot.getTabs().addAll(tabLogin, tabRegistry);
        tabLogin.setContent(gpLogin());
        tabRegistry.setContent(gpRegistry());
        tabRecovery.setContent(gpRecovery()); // Wird nur aktiv wenn "Passwort vergessen?" geklickt wird.

        return tabRoot;
    }

    public GridPane gpLogin() {
        GridPane gpLogin = new GridPane();
        gpLogin.setPadding(new Insets(30.0, 0.0, 50.0, 0.0));
        gpLogin.setAlignment(Pos.CENTER);
        gpLogin.setHgap(10.0);
        gpLogin.setVgap(10.0);

        // Unvermeidbar :(
        // Login Labels
        Label logLabel = new Label("Login");
        logLabel.setFont(new Font(25.0));
        Label logUser = new Label("Benutzername:");
        logUserTF = new TextField();
        logUserTF.setAlignment(Pos.CENTER);
        logUserTF.setPromptText("Benutzername");
        logUserTF.setTooltip(new Tooltip("Hier müssen Sie den bei der Registration angegebenen Benutzernamen eingeben."));
        Label logPasswort = new Label("Passwort:");
        logPasswortPF = new PasswordField();
        logPasswortPF.setAlignment(Pos.CENTER);
        logPasswortPF.setPromptText("Passwort");
        logPasswortPF.setTooltip(new Tooltip("Hier müssen Sie das bei der Registration angegebene Passwort eingeben."));
        logPassRecovery = new Label("Passwort vergessen?");

        // Buttons
        Button logButton = new Button("Login");
        logButton.setPrefWidth(190.0);

        logPassRecovery.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                logPassRecovery.setUnderline(true);
            }
        });
        logPassRecovery.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                logPassRecovery.setUnderline(false);
            }
        });
        logPassRecovery.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tabRoot.getTabs().add(tabRecovery);
                tabRecovery.getTabPane().getSelectionModel().select(tabRecovery);
            }
        });

        logButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                db.setUsername(logUserTF.getText());
                db.dbUserLogin();
                if (logUserTF.getText().equals(db.getResultUsername()) && logPasswortPF.getText().equals(db.getResultPass())) {
                    stageAppsHeaven = new Stage();
                    szeneAppsHeaven = new Scene(bpAppsHeavenStart(), 600.0, 600.0);

                    stageAppsHeaven.setTitle("Apps-Heaven");
                    stageAppsHeaven.setScene(szeneAppsHeaven);
                    stageAppsHeaven.show();
                    logUserTF.setText("");
                    logPasswortPF.setText("");
                } else {
                    error.setTitle("Login fehlgeschlagen");
                    error.setHeaderText("Ihre angegebenen Daten sind fehlerhaft.\nBitte überprüfen Sie diese und versuchen Sie es erneut.");
                    error.showAndWait();
                }
            }
        });

        gpLogin.setHalignment(logLabel, HPos.CENTER);
        gpLogin.setHalignment(logUser, HPos.CENTER);
        gpLogin.setHalignment(logPasswort, HPos.CENTER);
        gpLogin.setHalignment(logPassRecovery, HPos.CENTER);

        gpLogin.add(logLabel, 0, 0);
        gpLogin.add(logUser, 0, 1);
        gpLogin.add(logUserTF, 0, 2);
        gpLogin.add(logPasswort, 0,3);
        gpLogin.add(logPasswortPF, 0, 4);
        gpLogin.add(logPassRecovery, 0, 5);
        gpLogin.add(logButton, 0, 6);

        return gpLogin;
    }

    public GridPane gpRegistry() {
        GridPane gpRegistry = new GridPane();
        gpRegistry.setPadding(new Insets(30.0, 0.0, 50.0, 0.0));
        gpRegistry.setAlignment(Pos.CENTER);
        gpRegistry.setHgap(10.0);
        gpRegistry.setVgap(10.0);

        //Registry Labels
        Label regLabel = new Label("Registration");
        regLabel.setFont(new Font(25.0));
        Label regFelder = new Label("Alle Felder müssen ausgefüllt werden!");
        regFelder.setFont(new Font(15.0));
        Label regAnforderung = new Label("Für Anforderung mit dem Mauszeiger auf das jeweilige Feld.");
        regAnforderung.setFont(new Font(13.0));
        Label regUser = new Label("Username:");
        regUserTF = new TextField();
        regUserTF.setPromptText("Username");
        regUserTF.setTooltip(new Tooltip("Ihr Benutzername darf nur aus Groß-, Kleinbuchstaben und Zahlen bestehen!"));
        Label regEmail = new Label("E-Mail:");
        regEmailTF = new TextField();
        regEmailTF.setPromptText("E-Mail");
        regEmailTF.setTooltip(new Tooltip("Geben Sie ruhig eine Wegwerf-Adresse an.\nSollten Sie ihr Passwort verlieren benötigen Sie aber Zugang zu ihrem E-Mail Postfach."));
        Label regPasswort = new Label("Passwort:");
        regPasswortPF = new PasswordField();
        regPasswortPF.setPromptText("Passwort");
        regPasswortPF.setTooltip(new Tooltip("Die Anforderungen für ein Passwort sind:\nmin. 8 Zeichen, min. 1x Groß-, 1x Kleinbuchstaben, 1x Zahl, sowie 1x Sonderzeichen"));
        Label regPassWdh = new Label("Wiederholen:");
        regPassWdhPF = new PasswordField();
        regPassWdhPF.setPromptText("Passwort wiederholen");
        regPassWdhPF.setTooltip(new Tooltip("Das Passwort muss mit dem vorher von Ihnen eingetragenen übereinstimmen."));
        Button regButton = new Button("Registration abschließen");

        regButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String user = regUserTF.getText();
                String email = regEmailTF.getText();
                String passwort = regPasswortPF.getText();
                if (regUserTF.getText().isEmpty() || regEmailTF.getText().isEmpty() || regPasswortPF.getText().isEmpty() || regPassWdhPF.getText().isEmpty()) {
                    error.setTitle("Fehler");
                    error.setHeaderText("Alle Felder müssen ausgefüllt werden!");
                    error.showAndWait();
                } else if (!checkUser(user)) {
                    warning.setTitle("Warnung!");
                    warning.setHeaderText("Ihr Username entspricht nicht den Anforderungen.\nIhr Username darf nur aus Buchstaben und Zahlen bestehen.");
                    warning.showAndWait();
                } else if (!checkEmail(email)) {
                    warning.setTitle("Warnung!");
                    warning.setHeaderText("Ihr Email entspricht nicht den Anforderungen.\nIhre E-Mail-Adresse muss eine 'reale E-Mail-Adresse' sein.");
                    warning.showAndWait();
                } else if (!checkPass(passwort)) {
                    warning.setTitle("Warnung!");
                    warning.setHeaderText("Ihr Passwort entspricht nicht den Anforderungen.\nAnforderungen: min 8 Zeichen davon einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl & ein Sonderzeichen");
                    warning.showAndWait();
                } else if (!regPasswortPF.getText().equals(regPassWdhPF.getText())) {
                    error.setTitle("Fehler");
                    error.setHeaderText("Die Passwörter stimmen nicht überein.\nBitte versuchen Sie es erneut.");
                    error.showAndWait();
                } else {
                    db.setUsername(regUserTF.getText());
                    db.setEmail(regEmailTF.getText());
                    db.setPasswort(regPasswortPF.getText());

                    db.dbLogin();
                    db.dbCreateUser();
                    db.dbLogout();

                    confirm.setTitle("Glückwunsch");
                    confirm.setHeaderText("Ihr Benutzer-Account wurde erstellt.\nBitte melden Sie sich im Reiter Login an");
                    confirm.showAndWait();

                    regUserTF.setText("");
                    regEmailTF.setText("");
                    regPasswortPF.setText("");
                    regPassWdhPF.setText("");
                    logUserTF.setFocusTraversable(false);
                    logPasswortPF.setFocusTraversable(false);
                    tabRegistry.getTabPane().getSelectionModel().selectFirst();
                }
            }
        });

        gpRegistry.add(regLabel, 0, 0, 3, 1);
        gpRegistry.add(regFelder, 0,1,3,1);
        gpRegistry.add(regAnforderung, 0, 3, 3, 1);
        gpRegistry.add(regUser, 0,4);
        gpRegistry.add(regUserTF, 1,4);
        gpRegistry.add(regEmail,0,5);
        gpRegistry.add(regEmailTF,1,5);
        gpRegistry.add(regPasswort,0,6);
        gpRegistry.add(regPasswortPF,1,6);
        gpRegistry.add(regPassWdh, 0,7);
        gpRegistry.add(regPassWdhPF, 1, 7);
        gpRegistry.add(regButton, 0, 8, 3, 1);


        gpRegistry.setHalignment(regLabel, HPos.CENTER);
        gpRegistry.setHalignment(regFelder, HPos.CENTER);
        gpRegistry.setHalignment(regAnforderung, HPos.CENTER);
        gpRegistry.setHalignment(regUser, HPos.RIGHT);
        gpRegistry.setHalignment(regEmail, HPos.RIGHT);
        gpRegistry.setHalignment(regPasswort, HPos.RIGHT);
        gpRegistry.setHalignment(regPassWdh, HPos.RIGHT);
        gpRegistry.setHalignment(regButton, HPos.CENTER);

        return gpRegistry;
    }

    public GridPane gpRecovery() {
        GridPane gpRecovery = new GridPane();
        gpRecovery.setPadding(new Insets(30.0, 0.0, 50.0, 0.0));
        gpRecovery.setAlignment(Pos.CENTER);
        gpRecovery.setHgap(10.0);
        gpRecovery.setVgap(10.0);

        // Recovery Labels
        Label recLabel = new Label("Passwort vergessen?");
        recLabel.setFont(new Font(25.0));
        Label recEmail = new Label("Bitte geben Sie Ihre E-Mail Adresse an:");
        recEmailTF = new TextField();
        recEmailTF.setAlignment(Pos.CENTER);
        recEmailTF.setPromptText("E-Mail");
        recEmailTF.setTooltip(new Tooltip("Sie müssen die E-Mail Adresse, mit der Sie sich registriert haben, eingeben."));

        Button recZurueck = new Button("Zurück");
        recZurueck.setPrefWidth(90.0);
        Button recAnfordern = new Button("Passwort anfordern");

        gpRecovery.add(recLabel, 0, 0);
        gpRecovery.add(recEmail, 0,1);
        gpRecovery.add(recEmailTF,0,2);
        gpRecovery.add(recZurueck, 0,3);
        gpRecovery.add(recAnfordern, 0,3);

        recZurueck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tabRoot.getTabs().add(tabRecovery);
                tabRoot.getSelectionModel().select(tabRecovery);
                tabRoot.getTabs().remove(tabRecovery);
                logPassRecovery.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        tabRoot.getTabs().add(2, tabRecovery);
                        tabRecovery.getTabPane().getSelectionModel().select(tabRecovery);
                    }
                });
                tabRecovery.getTabPane().getSelectionModel().selectFirst();
            }
        });

        recAnfordern.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                db.setEmail(recEmailTF.getText());
                db.dbPassRecovery();
                if (recEmailTF.getText().equals(db.getResultEmail())) {
                    confirm.setTitle("Passwort");
                    confirm.setHeaderText("Ihr Passwort lautet: " + db.getResultPass());
                    confirm.showAndWait();

                    recEmailTF.setText("");
                    tabRoot.getTabs().add(tabRecovery);
                    tabRoot.getSelectionModel().select(tabRecovery);
                    tabRoot.getTabs().remove(tabRecovery);
                    logPassRecovery.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            tabRoot.getTabs().add(2, tabRecovery);
                            tabRecovery.getTabPane().getSelectionModel().select(tabRecovery);
                        }
                    });
                    tabRecovery.getTabPane().getSelectionModel().selectFirst();
                } else {
                    error.setTitle("Passwort-Anfrage fehlgeschlagen");
                    error.setHeaderText("Leider ist ein Fehler aufgetreten.\nBitte versuchen Sie es erneut.");
                    error.showAndWait();
                }
            }
        });


        gpRecovery.setHalignment(recLabel, HPos.CENTER);
        gpRecovery.setHalignment(recEmail, HPos.CENTER);
        gpRecovery.setHalignment(recZurueck, HPos.LEFT);
        gpRecovery.setHalignment(recAnfordern, HPos.RIGHT);

        return gpRecovery;
    }

    public HBox dbStatus() {
        HBox dbStatus = new HBox();
        dbStatus.setPadding(new Insets(0.0, 0.0, 30.0, 0.0));
        dbStatus.setSpacing(25.0);

        Label dbStatusLbl = new Label("DB Status");
        dbStatusLbl.setStyle("-fx-font-weight: bold");

        if (db.checkDB()) {
            dbStatusLbl.setText("Datenbank ist Online");
            dbStatusLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: green");
        } else {
            dbStatusLbl.setText("Datenbank ist Offline");
            dbStatusLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: red");
        }

        dbStatus.getChildren().add(dbStatusLbl);

        return dbStatus;
    }
    // Apps-Heaven Login, Registry & Passwort vergessen? - Ende

    // Apps-Heaven Start
    public BorderPane bpAppsHeavenStart() {
        bpAppHeavensStart = new BorderPane();
        bpAppHeavensStart.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));

        bpAppHeavensStart.setTop(ahMenuBar());
        bpAppHeavensStart.setCenter(vbWillkommen());

        return bpAppHeavensStart;
    }

    public MenuBar ahMenuBar() {
        MenuBar ahMenuBar = new MenuBar();

        // Menüs fuer die MenuBar
        Menu profil = new Menu("Profil");
        Menu apps = new Menu("Apps");
        Menu spiele = new Menu("Spiele");
        Menu medien = new Menu("Medien");
        Menu hilfe = new Menu("Hilfe");

        // MenuItems Profil
        MenuItem admin = new MenuItem("Admin-Bereich");
        MenuItem einstellungen = new MenuItem("Einstellungen");
        einstellungen.setStyle("-fx-text-fill: grey");
        MenuItem abmelden = new MenuItem("Abmelden");
        MenuItem beenden = new MenuItem("Beenden");

        // MenuItems Apps
        MenuItem bilderGalerie = new MenuItem("Bilder-Galerie");
        MenuItem laengenBerechnen = new MenuItem("Längen berechnen");

        // MenuItems Spiele
        MenuItem schereSteinPapier = new MenuItem("Schere Stein Papier");
        MenuItem wuerfelSpiel = new MenuItem("Würfelspiel");

        // MenuItems Medien
        MenuItem google = new MenuItem("Google.de");
        MenuItem youtube = new MenuItem("Youtube.com");

        // MenuItems Hilfe
        MenuItem miHilfe = new MenuItem("Hilfe");
        miHilfe.setStyle("-fx-text-fill: grey");
        MenuItem ueber = new MenuItem("Über Apps-Heaven");

        profil.getItems().addAll(admin, einstellungen, abmelden, spProfil, beenden);
        apps.getItems().addAll(bilderGalerie, laengenBerechnen);
        spiele.getItems().addAll(schereSteinPapier, wuerfelSpiel);
        medien.getItems().addAll(google, youtube);
        hilfe.getItems().addAll(miHilfe, spHilfe, ueber);

        // Profil Actions
        admin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(bpAdminBereich());
            }
        });
        abmelden.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stageAppsHeaven.close();
                profil.getItems().removeAll(admin, einstellungen, abmelden, spProfil, beenden);
                medien.getItems().removeAll(google, youtube);
                hilfe.getItems().removeAll(miHilfe, spHilfe, ueber);
            }
        });
        beenden.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        // Apps Actions
        bilderGalerie.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(bpBilderGalerie());
            }
        });
        laengenBerechnen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(bpLaengenBerechnen());
            }
        });

        // Spiele Actions
        schereSteinPapier.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(bpSchereSteinPapier());
            }
        });
        wuerfelSpiel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(bpWuerfelSpiel());
            }
        });

        // Medien Actions
        google.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getHostServices().showDocument("https://www.google.de/");
            }
        });
        youtube.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getHostServices().showDocument("https://www.youtube.com/?hl=de");
            }
        });

        // Hilfe Actions
        ueber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hilfeUeber = new Stage();
                szeneUeber = new Scene(vbUeber(), 400, 300);

                hilfeUeber.setTitle("Über App-Heavens");
                hilfeUeber.setScene(szeneUeber);
                hilfeUeber.show();
            }
        });

        ahMenuBar.getMenus().addAll(profil, apps, spiele, medien, hilfe);

        return ahMenuBar;
    }

    public VBox vbWillkommen() {
        vbWilkommen = new VBox();
        vbWilkommen.setSpacing(20.0);
        vbWilkommen.setAlignment(Pos.CENTER);

        Label lblWillkommen = new Label("Willkommen in Apps-Heaven");
        lblWillkommen.setFont(new Font(30.0));

        Label lblWillkommensText = new Label("Schön das Du hergefunden hast.\nIn diesem Projekt für die BBQ Umschulung\nfindest Du einige spannende Projekte wie zum Beispiel:\nEine Conversion-Maschine,\nLinks zu verschiedenen Medien\nund vieles mehr! (Bald :P)\n\nIch wünsche dir viel Spaß beim erkunden!");
        lblWillkommensText.setTextAlignment(TextAlignment.CENTER);
        lblWillkommensText.setFont(new Font(20.0));


        vbWilkommen.getChildren().addAll(lblWillkommen, lblWillkommensText);

        return vbWilkommen;
    }

    public BorderPane bpAdminBereich() {
        bpAdminBereich = new BorderPane();

        VBox adminTop = new VBox();
        adminTop.setAlignment(Pos.CENTER);
        adminTop.setPadding(new Insets(20.0));

        Label lblAdminBereich = new Label("Admin-Bereich");
        lblAdminBereich.setFont(new Font(30.0));
        Label lblAdminBereichText = new Label("Hier wird der Admin jeden registrierten Benutzer aus der Datenbank sehen können.");

        VBox adminLeft = new VBox(50.0);
        adminLeft.setAlignment(Pos.CENTER);
        adminLeft.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));

        Button adminTableView = new Button("TableView");
        adminTableView.setFocusTraversable(false);
        Button adminListView = new Button("ListView");
        adminListView.setFocusTraversable(false);

        VBox adminCenter = new VBox();
        adminCenter.setAlignment(Pos.CENTER);
        adminCenter.setPadding(new Insets(0.0, 10.0, 0.0, 0.0));

        VBox adminBottom = new VBox();
        adminBottom.setAlignment(Pos.CENTER);
        adminBottom.setPadding(new Insets(20.0));

        Button btnAdminZurueck = new Button("Zurück");

        VBox adminRight = new VBox();
        adminRight.setPrefSize(50.0, 500.0);
        adminRight.setPadding(new Insets(0.0, 30.0, 0.0, 30.0));

        adminTop.getChildren().addAll(lblAdminBereich, lblAdminBereichText);
        adminLeft.getChildren().addAll(adminTableView, adminListView);
        adminBottom.getChildren().addAll(btnAdminZurueck);

        bpAdminBereich.setTop(adminTop);
        bpAdminBereich.setLeft(adminLeft);
        bpAdminBereich.setCenter(adminTV());
        bpAdminBereich.setBottom(adminBottom);
        bpAdminBereich.setRight(adminRight);

        adminTableView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAdminBereich.getChildren().add(adminTV());
            }
        });

        adminListView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAdminBereich.getChildren().add(adminLV());
            }
        });

        btnAdminZurueck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(vbWilkommen);
            }
        });

        return bpAdminBereich;
    }

    public TableView<String> adminTV() {
        TableView<String> adminTV = new TableView<>();

        return adminTV;
    }

    public ListView<String> adminLV() {
        ListView<String> adminLV = new ListView<>();

        return adminLV;
    }

    public BorderPane bpLaengenBerechnen() {
        BorderPane bpLaengenBerechnen = new BorderPane();
        bpLaengenBerechnen.setPadding(new Insets(20.0, 0.0, 100.0, 0.0));

        HBox laengenTop = new HBox();
        laengenTop.setAlignment(Pos.CENTER);

        Label laengenUeberschrift = new Label("Längen berechnen");
        laengenUeberschrift.setFont(new Font(25.0));

        laengenTop.getChildren().add(laengenUeberschrift);


        GridPane laengenCenter = new GridPane();
        laengenCenter.setAlignment(Pos.CENTER);
        laengenCenter.setVgap(20.0);

        Label laengenEingabe = new Label("Eingabe:");
        laengenEingabe.setStyle("-fx-font-weight: bold");
        Label laengenAusgabe = new Label("Ausgabe:");
        laengenAusgabe.setStyle("-fx-font-weight: bold");

        TextField laengenEingabeTF = new TextField();
        laengenEingabeTF.setPromptText("Eingabe");
        laengenEingabeTF.setAlignment(Pos.CENTER);
        laengenEingabeTF.setFocusTraversable(false);

        TextField laengenAusgabeTF = new TextField();
        laengenAusgabeTF.setPromptText("Ausgabe");
        laengenAusgabeTF.setAlignment(Pos.CENTER);
        laengenAusgabeTF.setFocusTraversable(false);

        ObservableList<String> laengen = FXCollections.observableArrayList("Kilometer", "Meter", "Dezimeter", "Zentimeter", "Millimeter");

        ComboBox<String> eingabeBox = new ComboBox<>();
        eingabeBox.getItems().addAll(laengen);
        eingabeBox.getSelectionModel().select(0);

        ComboBox<String> ausgabeBox = new ComboBox<>();
        ausgabeBox.getItems().addAll(laengen);
        ausgabeBox.getSelectionModel().select(1);

        Button berechnen = new Button("Berechnen");

        laengenCenter.setHalignment(laengenEingabe, HPos.CENTER);
        laengenCenter.setHalignment(laengenAusgabe, HPos.CENTER);
        laengenCenter.setHalignment(eingabeBox, HPos.CENTER);
        laengenCenter.setHalignment(ausgabeBox, HPos.CENTER);

        laengenCenter.add(laengenEingabe, 0, 0);
        laengenCenter.add(laengenAusgabe, 3, 0);
        laengenCenter.add(laengenEingabeTF, 0, 1);
        laengenCenter.add(laengenAusgabeTF, 3, 1);
        laengenCenter.add(eingabeBox, 0,2);
        laengenCenter.add(ausgabeBox, 3, 2);
        laengenCenter.add(berechnen, 2, 3);

        berechnen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Double rechnung = Double.valueOf(laengenEingabeTF.getText());
                    if (eingabeBox.getValue().equals("Kilometer") && ausgabeBox.getValue().equals("Kilometer")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung));
                    } else if (eingabeBox.getValue().equals("Kilometer") && ausgabeBox.getValue().equals("Meter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 1000));
                    } else if (eingabeBox.getValue().equals("Kilometer") && ausgabeBox.getValue().equals("Dezimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 10000));
                    } else if (eingabeBox.getValue().equals("Kilometer") && ausgabeBox.getValue().equals("Zentimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 100000));
                    } else if (eingabeBox.getValue().equals("Kilometer") && ausgabeBox.getValue().equals("Millimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 1000000));
                    }

                    else if (eingabeBox.getValue().equals("Meter") && ausgabeBox.getValue().equals("Kilometer")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 1000));
                    } else if (eingabeBox.getValue().equals("Meter") && ausgabeBox.getValue().equals("Meter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung));
                    } else if (eingabeBox.getValue().equals("Meter") && ausgabeBox.getValue().equals("Dezimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 10));
                    } else if (eingabeBox.getValue().equals("Meter") && ausgabeBox.getValue().equals("Zentimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 100));
                    } else if (eingabeBox.getValue().equals("Meter") && ausgabeBox.getValue().equals("Millimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 1000));
                    }

                    else if (eingabeBox.getValue().equals("Dezimeter") && ausgabeBox.getValue().equals("Kilometer")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 10000));
                    }else if (eingabeBox.getValue().equals("Dezimeter") && ausgabeBox.getValue().equals("Meter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 10));
                    }else if (eingabeBox.getValue().equals("Dezimeter") && ausgabeBox.getValue().equals("Dezimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung));
                    }else if (eingabeBox.getValue().equals("Dezimeter") && ausgabeBox.getValue().equals("Zentimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 10));
                    }else if (eingabeBox.getValue().equals("Dezimeter") && ausgabeBox.getValue().equals("Millimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 100));
                    }

                    else if (eingabeBox.getValue().equals("Zentimeter") && ausgabeBox.getValue().equals("Kilometer")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 100000));
                    } else if (eingabeBox.getValue().equals("Zentimeter") && ausgabeBox.getValue().equals("Meter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 100));
                    } else if (eingabeBox.getValue().equals("Zentimeter") && ausgabeBox.getValue().equals("Dezimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 10));
                    } else if (eingabeBox.getValue().equals("Zentimeter") && ausgabeBox.getValue().equals("Zentimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung));
                    } else if (eingabeBox.getValue().equals("Zentimeter") && ausgabeBox.getValue().equals("Millimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung * 10));
                    }

                    else if (eingabeBox.getValue().equals("Millimeter") && ausgabeBox.getValue().equals("Kilometer")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 1000000));
                    } else if (eingabeBox.getValue().equals("Millimeter") && ausgabeBox.getValue().equals("Meter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 1000));
                    } else if (eingabeBox.getValue().equals("Millimeter") && ausgabeBox.getValue().equals("Dezimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 100));
                    } else if (eingabeBox.getValue().equals("Millimeter") && ausgabeBox.getValue().equals("Zentimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung / 10));
                    } else if (eingabeBox.getValue().equals("Millimeter") && ausgabeBox.getValue().equals("Millimeter")) {
                        laengenAusgabeTF.setText(String.valueOf(rechnung));
                    }
                } catch (Exception e) {

                }
            }
        });

        HBox laengenBottom = new HBox();
        laengenBottom.setAlignment(Pos.CENTER);

        Button laengenZurueck = new Button("Zurück");

        laengenZurueck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpAppHeavensStart.setCenter(vbWillkommen());
            }
        });

        laengenBottom.getChildren().add(laengenZurueck);

        bpLaengenBerechnen.setTop(laengenTop);
        bpLaengenBerechnen.setCenter(laengenCenter);
        bpLaengenBerechnen.setBottom(laengenBottom);

        return bpLaengenBerechnen;
    }

    public BorderPane bpBilderGalerie() {
        BorderPane bpBilderGalerie = new BorderPane();
        bpBilderGalerie.setPadding(new Insets(20.0));

        HBox bgTop = new HBox(20.0);
        bgTop.setAlignment(Pos.CENTER);

        Label bgBilderGalerie = new Label("Bilder-Galerie");
        bgBilderGalerie.setAlignment(Pos.CENTER);
        bgBilderGalerie.setFont(new Font("Times New Roman", 40.0));

        bgTop.getChildren().add(bgBilderGalerie);

        ScrollPane sgBottom = new ScrollPane();
        sgBottom.setFitToHeight(true);
        sgBottom.setFitToWidth(true);
        sgBottom.setPrefSize(600, 140);

        GridPane bgBottom = new GridPane();
        bgBottom.setHgap(10.0);

        Image imgBild1 = new Image("images/bild1.jpeg");
        Image imgBild2 = new Image("images/bild2.jpeg");
        Image imgBild3 = new Image("images/bild3.jpeg");
        Image imgBild4 = new Image("images/bild4.jpeg");
        Image imgBild5 = new Image("images/bild5.jpeg");

        ImageView bildAnzeige1 = new ImageView(imgBild1);
        bildAnzeige1.setFitHeight(120.0);
        bildAnzeige1.setFitWidth(180.0);
        ImageView bildAnzeige2 = new ImageView(imgBild2);
        bildAnzeige2.setFitHeight(120.0);
        bildAnzeige2.setFitWidth(180.0);
        ImageView bildAnzeige3 = new ImageView(imgBild3);
        bildAnzeige3.setFitHeight(120.0);
        bildAnzeige3.setFitWidth(180.0);
        ImageView bildAnzeige4 = new ImageView(imgBild4);
        bildAnzeige4.setFitHeight(120.0);
        bildAnzeige4.setFitWidth(180.0);
        ImageView bildAnzeige5 = new ImageView(imgBild5);
        bildAnzeige5.setFitHeight(120.0);
        bildAnzeige5.setFitWidth(180.0);

        bgBottom.add(bildAnzeige1, 0, 0);
        bgBottom.add(bildAnzeige2, 1, 0);
        bgBottom.add(bildAnzeige3, 2, 0);
        bgBottom.add(bildAnzeige4, 3, 0);
        bgBottom.add(bildAnzeige5, 4, 0);

        sgBottom.setContent(bgBottom);

        bpBilderGalerie.setTop(bgTop);
        bpBilderGalerie.setBottom(sgBottom);

        bildAnzeige1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView bgBild1 = new ImageView(imgBild1);
                bgBild1.setFitHeight(350.0);
                bgBild1.setFitWidth(550.0);

                bpBilderGalerie.setCenter(bgBild1);

                bgBild1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ScrollPane fullscreen = new ScrollPane();
                        Stage stageBild1 = new Stage();
                        Scene szeneBild1 = new Scene(fullscreen, 1440, 900);

                        ImageView fsBild1 = new ImageView(imgBild1);

                        fullscreen.setContent(fsBild1);

                        stageBild1.setTitle("Bild 1");
                        stageBild1.setScene(szeneBild1);
                        stageBild1.show();
                    }
                });
            }
        });
        bildAnzeige2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView bgBild2 = new ImageView(imgBild2);
                bgBild2.setFitHeight(350.0);
                bgBild2.setFitWidth(550.0);

                bpBilderGalerie.setCenter(bgBild2);

                bgBild2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ScrollPane fullscreen = new ScrollPane();
                        Stage stageBild2 = new Stage();
                        Scene szeneBild2 = new Scene(fullscreen, 1440, 900);

                        ImageView fsBild2 = new ImageView(imgBild2);

                        fullscreen.setContent(fsBild2);

                        stageBild2.setTitle("Bild 2");
                        stageBild2.setScene(szeneBild2);
                        stageBild2.show();
                    }
                });
            }
        });
        bildAnzeige3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView bgBild3 = new ImageView(imgBild3);
                bgBild3.setFitHeight(350.0);
                bgBild3.setFitWidth(550.0);

                bpBilderGalerie.setCenter(bgBild3);

                bgBild3.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ScrollPane fullscreen = new ScrollPane();
                        Stage stageBild3 = new Stage();
                        Scene szeneBild3 = new Scene(fullscreen, 1440, 900);

                        ImageView fsBild3 = new ImageView(imgBild3);

                        fullscreen.setContent(fsBild3);

                        stageBild3.setTitle("Bild 3");
                        stageBild3.setScene(szeneBild3);
                        stageBild3.show();
                    }
                });
            }
        });
        bildAnzeige4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView bgBild4 = new ImageView(imgBild4);
                bgBild4.setFitHeight(350.0);
                bgBild4.setFitWidth(550.0);

                bpBilderGalerie.setCenter(bgBild4);

                bgBild4.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ScrollPane fullscreen = new ScrollPane();
                        Stage stageBild4 = new Stage();
                        Scene szeneBild4 = new Scene(fullscreen, 1440, 900);

                        ImageView fsBild4 = new ImageView(imgBild4);

                        fullscreen.setContent(fsBild4);

                        stageBild4.setTitle("Bild 4");
                        stageBild4.setScene(szeneBild4);
                        stageBild4.show();
                    }
                });
            }
        });
        bildAnzeige5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView bgBild5 = new ImageView(imgBild5);
                bgBild5.setFitHeight(350.0);
                bgBild5.setFitWidth(550.0);

                bpBilderGalerie.setCenter(bgBild5);

                bgBild5.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ScrollPane fullscreen = new ScrollPane();
                        Stage stageBild5 = new Stage();
                        Scene szeneBild5 = new Scene(fullscreen, 1440, 900);

                        ImageView fsBild5 = new ImageView(imgBild5);

                        fullscreen.setContent(fsBild5);

                        stageBild5.setTitle("Bild 5");
                        stageBild5.setScene(szeneBild5);
                        stageBild5.show();
                    }
                });
            }
        });

        return bpBilderGalerie;
    }

    public BorderPane bpSchereSteinPapier() {
        BorderPane bpSchereSteinPapier = new BorderPane();
        bpSchereSteinPapier.setPadding(new Insets(50.0));

        VBox ergebnisse = new VBox(30.0);
        ergebnisse.setAlignment(Pos.CENTER);
        Label sspUeberschrift = new Label("Schere Stein Papier");
        sspUeberschrift.setFont(new Font("Comic Sans MS", 40.0));

        GridPane sspErgenisse = new GridPane();
        sspErgenisse.setAlignment(Pos.CENTER);
        sspErgenisse.setHgap(10.0);
        Label sspSpieler = new Label("Spieler");
        sspSpieler.setFont(new Font(25.0));
        Label sspSpielerErgebnis = new Label("0");
        sspSpielerErgebnis.setFont(new Font(25.0));
        Label sspComputerErgebnis = new Label("0");
        sspComputerErgebnis.setFont(new Font(25.0));
        Label sspComputer = new Label("Computer");
        sspComputer.setFont(new Font(25.0));
        Label sspUnentschieden = new Label("Unentschieden");
        sspUnentschieden.setFont(new Font(25.0));
        Label sspUnentschiedenErgebnis = new Label("0");
        sspUnentschiedenErgebnis.setFont(new Font(25.0));

        sspErgenisse.add(sspSpieler, 0, 0);
        sspErgenisse.add(sspSpielerErgebnis, 1, 0);
        sspErgenisse.add(sspComputer, 0, 1);
        sspErgenisse.add(sspComputerErgebnis, 1, 1);
        sspErgenisse.add(sspUnentschieden, 0, 2);
        sspErgenisse.add(sspUnentschiedenErgebnis, 1, 2);

        ergebnisse.getChildren().addAll(sspUeberschrift, sspErgenisse);

        HBox sspAuswahl = new HBox(50.0);
        sspAuswahl.setAlignment(Pos.CENTER);

        Image imgSchere = new Image("images/schere.png");
        ImageView schere = new ImageView(imgSchere);

        Image imgStein = new Image("images/stein.png");
        ImageView stein = new ImageView(imgStein);

        Image imgPapier = new Image("images/papier.png");
        ImageView papier = new ImageView(imgPapier);

        Button btnSchere = new Button();
        btnSchere.setGraphic(schere);
        btnSchere.setFocusTraversable(false);
        Button btnStein = new Button();
        btnStein.setGraphic(stein);
        btnStein.setFocusTraversable(false);
        Button btnPapier = new Button();
        btnPapier.setGraphic(papier);
        btnPapier.setFocusTraversable(false);

        sspAuswahl.getChildren().addAll(btnSchere, btnStein, btnPapier);

        GridPane sspSpielfeld = new GridPane();
        sspSpielfeld.setAlignment(Pos.CENTER);
        sspSpielfeld.setHgap(20.0);
        sspSpielfeld.setVgap(20.0);

        Label sspSpielerAuswahl = new Label();
        Label sspComputerAuswahl = new Label();
        Label sspErgebnis = new Label();
        sspErgebnis.setFont(new Font(20.0));
        sspErgebnis.setStyle("-fx-font-weight: bold");

        sspSpielfeld.add(sspSpielerAuswahl, 0, 0);
        sspSpielfeld.add(sspComputerAuswahl, 2, 0);
        sspSpielfeld.add(sspErgebnis, 1, 1);

        sspSpielfeld.setHalignment(sspErgebnis, HPos.CENTER);

        ImageView spielerSchere = new ImageView(imgSchere);
        ImageView spielerStein = new ImageView(imgStein);
        ImageView spielerPapier = new ImageView(imgPapier);
        ImageView computerSchere = new ImageView(imgSchere);
        ImageView computerStein = new ImageView(imgStein);
        ImageView computerPapier = new ImageView(imgPapier);

        ImageView[] spielerAuswahl = new ImageView[]{spielerSchere, spielerStein, spielerPapier};
        ImageView[] computerAuswahl = new ImageView[]{computerSchere, computerStein, computerPapier};

        Button nochmal = new Button("Nochmal");
        sspSpielfeld.setHalignment(nochmal, HPos.CENTER
        );

        Random random = new Random();

        spielerZaehler = 0;
        computerZaehler = 0;
        unentschiedenZaehler = 0;

        btnSchere.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpSchereSteinPapier.setBottom(null);

                Integer auswahl = random.nextInt(3);

                sspSpielerAuswahl.setGraphic(spielerAuswahl[0]);
                sspComputerAuswahl.setGraphic(computerAuswahl[auswahl]);

                if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[0])) {
                    sspErgebnis.setText("Unentschieden!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    unentschiedenZaehler++;
                    sspUnentschiedenErgebnis.setText(Integer.toString(unentschiedenZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                } else if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[1])) {
                    sspErgebnis.setText("Verloren!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    computerZaehler++;
                    sspComputerErgebnis.setText(Integer.toString(computerZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                } else if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[2])) {
                    sspErgebnis.setText("Gewonnen!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    spielerZaehler++;
                    sspSpielerErgebnis.setText(Integer.toString(spielerZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                }
            }
        });
        btnStein.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpSchereSteinPapier.setBottom(null);

                Integer auswahl = random.nextInt(3);

                sspSpielerAuswahl.setGraphic(spielerAuswahl[1]);
                sspComputerAuswahl.setGraphic(computerAuswahl[auswahl]);

                if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[0])) {
                    sspErgebnis.setText("Gewonnen!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    spielerZaehler++;
                    sspSpielerErgebnis.setText(Integer.toString(spielerZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                } else if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[1])) {
                    sspErgebnis.setText("Unentschieden!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    unentschiedenZaehler++;
                    sspUnentschiedenErgebnis.setText(Integer.toString(unentschiedenZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                } else if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[2])) {
                    sspErgebnis.setText("Verloren!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    computerZaehler++;
                    sspComputerErgebnis.setText(Integer.toString(computerZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                }
            }
        });
        btnPapier.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bpSchereSteinPapier.setBottom(null);

                Integer auswahl = random.nextInt(3);

                sspSpielerAuswahl.setGraphic(spielerAuswahl[2]);
                sspComputerAuswahl.setGraphic(computerAuswahl[auswahl]);

                if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[0])) {
                    sspErgebnis.setText("Verloren!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    computerZaehler++;
                    sspComputerErgebnis.setText(Integer.toString(computerZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                } else if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[1])) {
                    sspErgebnis.setText("Gewonnen!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    spielerZaehler++;
                    sspSpielerErgebnis.setText(Integer.toString(spielerZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                } else if (sspComputerAuswahl.getGraphic().equals(computerAuswahl[2])) {
                    sspErgebnis.setText("Unentschieden!");
                    sspErgebnis.setStyle("-fx-font-weight: bold");
                    unentschiedenZaehler++;
                    sspUnentschiedenErgebnis.setText(Integer.toString(unentschiedenZaehler));
                    sspSpielfeld.add(nochmal, 1, 3);
                }
            }
        });

        nochmal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sspSpielerAuswahl.setGraphic(null);
                sspComputerAuswahl.setGraphic(null);
                sspErgebnis.setText("");
                sspSpielfeld.getChildren().remove(nochmal);
                bpSchereSteinPapier.setBottom(sspAuswahl);
            }
        });

        bpSchereSteinPapier.setTop(ergebnisse);
        bpSchereSteinPapier.setBottom(sspAuswahl);
        bpSchereSteinPapier.setCenter(sspSpielfeld);

        return bpSchereSteinPapier;
    }

    public BorderPane bpWuerfelSpiel() {
        BorderPane bpWuerfelSpiel = new BorderPane();
        bpWuerfelSpiel.setPadding(new Insets(20.0));

        VBox wsTop = new VBox(20.0);
        wsTop.setAlignment(Pos.CENTER);

        Label wsUeberschrift = new Label("Würfelspiel");
        wsUeberschrift.setFont(new Font("Comic Sans MS", 40.0));

        GridPane wsErgebnisse = new GridPane();
        wsErgebnisse.setAlignment(Pos.CENTER);
        wsErgebnisse.setHgap(10.0);

        Label wsSpieler = new Label("Spieler");
        wsSpieler.setFont(new Font(25.0));
        Label wsSpielerErgebnis = new Label("0");
        wsSpielerErgebnis.setFont(new Font(25.0));
        Label wsComputer = new Label("Computer");
        wsComputer.setFont(new Font(25.0));
        Label wsComputerErgebnis = new Label("0");
        wsComputerErgebnis.setFont(new Font(25.0));
        Label wsUnentschieden = new Label("Unentschieden");
        wsUnentschieden.setFont(new Font(25.0));
        Label wsUnentschiedenErgebnis = new Label("0");
        wsUnentschiedenErgebnis.setFont(new Font(25.0));

        wsErgebnisse.add(wsSpieler, 0, 0);
        wsErgebnisse.add(wsSpielerErgebnis, 1, 0);
        wsErgebnisse.add(wsComputer, 0, 1);
        wsErgebnisse.add(wsComputerErgebnis, 1, 1);
        wsErgebnisse.add(wsUnentschieden, 0, 2);
        wsErgebnisse.add(wsUnentschiedenErgebnis, 1, 2);

        wsTop.getChildren().addAll(wsUeberschrift, wsErgebnisse);

        HBox wsBottom = new HBox();
        wsBottom.setPadding(new Insets(0.0, 0.0, 30.0, 0.0));
        wsBottom.setAlignment(Pos.CENTER);

        Button wuerfeln = new Button("Würfeln");

        wsBottom.getChildren().add(wuerfeln);

        GridPane wsCenter = new GridPane();
        wsCenter.setAlignment(Pos.CENTER);
        wsCenter.setHgap(15.0);
        wsCenter.setVgap(15.0);

        Label wsSpielerAuswahl = new Label();
        Label wsComputerAuswahl = new Label();
        Label wsErgebnis = new Label();
        wsErgebnis.setFont(new Font(20.0));
        wsErgebnis.setStyle("-fx-font-weight: bold");

        wsCenter.add(wsSpielerAuswahl, 0, 0);
        wsCenter.add(wsComputerAuswahl, 2, 0);
        wsCenter.add(wsErgebnis, 1, 1);

        Image imgWuerfel1 = new Image("images/wuerfel-1.png");
        Image imgWuerfel2 = new Image("images/wuerfel-2.png");
        Image imgWuerfel3 = new Image("images/wuerfel-3.png");
        Image imgWuerfel4 = new Image("images/wuerfel-4.png");
        Image imgWuerfel5 = new Image("images/wuerfel-5.png");
        Image imgWuerfel6 = new Image("images/wuerfel-6.png");

        ImageView wuerfel1 = new ImageView(imgWuerfel1);
        ImageView wuerfel2 = new ImageView(imgWuerfel2);
        ImageView wuerfel3 = new ImageView(imgWuerfel3);
        ImageView wuerfel4 = new ImageView(imgWuerfel4);
        ImageView wuerfel5 = new ImageView(imgWuerfel5);
        ImageView wuerfel6 = new ImageView(imgWuerfel6);

        ImageView wuerfelz1 = new ImageView(imgWuerfel1);
        ImageView wuerfelz2 = new ImageView(imgWuerfel2);
        ImageView wuerfelz3 = new ImageView(imgWuerfel3);
        ImageView wuerfelz4 = new ImageView(imgWuerfel4);
        ImageView wuerfelz5 = new ImageView(imgWuerfel5);
        ImageView wuerfelz6 = new ImageView(imgWuerfel6);

        ImageView[] wuerfel = {wuerfel1, wuerfel2, wuerfel3, wuerfel4, wuerfel5, wuerfel6};
        ImageView[] wuerfelz = {wuerfelz1, wuerfelz2, wuerfelz3, wuerfelz4, wuerfelz5, wuerfelz6};

        Random random = new Random();

        spielerZaehler = 0;
        computerZaehler = 0;
        unentschiedenZaehler = 0;

        wuerfeln.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                wsErgebnis.setText("");

                Integer spielerAuswahl = random.nextInt(5);
                Integer computerAuswahl = random.nextInt(5);

                wsSpielerAuswahl.setGraphic(wuerfel[spielerAuswahl]);
                wsComputerAuswahl.setGraphic(wuerfelz[computerAuswahl]);
                if (spielerAuswahl.equals(computerAuswahl)) {
                    wsErgebnis.setText("Unentschieden!");
                    unentschiedenZaehler++;
                    wsUnentschiedenErgebnis.setText(Integer.toString(unentschiedenZaehler));
                } else if (spielerAuswahl < computerAuswahl) {
                    wsErgebnis.setText("Verloren!");
                    computerZaehler++;
                    wsComputerErgebnis.setText(Integer.toString(computerZaehler));
                } else if (spielerAuswahl > computerAuswahl) {
                    wsErgebnis.setText("Gewonnen!");
                    spielerZaehler++;
                    wsSpielerErgebnis.setText(Integer.toString(spielerZaehler));
                }
            }
        });


        bpWuerfelSpiel.setTop(wsTop);
        bpWuerfelSpiel.setCenter(wsCenter);
        bpWuerfelSpiel.setBottom(wsBottom);

        return bpWuerfelSpiel;
    }




    public VBox vbUeber() {
        VBox vbUeber = new VBox();
        vbUeber.setPadding(new Insets(10.0));
        vbUeber.setSpacing(10.0);
        vbUeber.setAlignment(Pos.CENTER);

        Label lblUeber = new Label("Über Apps-Heaven");
        lblUeber.setFont(new Font(30.0));

        Label lblVersion = new Label("Apps-Heaven v0.6");

        Label lblEntwickler = new Label("Entwickelt von Sebastian Lessenig,\nfreundlichen Unterstützern der BBQ / U22 :)\nund nicht zu vergessen: Reza Unesshaad!");
        lblEntwickler.setTextAlignment(TextAlignment.CENTER);

        vbUeber.getChildren().addAll(lblUeber, lblVersion, lblEntwickler);

        return vbUeber;
    }
    // Apps-Heaven Ende

    // RegEx
    public boolean checkUser(String user) {
        if (user.matches("([A-Za-z0-9_]+)")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkEmail(String email) {
        if (email.matches("(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPass(String passwort) {
        if (passwort.matches("(^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$)")) {
            return true;
        } else {
            return false;
        }
    }

}
