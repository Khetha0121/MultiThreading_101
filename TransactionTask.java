package TransactionThreading;

class TransactionTask implements Runnable {
    private final BankAccount account1;
    //private final BankAccount account2;

    public TransactionTask(BankAccount a1) {
        this.account1 = a1;
        //this.account2 = a2;
    }

    @Override
    public void run() {
        for (int i = 0; i < 7; i++) {
            double amount = Math.random() * 100;
            int action = (int) (Math.random() * 2);
            
            System.out.println(Thread.currentThread().getName());

            switch (action) {
                case 0:
                    account1.deposit(amount);
                    break;
                case 1:
                    account1.withdraw(amount);
                    break;
            
            }
            

            try {
                Thread.sleep(7000); // simulate delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

