public class Position {
    private int positionId;
    private double amount;
    private String symbol;
    private Account account;

    public Position() {
    }

    public Position(int positionId, double amount, String symbol, Account account) {
        this.positionId = positionId;
        this.amount = amount;
        this.symbol = symbol;
        this.account = account;
    }

    public Position(double amount, String symbol, Account account) {
        this.amount = amount;
        this.symbol = symbol;
        this.account = account;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }
}
