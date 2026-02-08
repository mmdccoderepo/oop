package model;

public class SalariedEmployee extends Employee {
    private double basicSalary;

    public SalariedEmployee(int id, String firstName, String lastName, String email,
                            String phoneNumber, String position, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, position);
        this.basicSalary = basicSalary;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    @Override
    public double computeGrossSalary() {
        return basicSalary;
    }

    @Override
    public double computeDeductions() {
        return 0;
    }
}
