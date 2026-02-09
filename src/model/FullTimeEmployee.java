package model;

public class FullTimeEmployee extends Employee {
    private double basicSalary;

    public FullTimeEmployee(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                            String employeeType, String positionLevel, String designation, String sssNumber,
                            String philHealthNumber, String tin, String pagIbigNumber, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, designation, sssNumber, philHealthNumber, tin, pagIbigNumber);
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
}
