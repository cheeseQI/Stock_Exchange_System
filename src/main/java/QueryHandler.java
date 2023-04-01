public class QueryHandler extends OrderActionsHandler{
    private int transaction_id;
    private int account_id;

    public QueryHandler(int transaction_id, int account_id) {
        this.transaction_id = transaction_id;
        this.account_id = account_id;
    }

    @Override
    public void executeAction() {

    }

    public int getAccount_id() {
        return account_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }
}
