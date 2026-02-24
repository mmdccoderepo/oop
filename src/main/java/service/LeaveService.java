package service;

import dao.LeaveDAO;
import model.Employee;
import model.HR;
import model.Leave;

import java.time.LocalDate;
import java.util.List;

public class LeaveService {
    private LeaveDAO leaveDAO;

    public LeaveService(LeaveDAO leaveDAO) {
        this.leaveDAO = leaveDAO;
    }

    public void fileLeave(Employee employee, LocalDate startDate, LocalDate endDate, String reason) {
        Leave leave = new Leave(0, employee.getId(), startDate, endDate, "PENDING", reason);
        leaveDAO.addLeave(leave);
    }

    public void approveLeave(Employee approver, int leaveId) throws IllegalAccessException {
        if (!(approver instanceof HR)) {
            throw new IllegalAccessException("Only HR employees can approve leaves");
        }

        Leave leave = leaveDAO.getLeaveById(leaveId);
        if (leave != null) {
            leave.setStatus("APPROVED");
            leaveDAO.updateLeave(leave);
        }
    }

    public void rejectLeave(Employee approver, int leaveId) throws IllegalAccessException {
        if (!(approver instanceof HR)) {
            throw new IllegalAccessException("Only HR employees can reject leaves");
        }

        Leave leave = leaveDAO.getLeaveById(leaveId);
        if (leave != null) {
            leave.setStatus("REJECTED");
            leaveDAO.updateLeave(leave);
        }
    }

    public List<Leave> getEmployeeLeaves(int employeeId) {
        return leaveDAO.getLeavesByEmployeeId(employeeId);
    }

    public List<Leave> getAll(Employee requester) throws IllegalAccessException {
        if (!(requester instanceof HR)) {
            throw new IllegalAccessException("Only HR employees can view all leaves");
        }
        return leaveDAO.getAll();
    }
}

