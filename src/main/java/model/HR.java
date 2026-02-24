package model;

public class HR extends Regular {
    public HR(int id, String firstName, String lastName, String email, String phoneNumber, String address,
              String employeeType, String positionLevel, String department, String sssNumber,
              String philHealthNumber, String tin, String pagIbigNumber, double basicSalary) {
        super(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, department, sssNumber,
                philHealthNumber, tin, pagIbigNumber, basicSalary);
    }

    public double computeRecruitmentBonus() {
        return 0.05 * getCompensation(); // Example: 5% of basic salary as recruitment bonus
    }

    @Override
    public double computeGrossSalary() {
        return super.computeGrossSalary() + computeRecruitmentBonus();
    }
}
