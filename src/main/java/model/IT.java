package model;

public class IT extends Regular {
    public IT(int id, String firstName, String lastName, String email, String phoneNumber, String address,
              String employeeType, String positionLevel, String role, String sssNumber,
              String philHealthNumber, String tin, String pagIbigNumber, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, role, sssNumber,
                philHealthNumber, tin, pagIbigNumber, basicSalary);
    }

    @Override
    public double computeAllowances() {
        // If no hours worked, return 0
        if (getHoursWorked() == 0) {
            return 0.0;
        }
        return super.computeAllowances() + 1000.0; // Example: IT employees get an additional tech allowance of 1000
    }
}
