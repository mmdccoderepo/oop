package dao;

import model.Leave;

import java.util.List;

public interface LeaveDAO {
    List<Leave> getAll();

    Leave getLeaveById(int id);

    List<Leave> getLeavesByEmployeeId(int employeeId);

    void addLeave(Leave leave);

    void updateLeave(Leave leave);

    void deleteLeave(int id);
}

