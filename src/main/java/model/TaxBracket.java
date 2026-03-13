package model;

public class TaxBracket {
    private double salaryMin;
    private double salaryMax;
    private double baseAmount;
    private double rate;

    public TaxBracket(double salaryMin, double salaryMax, double baseAmount, double rate) {
        if (salaryMin < 0) {
            throw new IllegalArgumentException("Salary minimum cannot be negative.");
        }
        if (salaryMax < 0) {
            throw new IllegalArgumentException("Salary maximum cannot be negative.");
        }
        if (salaryMin > salaryMax) {
            throw new IllegalArgumentException("Salary minimum cannot be greater than salary maximum.");
        }
        if (baseAmount < 0) {
            throw new IllegalArgumentException("Base amount cannot be negative.");
        }
        if (rate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }
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
        if (salaryMin < 0) {
            throw new IllegalArgumentException("Salary minimum cannot be negative.");
        }
        if (salaryMin > this.salaryMax) {
            throw new IllegalArgumentException("Salary minimum cannot be greater than salary maximum.");
        }
        this.salaryMin = salaryMin;
    }

    public void setSalaryMax(double salaryMax) {
        if (salaryMax < 0) {
            throw new IllegalArgumentException("Salary maximum cannot be negative.");
        }
        if (this.salaryMin > salaryMax) {
            throw new IllegalArgumentException("Salary minimum cannot be greater than salary maximum.");
        }
        this.salaryMax = salaryMax;
    }

    public void setBaseAmount(double baseAmount) {
        if (baseAmount < 0) {
            throw new IllegalArgumentException("Base amount cannot be negative.");
        }
        this.baseAmount = baseAmount;
    }

    public void setRate(double rate) {
        if (rate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }
        this.rate = rate;
    }
}
