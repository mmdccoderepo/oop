package model;

import java.util.List;

public interface Payable {
    int getHoursWorked();

    void setHoursWorked(int hoursWorked);

    List<Allowance> getAllowances();

    void setAllowances(List<Allowance> allowances);

    List<Deduction> getDeductions();

    void setDeductions(List<Deduction> deductions);

    double computeAllowances();

    double computeDeductions();

    double computeGrossSalary();

    double computeNetSalary();
}
