package model;

public class PayrollAdmin extends SalariedEmployee {
    public PayrollAdmin(int id, String firstName, String lastName, String email,
                        String phoneNumber, String position, double monthlySalary) {
        super(id, firstName, lastName, email, phoneNumber, position, monthlySalary);
    }
}
