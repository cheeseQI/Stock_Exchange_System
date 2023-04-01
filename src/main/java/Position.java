public class Position {
    public String account_num;
    private String symbol;
    private Account account;

    public Position(String account_num, String symbol, Account account) {
        this.account_num = account_num;
        this.symbol = symbol;
        this.account = account;
    }

    public String getAccount_num() {
        return account_num;
    }

    public void setAccount_num(String account_num) {
        this.account_num = account_num;
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
