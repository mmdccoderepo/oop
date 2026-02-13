package model;

public class Probationary extends Employee {
    private double hourlyRate;

    public Probationary(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                        String employeeType, String positionLevel, String department, String sssNumber,
                        String philHealthNumber, String tin, String pagIbigNumber, double hourlyRate) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, department, sssNumber,
                philHealthNumber, tin, pagIbigNumber);
        setHourlyRate(hourlyRate);
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative.");
        }
        this.hourlyRate = hourlyRate;
    }

    public double computeAllowances() {
        return 0.0;
    }

    public double computeGrossSalary() {
        return getHourlyRate() * getHoursWorked();
    }
}
