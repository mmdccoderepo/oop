package model;

public class Regular extends Employee {
    private double basicSalary;

    private static final double STANDARD_WORK_HOURS = 160.0; // Assuming 40 hours/week * 4 weeks

    public Regular(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                   String employeeType, String positionLevel, String role, String sssNumber,
                   String philHealthNumber, String tin, String pagIbigNumber, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, role, sssNumber,
                philHealthNumber, tin, pagIbigNumber);
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

    public double getCompensation() {
        return getBasicSalary();
    }

    public double computeAllowances() {
        // If no hours worked, no allowances
        if (getHoursWorked() == 0) {
            return 0.0;
        }

        double totalAllowances = 0.0;
        for (Allowance allowance : getAllowances()) {
            if (allowance.getPositionLevel().equals(getPositionLevel())) {
                totalAllowances += allowance.getAmount();
            }
        }

        return totalAllowances;
    }

    public double computeGrossSalary() {
        // If no hours worked, return 0
        if (getHoursWorked() == 0) {
            return 0.0;
        }

        double basicSalary = getBasicSalary();
        double hourlyRate = basicSalary / STANDARD_WORK_HOURS;
        return hourlyRate * getHoursWorked() + computeAllowances();
    }
}
