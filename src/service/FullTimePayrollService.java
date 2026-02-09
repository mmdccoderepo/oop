package service;

import dao.AllowanceDAO;
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

    public FullTimePayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO) {
        super(allowanceDAO, deductionDAO);
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
    }

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

    public double computeGrossSalary(Employee employee) {
        return ((FullTimeEmployee) employee).getBasicSalary();
    }

    public double computeNetSalary(Employee employee) {
        double gross = computeGrossSalary(employee);
        double allowances = computeAllowances(employee);
        double deductions = computeDeductions(employee);
        return gross + allowances - deductions;
    }
}
