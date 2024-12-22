package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AntivirusApp extends Application {
    private VBox centerPanel;
    private final FileScanner core = new FileScanner();
    private BorderPane root;

    // Additional settings
    private boolean realTimeProtectionEnabled = true;
    private int scanFrequency = 5; // in minutes

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        HBox menu = createTopMenu(primaryStage);

        centerPanel = createDashboard();

        root.setTop(menu);
        root.setCenter(centerPanel);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(45, 45, 48), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Antivirus Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTopMenu(Stage stage) {
        HBox menu = new HBox(20);
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.CENTER);

        Button scanButton = createMenuButton("Virus Scan", () -> startScan(stage));
        Button quarantineButton = createMenuButton("Quarantine", this::showQuarantine);
        Button settingsButton = createMenuButton("Settings", this::showSettings);
        Button reportButton = createMenuButton("Scan Report", this::showScanReport);

        menu.getChildren().addAll(scanButton, quarantineButton, settingsButton, reportButton);
        menu.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 32), CornerRadii.EMPTY, Insets.EMPTY)));

        return menu;
    }

    private VBox createDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(30));
        dashboard.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Welcome to Antivirus Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        dashboard.getChildren().add(title);
        return dashboard;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #1E88E5; -fx-background-radius: 10; -fx-padding: 10;");
        button.setOnAction(e -> {
            centerPanel.getChildren().clear();
            action.run();
        });
        return button;
    }

    private void startScan(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Scan");
        File directory = directoryChooser.showDialog(stage);

        if (directory != null) {
            Label scanningLabel = new Label("Scanning directory: " + directory.getPath());
            scanningLabel.setTextFill(Color.WHITE);
            centerPanel.getChildren().add(scanningLabel);

            List<String> threats = core.scanDirectory(directory);

            Label resultLabel = new Label("Scan complete. Threats found: " + threats.size());
            resultLabel.setTextFill(Color.WHITE);
            centerPanel.getChildren().add(resultLabel);

            threats.forEach(file -> {
                Label threatLabel = new Label("Threat: " + file);
                threatLabel.setTextFill(Color.LIGHTGRAY);
                centerPanel.getChildren().add(threatLabel);
            });
        }
    }

    private void showQuarantine() {
        centerPanel.getChildren().clear();
        List<String> quarantinedFiles = core.getQuarantinedFiles();
        Label quarantineLabel = new Label("Quarantined Files:");
        quarantineLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        quarantineLabel.setTextFill(Color.WHITE);
        centerPanel.getChildren().add(quarantineLabel);

        quarantinedFiles.forEach(file -> {
            Label fileLabel = new Label(file);
            fileLabel.setTextFill(Color.LIGHTGRAY);
            centerPanel.getChildren().add(fileLabel);
        });

        Button deleteQuarantineButton = new Button("Delete Quarantine Folder");
        deleteQuarantineButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteQuarantineButton.setOnAction(e -> {
            if (core.deleteQuarantineFolder()) {
                centerPanel.getChildren().clear();
                Label successLabel = new Label("Quarantine folder deleted successfully.");
                successLabel.setTextFill(Color.WHITE);
                centerPanel.getChildren().add(successLabel);
            } else {
                Label errorLabel = new Label("Failed to delete quarantine folder.");
                errorLabel.setTextFill(Color.RED);
                centerPanel.getChildren().add(errorLabel);
            }
        });

        centerPanel.getChildren().add(deleteQuarantineButton);

    }

    private void showSettings() {
        Label settingsTitle = new Label("Settings");
        settingsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        settingsTitle.setTextFill(Color.WHITE);

        // Real-time protection toggle
        Label realTimeLabel = new Label("Real-time Protection: ");
        realTimeLabel.setTextFill(Color.WHITE);
        ToggleGroup realTimeGroup = new ToggleGroup();
        RadioButton enabledButton = new RadioButton("Enabled");
        RadioButton disabledButton = new RadioButton("Disabled");
        enabledButton.setToggleGroup(realTimeGroup);
        disabledButton.setToggleGroup(realTimeGroup);

        if (realTimeProtectionEnabled) {
            enabledButton.setSelected(true);
        } else {
            disabledButton.setSelected(true);
        }

        enabledButton.setOnAction(e -> realTimeProtectionEnabled = true);
        disabledButton.setOnAction(e -> realTimeProtectionEnabled = false);

        HBox realTimeBox = new HBox(10, realTimeLabel, enabledButton, disabledButton);

        // Scan frequency control
        Label frequencyLabel = new Label("Scan Frequency (minutes): ");
        frequencyLabel.setTextFill(Color.WHITE);
        Spinner<Integer> frequencySpinner = new Spinner<>(1, 60, scanFrequency);
        frequencySpinner.valueProperty().addListener((obs, oldValue, newValue) -> scanFrequency = newValue);

        HBox frequencyBox = new HBox(10, frequencyLabel, frequencySpinner);

        // Background color change button
        Button changeBackgroundButton = new Button("Change Background Color");
        changeBackgroundButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
        changeBackgroundButton.setOnAction(e -> changeBackgroundColor());

        VBox settingsPanel = new VBox(20, settingsTitle, realTimeBox, frequencyBox, changeBackgroundButton);
        settingsPanel.setAlignment(Pos.CENTER);
        settingsPanel.setPadding(new Insets(20));

        centerPanel.getChildren().add(settingsPanel);
    }

    private void changeBackgroundColor() {
        root.setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void showScanReport() {
        String report = core.getScanReport();

        TextArea reportArea = new TextArea(report);
        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);
        reportArea.setWrapText(true);

        centerPanel.getChildren().clear();
        centerPanel.getChildren().add(reportArea);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
