// Create a new launcher class - ApplicationLauncher.java
package com.schedule;

import com.schedule.views.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Button adminButton = new Button("Launch Admin View");
        Button doctorButton = new Button("Launch Doctor View");

        adminButton.setOnAction(e -> {
            Stage adminStage = new Stage();
            new AdminView().start(adminStage);
        });

        doctorButton.setOnAction(e -> {
            Stage doctorStage = new Stage();
            new DoctorView().start(doctorStage);
        });

        root.getChildren().addAll(adminButton, doctorButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Doctor Scheduler Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}