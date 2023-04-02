public class Position {
    public double amount;
    private String symbol;
    private Account account;

    public Position(double amount, String symbol, Account account) {
        this.amount= amount;
        this.symbol = symbol;
        this.account = account;
    }

    public double getAccount_num() {
        return amount;
    }

    public void setAccount_num(double amount) {
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
}
