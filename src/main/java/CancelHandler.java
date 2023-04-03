public class CancelHandler extends ActionsHandler {
    private int transaction_id;
    private int account_id;

    public CancelHandler(int transaction_id, int account_id) {
        this.transaction_id = transaction_id;
        this.account_id = account_id;
    }

    @Override
    public String executeAction() {
        return null;
    }

    public int getAccount_id() {
        return account_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }
}
