import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExecuteHandler extends ActionsHandler {
    private static ArrayList<Order> buyOrders;
    private static ArrayList<Order> sellOrders;

    static {
        buyOrders = new ArrayList<>();
        sellOrders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        if (order.getAmount() > 0) { //buy
            buyOrders.add(order);
            buyOrders.sort(Comparator.comparingDouble(Order::getLimit_price).reversed().thenComparingLong(Order::getTransId));
        } else { //sell
            sellOrders.add(order);
            sellOrders.sort(Comparator.comparingDouble(Order::getLimit_price).thenComparingLong(Order::getTransId));
        }
    }

    public void deleteOrder(Order order) {
        if (order.getAmount() > 0) { //buy
            buyOrders.remove(order);
        } else if (order.getAmount() < 0) { //sell
            sellOrders.remove(order);
        }
    }

    @Override
    public String executeAction() { //match orders
        for (Order order: buyOrders) {
            System.out.println(order.getSymbol() + " " + order.getAmount());
        }
        System.out.println("sells: ");
        for (Order order: sellOrders) {
            System.out.println(order.getSymbol() + " " + order.getAmount());
        }
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.get(0);
            Order sellOrder = sellOrders.get(0);

            if (buyOrder.getLimit_price() >= sellOrder.getLimit_price()) {
                double sellAmount = Math.abs(sellOrder.getAmount());
                double matchedAmount = Math.min(buyOrder.getAmount(), sellAmount);
                buyOrder.setAmount(buyOrder.getAmount() - matchedAmount);
                double matchPrice;
                if (buyOrder.getTransId() > sellOrder.getTransId()) {
                    matchPrice = sellOrder.getLimit_price();
                }
                else {
                    matchPrice = buyOrder.getLimit_price();
                }
                orderInfoUpdate(buyOrder, matchedAmount, buyOrders, matchPrice, false);
                sellOrder.setAmount(sellOrder.getAmount() + matchedAmount);
                orderInfoUpdate(sellOrder, matchedAmount, sellOrders, matchPrice, true);
            }
            else {
                break;
            }
        }
        return null;
    }

    private void orderInfoUpdate(Order order, double matchedAmount, ArrayList<Order> orders, double matchPrice, boolean sell) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            if (order.getAmount() == 0) {
                order.setLimit_price(matchPrice);
                order.setStatus(Status.EXECUTED);
                order.setTime();
                orderMapper.updateOrder(order);
                sqlSession.commit();
                updateAccountAndPosition(order, matchedAmount, matchPrice, sell, sqlSession, accountMapper, positionMapper);
                orders.remove(0);
            }
            else {//split order
                Order orderOpen = new Order(order.getTransId(), order.getSymbol(), order.getAmount(), order.getLimit_price(), Status.OPEN, order.getAccount());
                Order orderExecuted;// Replace the original order with the split order
                if (sell) {
                    orderExecuted = new Order(order.getTransId(), order.getSymbol(), -matchedAmount, matchPrice, Status.EXECUTED, order.getAccount());
                }
                else {
                    orderExecuted = new Order(order.getTransId(), order.getSymbol(), matchedAmount, matchPrice, Status.EXECUTED, order.getAccount());
                }
                updateAccountAndPosition(order, matchedAmount, matchPrice, sell, sqlSession, accountMapper, positionMapper);

                orderMapper.deleteOrder(order.getId());
                orderMapper.insertOrder(orderExecuted);
                orderMapper.insertOrder(orderOpen);
                sqlSession.commit();

                orders.set(0, orderOpen); // Replace the original order with the split order
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAccountAndPosition(Order order, double matchedAmount, double matchPrice, boolean sell, SqlSession sqlSession, AccountMapper accountMapper, PositionMapper positionMapper) {
        if (sell) { //update account
            Account sellAccount = accountMapper.getAccountByNum(order.getAccount().getAccountNum());
            sellAccount.setBalance(sellAccount.getBalance() + matchedAmount * matchPrice);
            accountMapper.updateAccount(sellAccount);
            sqlSession.commit();
        }
        else { //update position
            Account buyAccount = order.getAccount();
            updatePositionInfo(order.getSymbol(), buyAccount.getAccountNum(), matchedAmount, sqlSession, accountMapper, positionMapper);
        }
        sqlSession.commit();
    }

    private void updatePositionInfo(String symbol, String accountNum, double matchedAmount, SqlSession sqlSession, AccountMapper accountMapper, PositionMapper positionMapper) {
        List<Position> positions = positionMapper.getPositionsByAccountNum(accountNum);
        // find if symbol in list
        boolean found = false;
        for(Position position : positions) {
            if(position.getSymbol().equals(symbol)) { // Symbol found in the list
                found = true;
                double newShare = position.getAmount() + matchedAmount;
                position.setAmount(newShare);
                positionMapper.updatePosition(position);
                sqlSession.commit();
                break;
            }
        }
        if (!found) { //create symbol
            Account account = accountMapper.getAccountByNum(accountNum);
            Position position = new Position(matchedAmount, symbol, account);
            positionMapper.insertPosition(position);
            sqlSession.commit();
        }
    }
}
