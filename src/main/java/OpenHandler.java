public class OpenHandler extends OrderActionsHandler{
    private int transaction_id;

    public OpenHandler(int id) {
        this.transaction_id = id;
    }

    @Override
    public void executeAction(){}
}
