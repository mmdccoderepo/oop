package model;

public class Finance extends Regular {
    public Finance(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                   String employeeType, String positionLevel, String department, String sssNumber,
                   String philHealthNumber, String tin, String pagIbigNumber, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, department, sssNumber,
                philHealthNumber, tin, pagIbigNumber, basicSalary);
    }

    public double computeAuditBonus() {
        return 0.1 * getCompensation(); // Example: 10% of basic salary as financial bonus
    }

    @Override
    public double computeGrossSalary() {
        return super.computeGrossSalary() + computeAuditBonus();
    }
}
