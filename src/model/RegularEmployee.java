package model;

public class RegularEmployee extends Employee {
    private double hourlyRate;
    private double hoursWorked;

    public RegularEmployee(int id, String firstName, String lastName, String email,
                           String phoneNumber, String position, double hourlyRate, double hoursWorked) {
        super(id, firstName, lastName, email, phoneNumber, position);
        this.hourlyRate = hourlyRate;
        setHoursWorked(hoursWorked);
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) throws IllegalArgumentException {
        if (hoursWorked < 0) {
            throw new IllegalArgumentException("Hours worked cannot be negative");
        }

        if (hoursWorked > 160) { // Assuming a maximum of 160 working hours per month
            throw new IllegalArgumentException("Hours worked cannot exceed 160 hours per month");
        }

        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateMonthlySalary() {
        return hourlyRate * hoursWorked;
    }
}
