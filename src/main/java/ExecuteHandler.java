import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExecuteHandler extends ActionsHandler {
    private ArrayList<Order> buyOrders;
    private ArrayList<Order> sellOrders;

    public ExecuteHandler() {
        this.buyOrders = new ArrayList<>();
        this.sellOrders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            List<Order> orders = orderMapper.findOrderBySymbolAndStatus(order.getSymbol(), Status.OPEN);
            System.out.println(orders.size());
            sqlSession.commit();
            for (Order ord : orders) {
                insertToList(ord);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertToList(Order order) {
        if (order.getAmount() > 0) { //buy
            buyOrders.add(order);
            buyOrders.sort(Comparator.comparingDouble(Order::getLimit_price).reversed().thenComparingLong(Order::getTransId));
        } else { //sell
            sellOrders.add(order);
            sellOrders.sort(Comparator.comparingDouble(Order::getLimit_price).thenComparingLong(Order::getTransId));
        }
    }

    @Override
    public String executeAction() { //match orders
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.get(0);
            Order sellOrder = sellOrders.get(0);

            if (buyOrder.getLimit_price() >= sellOrder.getLimit_price()) { //match
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

                SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
                try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                    int retries = 0;
                    boolean success = false;
                    while (!success && retries < SystemConstant.MAX_RETRY) {
                        try {
                            orderInfoUpdate(sqlSession, buyOrder, matchedAmount, buyOrders, matchPrice, false);
                            sellOrder.setAmount(sellOrder.getAmount() + matchedAmount);
                            orderInfoUpdate(sqlSession, sellOrder, matchedAmount, sellOrders, matchPrice, true);
                            success = true;
                        } catch (Exception e) {
                            sqlSession.rollback();
                            retries++;
                        }
                    }
                    if (!success) {
                        return null; // todo: any action for exceed limit
                    }
                    sqlSession.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return null;
    }

    private void orderInfoUpdate(SqlSession sqlSession, Order order, double matchedAmount, ArrayList<Order> orders, double matchPrice, boolean sell) {
        PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
        AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
        OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
        if (order.getAmount() == 0) {
            Order orderById = orderMapper.findOrderById(order.getId());
            orderById.setLimit_price(matchPrice);
            orderById.setStatus(Status.EXECUTED);
            orderById.setTime();
            int result = orderMapper.updateOrder(orderById);
            if (result == 0) {
                throw new RuntimeException("Update order failed due to concurrency conflict");
            }
            //sqlSession.commit();
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
            List<Order> orderOpens = orderMapper.findOrderByTransId(orderOpen.getTransId()); //to get the orderOpen's id
            //sqlSession.commit();
            for (Order order1 : orderOpens) {
                if (order1.getStatus().equals(Status.OPEN)) {
                    orders.set(0, order1); // Replace the original order with the split order
                    break;
                }
            }
        }
    }

    private void updateAccountAndPosition(Order order, double matchedAmount, double matchPrice, boolean sell, SqlSession sqlSession, AccountMapper accountMapper, PositionMapper positionMapper) {
        if (sell) { //update account
            int accountId = order.getAccount().getAccountId();
            Account sellAccount = accountMapper.getAccountById(accountId);
            sellAccount.setBalance(sellAccount.getBalance() + matchedAmount * matchPrice);
            int result = accountMapper.updateAccount(sellAccount);
            if (result == 0) {
                throw new RuntimeException("Update account failed due to concurrency conflict");
            }
            //sqlSession.commit();
        }
        else { //update position (and refund balance to account)
            Account buyAccount = accountMapper.getAccountById(order.getAccount().getAccountId());
            if (matchPrice != order.getLimit_price()) {
                buyAccount.setBalance(buyAccount.getBalance() + matchedAmount * (order.getLimit_price() - matchPrice));
                accountMapper.updateAccount(buyAccount);
                //sqlSession.commit();
            }
            updatePositionInfo(order.getSymbol(), buyAccount.getAccountNum(), matchedAmount, sqlSession, accountMapper, positionMapper);
        }
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
                int result = positionMapper.updatePosition(position);
                if (result == 0) {
                    throw new RuntimeException("Update position failed due to concurrency conflict");
                }
                //sqlSession.commit();
                break;
            }
        }
        if (!found) { //create symbol
            Account account = accountMapper.getAccountByNum(accountNum);
            Position position = new Position(matchedAmount, symbol, account);
            positionMapper.insertPosition(position);
            //sqlSession.commit();
        }
    }
}