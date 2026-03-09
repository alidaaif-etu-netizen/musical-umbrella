// BankApplication.java


public class BankApplication {
    public static void main(String[] args) {
        System.out.println("Welcome to the Bank Management System");
        BankManager bankManager = new BankManager();
        bankManager.start();
    }
}