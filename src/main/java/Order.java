import java.time.Instant;
import java.util.Set;

public class Order {
    private int id; //transaction_id
    private String symbol;
    private double amount;
    private double limit_price;
    private Status status;
    private long time;
    private Account account;

    public Order(){}
    public Order(String symbol, double amount, double limit_price, Status status, Account account) {
        this.symbol =symbol;
        this.amount = amount;
        this.limit_price = limit_price;
        this.status = status;
        this.account = account;
        Instant instant = Instant.now();
        this.time = instant.getEpochSecond();
    }

    public Order(String symbol, double amount, double limit_price, Account account) {
        this.symbol =symbol;
        this.amount = amount;
        this.limit_price = limit_price;
        this.account = account;
        Instant instant = Instant.now();
        this.time = instant.getEpochSecond();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getLimit_price() {
        return limit_price;
    }

    public void setLimit_price(double limit_price) {
        this.limit_price = limit_price;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime() {
        Instant instant = Instant.now();
        this.time = instant.getEpochSecond();
    }

}
