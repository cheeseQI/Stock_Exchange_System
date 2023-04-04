import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Order {
    private int id;
    private long transId;
    private String symbol;
    private double amount;
    private double limit_price;
    private Status status;
    private long time;
    private Account account;

    public Order(){}
    public Order(long transId, String symbol, double amount, double limit_price, Status status, Account account) {
        this.transId = transId;
        this.symbol =symbol;
        this.amount = amount;
        this.limit_price = limit_price;
        this.status = status;
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

    public String getFormalTime() {
        Date date = new Date(this.time * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public void setTime() {
        Instant instant = Instant.now();
        this.time = instant.getEpochSecond();
    }

    public long getTransId() {
        return transId;
    }

    public void setTransId(long trans_id) {
        this.transId = trans_id;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
