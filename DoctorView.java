package com.schedule.views;

import com.schedule.controllers.AdminController;
import com.schedule.controllers.DoctorController;
import com.schedule.models.*;
import com.schedule.enums.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

public class DoctorView extends Application {
    private DoctorController controller;
    private TableView<Shift> shiftsTable;
    private DatePicker datePicker;
    private TableView<LocalDate> preferredTable;
    private TableView<LocalDate> unavailableTable;
    private ComboBox<Shift> shiftCombo;
    private ComboBox<Doctor> doctorCombo;
    private Schedule schedule;

    @Override
    public void start(Stage primaryStage) {
        // Share the same schedule instance that AdminView uses
        AdminController adminController = new AdminController();
        Doctor doctor = new Doctor("Demo Doctor", SeniorityLevel.SENIOR);
        schedule = adminController.getSchedule();
        controller = new DoctorController(doctor, schedule);
        
        TabPane tabPane = new TabPane();
        
        Tab scheduleTab = new Tab("My Schedule");
        scheduleTab.setContent(createScheduleTab());
        
        Tab preferencesTab = new Tab("Preferences");
        preferencesTab.setContent(createPreferencesTab());
        
        Tab swapTab = new Tab("Request Swap");
        swapTab.setContent(createSwapTab());
        
        tabPane.getTabs().addAll(scheduleTab, preferencesTab, swapTab);
        
        // Add a refresh button to update the view when schedule is generated
        Button refreshButton = new Button("Refresh Schedule");
        refreshButton.setOnAction(e -> refreshAll());
        
        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(refreshButton, tabPane);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Doctor Scheduler - Doctor Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initial refresh
        refreshAll();
    }

    private VBox createScheduleTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        shiftsTable = new TableView<>();
        
        TableColumn<Shift, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDate()));
        
        TableColumn<Shift, String> typeCol = new TableColumn<>("Shift Type");
        typeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getType().toString()));

        shiftsTable.getColumns().addAll(dateCol, typeCol);

        // Add calendar view
        Button viewCalendarButton = new Button("View Calendar");
        viewCalendarButton.setOnAction(e -> showCalendarView());

        vbox.getChildren().addAll(shiftsTable, viewCalendarButton);
        return vbox;
    }

    private VBox createPreferencesTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        datePicker = new DatePicker();
        
        Button addPreferredButton = new Button("Add Preferred Date");
        Button addUnavailableButton = new Button("Add Unavailable Date");

        preferredTable = new TableView<>();
        TableColumn<LocalDate, LocalDate> preferredCol = new TableColumn<>("Preferred Dates");
        preferredCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue()));
        preferredTable.getColumns().add(preferredCol);

        unavailableTable = new TableView<>();
        TableColumn<LocalDate, LocalDate> unavailableCol = new TableColumn<>("Unavailable Dates");
        unavailableCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue()));
        unavailableTable.getColumns().add(unavailableCol);

        addPreferredButton.setOnAction(e -> {
            if (datePicker.getValue() != null) {
                controller.addPreferredDate(datePicker.getValue());
                refreshPreferencesTables();
            }
        });

        addUnavailableButton.setOnAction(e -> {
            if (datePicker.getValue() != null) {
                controller.addUnavailableDate(datePicker.getValue());
                refreshPreferencesTables();
            }
        });

        HBox controls = new HBox(10);
        controls.getChildren().addAll(datePicker, addPreferredButton, addUnavailableButton);

        vbox.getChildren().addAll(controls, new Label("Preferred Dates:"), 
            preferredTable, new Label("Unavailable Dates:"), unavailableTable);
        
        // Initial table population
        refreshPreferencesTables();
        
        return vbox;
    }

    private void showCalendarView() {
        if (schedule == null) {
            new Alert(Alert.AlertType.ERROR, "No schedule available").showAndWait();
            return;
        }

        Stage scheduleStage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        CalendarView calendarView = new CalendarView(schedule.getDailySchedule());

        Button prevButton = new Button("Previous Month");
        Button nextButton = new Button("Next Month");
        
        prevButton.setOnAction(e -> calendarView.previousMonth());
        nextButton.setOnAction(e -> calendarView.nextMonth());

        HBox controls = new HBox(10);
        controls.getChildren().addAll(prevButton, nextButton);
        controls.setAlignment(Pos.CENTER);

        root.getChildren().addAll(controls, calendarView);
        Scene scene = new Scene(root, 800, 600);
        scheduleStage.setTitle("Schedule Calendar View");
        scheduleStage.setScene(scene);
        scheduleStage.show();
    }

    private VBox createSwapTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        shiftCombo = new ComboBox<>();
        doctorCombo = new ComboBox<>();
        
        Button requestButton = new Button("Request Swap");
        requestButton.setOnAction(e -> {
            if (shiftCombo.getValue() != null && doctorCombo.getValue() != null) {
                controller.requestShiftSwap(shiftCombo.getValue(), doctorCombo.getValue());
                new Alert(Alert.AlertType.INFORMATION, 
                    "Swap request sent successfully!").showAndWait();
                refreshAll();
            }
        });

        vbox.getChildren().addAll(
            new Label("Select Shift:"), shiftCombo,
            new Label("Select Doctor:"), doctorCombo,
            requestButton
        );
        return vbox;
    }

    private void refreshAll() {
        refreshShiftsTable();
        refreshPreferencesTables();
        refreshSwapControls();
    }

    private void refreshShiftsTable() {
        shiftsTable.getItems().clear();
        if (controller.getAssignedShifts() != null) {
            shiftsTable.getItems().addAll(controller.getAssignedShifts());
        }
    }

    private void refreshSwapControls() {
        shiftCombo.getItems().clear();
        doctorCombo.getItems().clear();
        
        if (schedule != null) {
            shiftCombo.getItems().addAll(controller.getAssignedShifts());
            // Add all doctors except the current one
            List<Doctor> allDoctors = new ArrayList<>(schedule.getAllDoctors());
            allDoctors.remove(controller.getDoctor());
            doctorCombo.getItems().addAll(allDoctors);
        }
    }

    private void refreshPreferencesTables() {
        preferredTable.getItems().clear();
        preferredTable.getItems().addAll(controller.getPreferredDates());
        
        unavailableTable.getItems().clear();
        unavailableTable.getItems().addAll(controller.getUnavailableDates());
    }
}