package service;

import dao.AllowanceDAO;
import dao.AttendanceLogDAO;
import dao.DeductionDAO;
import model.Employee;
import model.PartTimeEmployee;

public class PartTimePayrollService extends PayrollService {
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;
    protected AttendanceLogDAO attendanceLogDAO;

    public PartTimePayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO, AttendanceLogDAO attendanceLogDAO) {
        super(allowanceDAO, deductionDAO);
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.attendanceLogDAO = attendanceLogDAO;
    }

    @Override
    public double computeAllowances(Employee employee) {
        return 0.0;
    }

    @Override
    public double computeDeductions(Employee employee) {
        return 0.0;
    }

    @Override
    public double computeGrossSalary(Employee employee) {
        double hoursWorked = attendanceLogDAO.getTotalHoursWorked(employee.getId());
        return ((PartTimeEmployee) employee).getHourlyRate() * hoursWorked;
    }

    @Override
    public double computeNetSalary(Employee employee) {
        return computeGrossSalary(employee);
    }
}
