package service;

import dao.AllowanceDAO;
import dao.DeductionDAO;
import model.Employee;
import model.PartTimeEmployee;

public class PartTimePayrollService extends PayrollService {
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;

    public PartTimePayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO) {
        super(allowanceDAO, deductionDAO);
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
    }

    public double computeGrossSalary(Employee employee) {
        return ((PartTimeEmployee) employee).getHourlyRate() * 160;
    }

    public double computeNetSalary(Employee employee) {
        return computeGrossSalary(employee);
    }
}
