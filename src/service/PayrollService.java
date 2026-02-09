package service;

import dao.AllowanceDAO;
import dao.DeductionDAO;
import model.Allowance;
import model.Deduction;
import model.Employee;

import java.util.ArrayList;
import java.util.List;

public abstract class PayrollService {
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;

    public PayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO) {
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
        double total = 0.0;
        total += getDeduction("SSS", gross);
        total += getDeduction("PhilHealth", gross);
        total += getDeduction("Pag-IBIG", gross);
        return total;
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

    public abstract double computeGrossSalary(Employee employee);

    public abstract double computeNetSalary(Employee employee);
}
