public class Account {
    private int accountId;
    private double balance;
    //private HashSet<Position> positions; //todo: may need delete this position set, and use multiple table select
    public Account() {
    }
    public Account(double balance) {
        this.balance = balance;
    }

    public Account(int accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }
}
