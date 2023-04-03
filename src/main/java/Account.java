public class Account {
    // primary key
    private int accountId;
    private double balance;
    // user choose
    private String accountNum;

    public Account() {
    }

    public Account(double balance, String accountNum) {
        this.balance = balance;
        this.accountNum = accountNum;
    }

    public Account(int accountId, double balance, String accountNum) {
        this.accountId = accountId;
        this.balance = balance;
        this.accountNum = accountNum;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
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
