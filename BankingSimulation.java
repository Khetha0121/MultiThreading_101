package TransactionThreading;

public class BankingSimulation {
    public static void main(String[] args) {
        BankAccount kk = new BankAccount("Khethokuhle", 100);
        //BankAccount nb = new BankAccount("Nokwanda", 200);

        Thread t1 = new Thread(new TransactionTask(kk));
        Thread t2 = new Thread(new TransactionTask(kk));
        Thread t3 = new Thread(new TransactionTask(kk));
        Thread t4 = new Thread(new TransactionTask(kk));
        Thread t5 = new Thread(new TransactionTask(kk));
        
        
        t1.currentThread().setName("T1");
        t2.currentThread().setName("T2");
        t3.currentThread().setName("T3");
        t4.currentThread().setName("T4");
        t5.currentThread().setName("T5");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n--- Final Balances ---");
        System.out.printf("Khethokuhle: %.2f%n", kk.getBalance());
        //System.out.printf("Nokwanda: %.2f%n", nb.getBalance());
    }
}
