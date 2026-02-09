package service;

import dao.AllowanceDAO;
import dao.DeductionDAO;
import model.Employee;
import model.FullTimeEmployee;

public class FullTimePayrollService extends PayrollService {
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;

    public FullTimePayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO) {
        super(allowanceDAO, deductionDAO);
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
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
