// CheckingAccount.java

import bankapp.Account;

public class CheckingAccount extends Account {
    private static final long serialVersionUID = 1L;
    private double overdraftLimit;
    private double monthlyFee;
    private boolean monthlyFeePaid;

    public CheckingAccount(String accountNumber, String accountHolderName, 
                          double initialBalance, double overdraftLimit, double monthlyFee) {
        super(accountNumber, accountHolderName, initialBalance, AccountType.CHECKING);
        this.overdraftLimit = overdraftLimit;
        this.monthlyFee = monthlyFee;
        this.monthlyFeePaid = false;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount");
            return false;
        }
        
        if (balance - amount >= -overdraftLimit) {
            balance -= amount;
            addTransaction("Withdrawal", -amount);
            return true;
        } else {
            System.out.println("Overdraft limit exceeded");
            return false;
        }
    }

    @Override
    public void applyMonthlyFee() {
        if (!monthlyFeePaid) {
            balance -= monthlyFee;
            addTransaction("Monthly Fee", -monthlyFee);
            monthlyFeePaid = true;
        }
    }

    public void resetMonthlyFee() {
        monthlyFeePaid = false;
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}