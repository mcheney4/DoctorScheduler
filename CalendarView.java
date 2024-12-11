// CalendarView.java
package com.schedule.views;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import com.schedule.models.Shift;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class CalendarView extends VBox {
    private YearMonth currentYearMonth;
    private GridPane calendar;
    private Map<LocalDate, List<Shift>> scheduleData;
    private Label monthYearLabel;

    public CalendarView(Map<LocalDate, List<Shift>> scheduleData) {
        this.scheduleData = scheduleData;
        this.currentYearMonth = YearMonth.now();
        this.calendar = new GridPane();
        setPadding(new Insets(10));
        setSpacing(10);
        setupCalendar();
    }

    private void setupCalendar() {
        // Add month/year label
        monthYearLabel = new Label(
            currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        monthYearLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
        monthYearLabel.setAlignment(Pos.CENTER);

        // Header with days of week
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold");
            calendar.add(dayLabel, i, 0);
        }

        // Calendar days
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = currentYearMonth.atDay(i);
            StackPane dayCell = createDayCell(date);
            calendar.add(dayCell, (dayOfWeek + i - 1) % 7, (dayOfWeek + i - 1) / 7 + 1);
        }

        getChildren().clear();
        getChildren().addAll(monthYearLabel, calendar);
    }

    private StackPane createDayCell(LocalDate date) {
        StackPane cell = new StackPane();
        cell.setPrefSize(100, 100);
        
        Rectangle background = new Rectangle(98, 98);
        background.setFill(Color.WHITE);
        background.setStroke(Color.GRAY);

        VBox content = new VBox(5);
        content.setAlignment(Pos.TOP_CENTER);
        
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        content.getChildren().add(dateLabel);

        List<Shift> shifts = scheduleData.get(date);
        if (shifts != null) {
            for (Shift shift : shifts) {
                Label shiftLabel = new Label(
                    shift.getType() + ": " + shift.getDoctor().getName());
                shiftLabel.setStyle("-fx-font-size: 10px");
                content.getChildren().add(shiftLabel);
            }
            background.setFill(Color.LIGHTBLUE);
        }

        cell.getChildren().addAll(background, content);
        return cell;
    }

    public void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    public void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    private void updateCalendar() {
        monthYearLabel.setText(
            currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        calendar.getChildren().clear();
        setupCalendar();
    }
}