import java.util.ArrayList;
import java.util.Comparator;

public class ExecuteHandler extends ActionsHandler {
    private static ArrayList<Order> buyOrders;
    private static ArrayList<Order> sellOrders;

    public ExecuteHandler() {
        buyOrders = new ArrayList<>();
        sellOrders = new ArrayList<>();
    }


    @Override
    public String executeAction(){
        //addOrder

        //matchOrder

        //TODO: update account info

        //TODO: update position info

        //TODO: write to database
        return null;
    }

    public void addOrder(Order order) {
        if (order.getAmount() > 0) { //buy
            buyOrders.add(order);
            buyOrders.sort(Comparator.comparingDouble(Order::getLimit_price).reversed().thenComparingInt(Order::getId));
        } else { //sell
            sellOrders.add(order);
            sellOrders.sort(Comparator.comparingDouble(Order::getLimit_price).thenComparingInt(Order::getId));
        }
    }

    private void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.get(0);
            Order sellOrder = sellOrders.get(0);

            if (buyOrder.getLimit_price() >= sellOrder.getLimit_price()) {
                double matchedAmount = Math.min(buyOrder.getAmount(), sellOrder.getAmount());

                buyOrder.setAmount(buyOrder.getAmount() - matchedAmount);
                orderInfoUpdate(buyOrder, matchedAmount, buyOrders);
                sellOrder.setAmount(sellOrder.getAmount() - matchedAmount);
                orderInfoUpdate(sellOrder, matchedAmount, sellOrders);
            }
            else {
                break;
            }
        }
    }

    private void orderInfoUpdate(Order order, double machtedAmount, ArrayList<Order> orders) {
        if (order.getAmount() == 0) {
            order.setStatus(Status.EXECUTED);
            order.setTime();
            orders.remove(0);
        }
        else {
            //split order
            // todo: 占位符transid，后面需要自己填
            Order orderOpen = new Order(-1, order.getSymbol(), order.getAmount() - machtedAmount, order.getLimit_price(), Status.OPEN, order.getAccount());
            Order orderExecuted = new Order(-1, order.getSymbol(), machtedAmount, order.getLimit_price(), Status.EXECUTED, order.getAccount());
            orders.set(0, orderOpen); // Replace the original order with the split order
            // TODO: delete the origin order from database

            // TODO: write to database
        }
    }
}
