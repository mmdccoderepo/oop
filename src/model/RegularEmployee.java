package model;

public class RegularEmployee extends Employee {
    private double hourlyRate;

    public RegularEmployee(int id, String firstName, String lastName, String email,
                           String phoneNumber, String position, double hourlyRate) {
        super(id, firstName, lastName, email, phoneNumber, position);
        this.hourlyRate = hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double computeGrossSalary() {
        return hourlyRate * 160;
    }

    @Override
    public double computeDeductions() {
        return 0;
    }
}
