package dao;

import model.AttendanceLog;

import java.util.List;

public interface AttendanceLogDAO {
    List<AttendanceLog> getAll();

    List<AttendanceLog> getByEmployeeId(int employeeId);

    int getTotalHoursWorked(int employeeId);
}
