# Thread-Safe Banking System - Documentation & Best Practices

## Overview

This Java application demonstrates a banking system with concurrent transaction processing using multiple threads. The system consists of three classes that work together to simulate banking operations while addressing thread safety concerns and potential deadlock conditions.

## Class Architecture

### 1. BankAccount Class

The `BankAccount` class represents a bank account with basic banking operations.

#### Key Attributes
```java
private String accountHolder;    // Account owner's name
private double balance;          // Current account balance
```

#### Constructor
```java
public BankAccount(String accountHolder, double initialBalance)
```
- Initializes account with holder name and starting balance
- Simple parameter assignment without additional validation

#### Core Operations

**Deposit Operation**
```java
public synchronized void deposit(double amount)
```
- **Thread Safety**: Uses `synchronized` keyword for method-level locking
- **Atomic Operation**: Balance update is thread-safe
- **Logging**: Provides transaction feedback with formatted output

**Withdrawal Operation**
```java
public synchronized boolean withdraw(double amount)
```
- **Balance Validation**: Checks sufficient funds before withdrawal
- **Return Value**: Boolean indicates success/failure
- **Atomic Operation**: Balance check and update performed atomically
- **Error Handling**: Logs insufficient funds scenarios

**Transfer Operation**
```java
public void transferTo(BankAccount target, double amount)
```
- **Non-synchronized method**: Only calls synchronized methods
- **Conditional Transfer**: Only proceeds if withdrawal is successful
- **Logging**: Provides transfer confirmation with recipient details

**Balance Inquiry**
```java
public double getBalance()
```
- **Simple getter**: Returns current balance
- **Note**: Not synchronized in original implementation

### 2. TransactionTask Class

The `TransactionTask` class implements `Runnable` to perform concurrent banking operations.

#### Attributes
```java
private final BankAccount account1;    // Primary account for operations
private final BankAccount account2;    // Secondary account for transfers
```

#### Constructor
```java
public TransactionTask(BankAccount a1, BankAccount a2)
```
- Takes two accounts to operate between
- Stores references as final fields

#### Run Method
```java
public void run()
```
- Executes 5 random banking operations per thread
- Uses `Math.random()` for random amount and action selection
- Three possible actions: deposit (0), withdraw (1), transfer (2)
- Implements 500ms fixed delay with `Thread.sleep()`
- Handles `InterruptedException` with proper thread interruption

### 3. BankingSimulation Class (Main)

The main class orchestrates the concurrent banking simulation.

#### Key Features
- Creates two bank accounts: "Khethokuhle" and "Nokwanda" with $200 each
- Launches two threads with cross-account operations
- Uses `Thread.join()` to wait for completion
- Displays final account balances

## Thread Safety Analysis

### Synchronized Methods Used
The original code uses `synchronized` methods for thread safety:

```java
public synchronized void deposit(double amount)
public synchronized boolean withdraw(double amount)
```

### Benefits of Synchronized Methods
- **Automatic Locking**: JVM handles lock acquisition and release
- **Method-Level Protection**: Entire method execution is atomic
- **Simplicity**: No explicit lock management required

### Potential Issues with Current Implementation

#### 1. Race Conditions in Transfer Operations
```java
public void transferTo(BankAccount target, double amount) {
    if (withdraw(amount)) {
        target.deposit(amount);
        // Potential issue: Gap between withdraw and deposit
    }
}
```

**Problem**: The transfer operation is not atomic. Between the `withdraw()` and `deposit()` calls, other threads could interfere.

#### 2. Deadlock Vulnerability
The current implementation is susceptible to deadlocks in the following scenario:

**Deadlock Scenario:**
```
Thread 1: account1.transferTo(account2, amount)
Thread 2: account2.transferTo(account1, amount)
```

**What happens:**
1. Thread 1 locks account1 (in withdraw)
2. Thread 2 locks account2 (in withdraw) 
3. Thread 1 tries to lock account2 (in deposit) - BLOCKED
4. Thread 2 tries to lock account1 (in deposit) - BLOCKED
5. **DEADLOCK OCCURS**

#### 3. Unsynchronized getBalance()
```java
public double getBalance() {
    return balance;  // Not synchronized - potential race condition
}
```

## Best Practices Present in Original Code

### 1. Proper Exception Handling
```java
try {
    Thread.sleep(500);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();  // Restores interrupt status
}
```

### 2. Thread Lifecycle Management
```java
t1.start();
t2.start();

try {
    t1.join();  // Wait for thread completion
    t2.join();
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### 3. Defensive Programming
- Balance validation before withdrawal
- Conditional transfer logic
- Comprehensive logging for debugging

### 4. Clean Object-Oriented Design
- Clear separation of concerns
- Encapsulation of account data
- Immutable task configuration

## Deadlock Conditions Analysis

### The Four Coffman Conditions
1. **Mutual Exclusion**: ✓ Present (synchronized methods)
2. **Hold and Wait**: ✓ Present (transfer holds one lock, waits for another)
3. **No Preemption**: ✓ Present (synchronized locks cannot be interrupted)
4. **Circular Wait**: ✓ **POSSIBLE** (cross-transfers can create circular dependency)

### Current Deadlock Risk
The system satisfies all four Coffman conditions, making deadlock possible when:
- Multiple threads perform transfers between the same accounts
- Transfers occur in opposite directions simultaneously

## Testing Scenarios

### Normal Operation Flow
```
Initial State: Khethokuhle=$200, Nokwanda=$200
Thread 1: Operates on (Khethokuhle → Nokwanda)
Thread 2: Operates on (Nokwanda → Khethokuhle)
Each performs 5 random operations with 500ms delays
```

### Money Conservation
The system should maintain total money conservation:
```
Initial Total: $400
Final Total: Should remain $400 (unless operations fail due to insufficient funds)
```

## Potential Improvements

### 1. Atomic Transfer Operations
Implement transfers as single synchronized block:
```java
public synchronized void transferTo(BankAccount target, double amount) {
    synchronized(target) {
        // Atomic transfer logic
    }
}
```

### 2. Deadlock Prevention
- Implement ordered locking (always lock accounts in consistent order)
- Use timeout-based locking mechanisms
- Avoid nested synchronization where possible

### 3. Enhanced Error Handling
- Custom exception types for banking operations
- More detailed transaction logging
- Input validation for amounts

### 4. Thread-Safe Balance Inquiry
```java
public synchronized double getBalance() {
    return balance;
}
```

## Conclusion

The original banking system demonstrates basic thread safety using synchronized methods but is vulnerable to deadlocks during concurrent transfer operations. While the implementation shows good practices in thread lifecycle management and exception handling, it requires improvements to handle the classic "dining philosophers" style deadlock scenario that can occur with bidirectional transfers.

The system serves as an excellent learning example for understanding:
- Basic thread synchronization
- Deadlock conditions and causes
- Race condition prevention
- Concurrent programming challenges in financial systems
