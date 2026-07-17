package bankapp;

public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    private double interestRate;

    public SavingsAccount(String accountNumber, String accountHolderName,
                          double initialBalance, double interestRate) {
        super(accountNumber, accountHolderName, initialBalance, AccountType.SAVINGS);
        this.interestRate = interestRate;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public void applyInterest() {
        double interest = balance * interestRate;
        balance += interest;
        addTransaction("Interest applied", interest);
    }

    @Override
    public void applyMonthlyFee() {
        applyInterest();
    }
}
