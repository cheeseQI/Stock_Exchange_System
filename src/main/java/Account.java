public class Account {
    private String accountId;
    private double balance;
    //private HashSet<Position> positions; //todo: may need delete this position set, and use multiple table select
    public Account() {
    }

    public Account(double balance) {
        this.balance = balance;
    }

    public Account(String accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }
}
