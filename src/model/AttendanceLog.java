package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceLog {
    private int employeeId;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;

    public AttendanceLog(int employeeId, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public double getHoursWorked() {
        if (timeIn == null || timeOut == null) {
            return 0.0;
        }
        Duration duration = Duration.between(timeIn, timeOut);
        return duration.toMinutes() / 60.0;
    }
}
