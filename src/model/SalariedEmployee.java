package model;

public class SalariedEmployee extends Employee {
    private double monthlySalary;

    public SalariedEmployee(int id, String firstName, String lastName, String email,
                            String phoneNumber, String position, double monthlySalary) {
        super(id, firstName, lastName, email, phoneNumber, position);
        this.monthlySalary = monthlySalary;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateMonthlySalary() {
        return monthlySalary;
    }
}
