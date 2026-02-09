package service;

import dao.AllowanceDAO;
import dao.DeductionDAO;
import model.Employee;

public abstract class PayrollService {
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;

    public PayrollService(AllowanceDAO allowanceDAO, DeductionDAO deductionDAO) {
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
    }

    public abstract double computeAllowances(Employee employee);

    public abstract double computeDeductions(Employee employee);

    public abstract double computeGrossSalary(Employee employee);

    public abstract double computeNetSalary(Employee employee);
}
