package dao;

import model.AttendanceLog;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceLogDAO {
    List<AttendanceLog> getAll();

    List<AttendanceLog> getByEmployeeId(int employeeId);

    int getTotalHoursWorked(int employeeId);

    int getHoursWorkedInRange(int employeeId, LocalDate startDate, LocalDate endDate);

    boolean addLog(AttendanceLog log);
}
