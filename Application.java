// Application.java
package com.schedule;

import com.schedule.views.AdminView;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) {
        AdminView adminView = new AdminView();
        adminView.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}