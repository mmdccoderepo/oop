package service;

import dao.AllowanceDAO;
import dao.AttendanceLogDAO;
import dao.DeductionDAO;
import model.Allowance;
import model.Deduction;
import model.Employee;
import model.FullTimeEmployee;

import java.util.ArrayList;
import java.util.List;

public class FullTimePayrollService extends PayrollService {
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;
    protected AttendanceLogDAO attendanceLogDAO;

    private static final double STANDARD_WORK_HOURS = 160.0; // Assuming 40 hours/week * 4 weeks

    public FullTimePayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO, AttendanceLogDAO attendanceLogDAO) {
        super(allowanceDAO, deductionDAO);
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.attendanceLogDAO = attendanceLogDAO;
    }

    @Override
    public double computeAllowances(Employee employee) {
        if (allowanceDAO == null) {
            return 0.0;
        }

        double totalAllowances = 0.0;
        for (Allowance allowance : allowanceDAO.getAll()) {
            if (allowance.getPositionLevel().equals(employee.getPositionLevel())) {
                totalAllowances += allowance.getAmount();
            }
        }

        return totalAllowances;
    }

    @Override
    public double computeDeductions(Employee employee) {
        if (deductionDAO == null) {
            return 0.0;
        }
        double gross = computeGrossSalary(employee);
        double totalDeductions = 0.0;
        totalDeductions += getDeduction("SSS", gross);
        totalDeductions += getDeduction("PhilHealth", gross);
        totalDeductions += getDeduction("Pag-IBIG", gross);
        return totalDeductions;
    }

    private double getDeduction(String deductionType, double gross) {
        if (deductionDAO == null) {
            return 0.0;
        }
        List<Deduction> deductions = new ArrayList<>();
        for (Deduction deduction : deductionDAO.getAll()) {
            if (deductionType.equals(deduction.getName())) {
                deductions.add(deduction);
            }
        }
        if (deductions.isEmpty()) {
            return 0.0;
        }

        Deduction matchingBracket = null;
        for (Deduction deduction : deductions) {
            if (gross >= deduction.getSalaryMin() && gross <= deduction.getSalaryMax()) {
                if (matchingBracket == null || deduction.getSalaryMin() > matchingBracket.getSalaryMin()) {
                    matchingBracket = deduction;
                }
            }
        }

        if (matchingBracket == null) {
            return 0.0;
        }

        return matchingBracket.getEmployeeShare();
    }

    @Override
    public double computeGrossSalary(Employee employee) {
        double basicSalary = ((FullTimeEmployee) employee).getBasicSalary();
        double hoursWorked = attendanceLogDAO.getTotalHoursWorked(employee.getId());
        double hourlyRate = basicSalary / STANDARD_WORK_HOURS;
        return hourlyRate * hoursWorked;
    }

    @Override
    public double computeNetSalary(Employee employee) {
        double gross = computeGrossSalary(employee);
        double allowances = computeAllowances(employee);
        double deductions = computeDeductions(employee);
        return gross + allowances - deductions;
    }
}
