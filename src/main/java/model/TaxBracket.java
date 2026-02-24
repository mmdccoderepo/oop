package model;

public class TaxBracket {
    private double salaryMin;
    private double salaryMax;
    private double baseAmount;
    private double rate;

    public TaxBracket(double salaryMin, double salaryMax, double baseAmount, double rate) {
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.baseAmount = baseAmount;
        this.rate = rate;
    }

    // Getters
    public double getSalaryMin() {
        return salaryMin;
    }

    public double getSalaryMax() {
        return salaryMax;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public double getRate() {
        return rate;
    }

    // Setters
    public void setSalaryMin(double salaryMin) {
        this.salaryMin = salaryMin;
    }

    public void setSalaryMax(double salaryMax) {
        this.salaryMax = salaryMax;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
