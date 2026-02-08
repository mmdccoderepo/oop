package interfaces;

public interface Payable {
    double computeGrossSalary();

    double computeDeductions();

    default double computeNetSalary() {
        return computeGrossSalary() - computeDeductions();
    }
}
