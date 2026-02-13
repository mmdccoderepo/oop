package model;

import java.util.ArrayList;
import java.util.List;

public class Regular extends Employee {
    private double basicSalary;

    private static final double STANDARD_WORK_HOURS = 160.0; // Assuming 40 hours/week * 4 weeks

    public Regular(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                   String employeeType, String positionLevel, String designation, String sssNumber,
                   String philHealthNumber, String tin, String pagIbigNumber, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, designation, sssNumber,
                philHealthNumber, tin, pagIbigNumber);
        setBasicSalary(basicSalary);
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        if (basicSalary < 0) {
            throw new IllegalArgumentException("Basic salary cannot be negative.");
        }
        this.basicSalary = basicSalary;
    }

    public double computeAllowances() {
        double totalAllowances = 0.0;
        for (Allowance allowance : getAllowances()) {
            if (allowance.getPositionLevel().equals(getPositionLevel())) {
                totalAllowances += allowance.getAmount();
            }
        }

        return totalAllowances;
    }

    public double computeDeductions() {
        double gross = computeGrossSalary();
        double totalDeductions = 0.0;
        totalDeductions += getDeduction("SSS", gross);
        totalDeductions += getDeduction("PhilHealth", gross);
        totalDeductions += getDeduction("Pag-IBIG", gross);
        return totalDeductions;
    }

    private double getDeduction(String deductionType, double gross) {
        List<Deduction> deductions = new ArrayList<>();
        for (Deduction deduction : getDeductions()) {
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

    public double computeGrossSalary() {
        double basicSalary = getBasicSalary();
        double hourlyRate = basicSalary / STANDARD_WORK_HOURS;
        return hourlyRate * getHoursWorked();
    }

    public double computeNetSalary() {
        double gross = computeGrossSalary();
        double allowances = computeAllowances();
        double deductions = computeDeductions();
        return gross + allowances - deductions;
    }
}
