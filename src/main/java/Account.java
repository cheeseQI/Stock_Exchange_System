import java.util.HashSet;

public class Account {
    private String account_num;
    private double balance;
    private HashSet<Position> positions; //TODO: figure out if need to change to map for convenience

    public Account(String account_num, double balance) {
        this.account_num = account_num;
        this.balance = balance;
        this.positions = new HashSet<>();
    }

    public String getAccount_num() {
        return account_num;
    }

    public double getBalance() {
        return balance;
    }

    public HashSet<Position> getPositions() {
        return positions;
    }

    public void setAccount_num(String account_num) {
        this.account_num = account_num;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPositions(HashSet<Position> positions) {
        this.positions = positions;
    }
}
