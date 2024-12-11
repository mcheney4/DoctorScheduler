// AdminView.java
package com.schedule.views;

import com.schedule.controllers.AdminController;
import com.schedule.models.*;
import com.schedule.enums.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminView extends Application {
    private AdminController controller;
    private TableView<Doctor> doctorTable;
    private TableView<ShiftSwapRequest> swapRequestsTable;
    private DatePicker scheduleDatePicker;

    @Override
    public void start(Stage primaryStage) {
        controller = new AdminController();
        
        TabPane tabPane = new TabPane();
        
        Tab doctorsTab = new Tab("Doctors");
        doctorsTab.setContent(createDoctorsTab());
        
        Tab scheduleTab = new Tab("Schedule");
        scheduleTab.setContent(createScheduleTab());
        
        Tab swapRequestsTab = new Tab("Swap Requests");
        swapRequestsTab.setContent(createSwapRequestsTab());
        
        tabPane.getTabs().addAll(doctorsTab, scheduleTab, swapRequestsTab);
        
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Doctor Scheduler - Admin Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initial refresh of doctor table
        refreshDoctorTable();
    }
    private VBox createDoctorsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        doctorTable = new TableView<>();
        
        TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Doctor, String> seniorityCol = new TableColumn<>("Seniority");
        seniorityCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getSeniority().toString()));
        
        doctorTable.getColumns().addAll(nameCol, seniorityCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Doctor Name");
        
        ComboBox<SeniorityLevel> seniorityCombo = new ComboBox<>();
        seniorityCombo.getItems().addAll(SeniorityLevel.values());
        
        Button addButton = new Button("Add Doctor");
        addButton.setOnAction(e -> {
            if (!nameField.getText().isEmpty() && seniorityCombo.getValue() != null) {
                controller.addDoctor(nameField.getText(), seniorityCombo.getValue());
                refreshDoctorTable();
                nameField.clear();
                seniorityCombo.setValue(null);
            }
        });

        HBox addControls = new HBox(10);
        addControls.getChildren().addAll(nameField, seniorityCombo, addButton);

        vbox.getChildren().addAll(doctorTable, addControls);
        return vbox;
    }

    private VBox createScheduleTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        scheduleDatePicker = new DatePicker();
        Button generateButton = new Button("Generate Annual Schedule");
        generateButton.setOnAction(e -> {
            LocalDate startDate = scheduleDatePicker.getValue();
            if (startDate != null) {
                controller.createSchedule(startDate, startDate.plusYears(1));
                showScheduleView();
            }
        });

        TableView<Shift> scheduleTable = new TableView<>();
        
        TableColumn<Shift, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDate()));
        
        TableColumn<Shift, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDoctor().getName()));
        
        TableColumn<Shift, String> typeCol = new TableColumn<>("Shift Type");
        typeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getType().toString()));
        
        scheduleTable.getColumns().addAll(dateCol, doctorCol, typeCol);

        HBox controls = new HBox(10);
        controls.getChildren().addAll(scheduleDatePicker, generateButton);

        vbox.getChildren().addAll(controls, scheduleTable);
        return vbox;
    }

    private VBox createSwapRequestsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        swapRequestsTable = new TableView<>();
        
        TableColumn<ShiftSwapRequest, String> requesterCol = new TableColumn<>("Requester");
        requesterCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getRequester().getName()));
        
        TableColumn<ShiftSwapRequest, String> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTarget().getName()));
        
        TableColumn<ShiftSwapRequest, LocalDate> dateCol = new TableColumn<>("Shift Date");
        dateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getOriginalShift().getDate()));
        
        TableColumn<ShiftSwapRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

        TableColumn<ShiftSwapRequest, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            {
                approveButton.setOnAction(e -> handleSwapRequest(true));
                rejectButton.setOnAction(e -> handleSwapRequest(false));
            }

            private void handleSwapRequest(boolean approved) {
                ShiftSwapRequest request = getTableView().getItems().get(getIndex());
                controller.handleShiftSwapRequest(request.getId(), approved);
                refreshSwapRequestsTable();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(approveButton, rejectButton);
                    setGraphic(buttons);
                }
            }
        });

        swapRequestsTable.getColumns().addAll(
            requesterCol, targetCol, dateCol, statusCol, actionsCol);

        vbox.getChildren().add(swapRequestsTable);
        return vbox;
    }

    private void refreshDoctorTable() {
        doctorTable.getItems().clear();
        doctorTable.getItems().addAll(controller.getDoctors());
    }

    private void refreshSwapRequestsTable() {
        swapRequestsTable.getItems().clear();
        swapRequestsTable.getItems().addAll(controller.getSchedule().getPendingSwaps());
    }

    // In AdminView.java, update showScheduleView method:
    // Update showScheduleView() in AdminView.java
    private void showScheduleView() {
        if (controller.getSchedule() == null) {
            new Alert(Alert.AlertType.ERROR, "Please generate a schedule first").showAndWait();
            return;
        }
    
        Stage scheduleStage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
    
        CalendarView calendarView = new CalendarView(controller.getSchedule().getDailySchedule());
    
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
// Add DisplayShift class
private static class DisplayShift {
    private final LocalDate date;
    private final ShiftType shiftType;
    private final String doctorName;

    public DisplayShift(LocalDate date, ShiftType shiftType, String doctorName) {
        this.date = date;
        this.shiftType = shiftType;
        this.doctorName = doctorName;
    }

    public LocalDate getDate() { return date; }
    public ShiftType getShiftType() { return shiftType; }
    public String getDoctorName() { return doctorName; }
}
}
