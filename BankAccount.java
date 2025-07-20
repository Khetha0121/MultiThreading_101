package TransactionThreading;

class BankAccount {
    private String accountHolder;
    private double balance;

    public BankAccount(String accountHolder, double initialBalance) {
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.printf("[%s] Deposited: %.2f | New Balance: %.2f%n", accountHolder, amount, balance);
    }

    public synchronized boolean withdraw(double amount) {
        if (amount > balance) {
            System.out.printf("[%s] Withdrawal failed (Insufficient funds): %.2f | Balance: %.2f%n", accountHolder, amount, balance);
            return false;
        }
        balance -= amount;
        System.out.printf("[%s] Withdrew: %.2f | New Balance: %.2f%n", accountHolder, amount, balance);
        return true;
    }

    public synchronized double getBalance() {
        return balance;
    }
}
