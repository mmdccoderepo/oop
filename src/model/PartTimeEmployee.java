package model;

public class PartTimeEmployee extends Employee {
    private double hourlyRate;

    public PartTimeEmployee(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                            String employeeType, String positionLevel, String designation, String sssNumber,
                            String philHealthNumber, String tin, String pagIbigNumber, double hourlyRate) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, designation, sssNumber, philHealthNumber, tin, pagIbigNumber);
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
}
