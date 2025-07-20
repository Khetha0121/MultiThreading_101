# Banking Simulation Code Documentation

## Current Code Analysis

This Java banking simulation demonstrates basic multithreading with a single shared bank account accessed by multiple concurrent threads.

## Code Structure

### 1. BankingSimulation (Main Class)
```java
public static void main(String[] args) {
    BankAccount kk = new BankAccount("Khethokuhle", 200);
    
    Thread t1 = new Thread(new TransactionTask(kk));
    Thread t2 = new Thread(new TransactionTask(kk));
    Thread t3 = new Thread(new TransactionTask(kk));
    Thread t4 = new Thread(new TransactionTask(kk));
    Thread t5 = new Thread(new TransactionTask(kk));
    
    // Start all threads
    t1.start(); t2.start(); t3.start(); t4.start(); t5.start();
    
    // Wait for all threads to complete
    try {
        t1.join(); t2.join(); t3.join(); t4.join(); t5.join();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    
    System.out.println("\n--- Final Balances ---");
    System.out.printf("Khethokuhle: %.2f%n", kk.getBalance());
}
```

**What this does:**
- Creates one bank account with initial balance of 200
- Creates 5 threads that all operate on the same account
- Starts all threads simultaneously
- Waits for all threads to finish using `join()`
- Displays final balance

### 2. BankAccount Class
```java
class BankAccount {
    private String accountHolder;
    private double balance;
    
    public BankAccount(String accountHolder, double initialBalance) {
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }
    
    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.printf("[%s] Deposited: %.2f | New Balance: %.2f%n", 
                          accountHolder, amount, balance);
    }
    
    public synchronized boolean withdraw(double amount) {
        if (amount > balance) {
            System.out.printf("[%s] Withdrawal failed (Insufficient funds): %.2f | Balance: %.2f%n", 
                              accountHolder, amount, balance);
            return false;
        }
        balance -= amount;
        System.out.printf("[%s] Withdrew: %.2f | New Balance: %.2f%n", 
                          accountHolder, amount, balance);
        return true;
    }
    
    public synchronized double getBalance() {
        return balance;
    }
}
```

**Key features:**
- All methods are `synchronized` - only one thread can access any method at a time
- Deposit always succeeds and adds to balance
- Withdrawal checks for sufficient funds before proceeding
- All operations print their results immediately

### 3. TransactionTask Class
```java
class TransactionTask implements Runnable {
    private final BankAccount account1;
    
    public TransactionTask(BankAccount a1) {
        this.account1 = a1;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < 7; i++) {
            double amount = Math.random() * 100;  // Random amount 0-100
            int action = (int) (Math.random() * 2);  // 0 or 1
            
            switch (action) {
                case 0: account1.deposit(amount); break;
                case 1: account1.withdraw(amount); break;
            }
            
            try {
                Thread.sleep(1000); // Wait 1 second between transactions
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

**What each thread does:**
- Performs exactly 7 random transactions
- Each transaction is either a deposit (case 0) or withdrawal (case 1)
- Random amounts between 0 and 100
- Waits 1 second between each transaction
- All 5 threads do this simultaneously on the same account

## Multithreading Behavior

### Thread Safety Implementation
The code uses **method-level synchronization** on the BankAccount:
- `synchronized` keyword on all methods means only one thread can execute any BankAccount method at a time
- This prevents race conditions where multiple threads could read/modify the balance simultaneously
- Each transaction (deposit/withdraw) is atomic - it either completes fully or not at all

### Execution Flow
1. All 5 threads start simultaneously
2. Each thread tries to perform transactions on the same account
3. Only one thread can access the account at any given moment (due to synchronization)
4. Threads essentially queue up and take turns
5. Each thread waits 1 second between its own transactions
6. Total execution time: roughly 35 seconds (7 transactions × 5 threads, but overlapped)

### Output Pattern
You'll see interleaved output like:
```
[Khethokuhle] Deposited: 45.67 | New Balance: 245.67
[Khethokuhle] Withdrew: 23.45 | New Balance: 222.22
[Khethokuhle] Withdrawal failed (Insufficient funds): 89.34 | Balance: 15.67
```

## Deadlock Analysis

### Current Status: NO DEADLOCK RISK
This code **cannot deadlock** because:
- Only **one shared resource** (single BankAccount)
- No **nested locking** (methods don't call other synchronized methods)
- No **circular wait conditions** possible with single resource

### Why Deadlock Isn't Possible Here
Deadlock requires at least two resources where:
- Thread A holds Resource 1, wants Resource 2
- Thread B holds Resource 2, wants Resource 1

Since all threads only ever want the same single resource (the BankAccount), they just queue up and wait their turn.

## Best Practices Observed

### ✅ Good Practices
1. **Proper synchronization** - All shared data access is synchronized
2. **Exception handling** - InterruptedException is caught and thread interrupt status restored
3. **Resource cleanup** - Using join() to wait for threads before showing results
4. **Immutable task state** - TransactionTask fields are final

### ⚠️ Areas for Improvement
1. **Coarse-grained locking** - Entire methods are synchronized, which may be overkill
2. **No timeout handling** - Threads could wait indefinitely
3. **Console I/O in synchronized block** - Printing happens while holding the lock
4. **No graceful shutdown mechanism**

## Performance Characteristics

- **Throughput**: Limited by synchronization - only one transaction at a time
- **Latency**: Each transaction must wait for lock acquisition
- **Scalability**: Adding more threads won't increase throughput, just contention

## Commented Code Analysis

The code has commented sections for a second account:
```java
//BankAccount nb = new BankAccount("Nokwanda", 200);
//private final BankAccount account2;
//System.out.printf("Nokwanda: %.2f%n", nb.getBalance());
```

If these were uncommented and used, **deadlock would become possible** if transfers between accounts were implemented without proper lock ordering.

## Summary

This is a well-structured, thread-safe banking simulation that demonstrates:
- Safe concurrent access to shared resources
- Proper thread lifecycle management  
- Basic synchronization techniques
- Deadlock-free design (by having only one shared resource)

The code serves as a good foundation for learning multithreading concepts, with clear opportunities for enhancement as complexity increases.
