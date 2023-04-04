import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class CancelHandler extends ActionsHandler {
    private long transactionId;
    private String accountNum;

    public CancelHandler(long transactionId, String accountId) {
        this.transactionId = transactionId;
        this.accountNum = accountId;
    }

    @Override
    public String executeAction() {
        String res = "<canceled id=\"" + transactionId + "\">\n";
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            List<Order> orderList = orderMapper.findOrderByTransId(transactionId);
            for (Order order: orderList) {
                if (!order.getAccount().getAccountNum().equals(accountNum)) {
                    res += "<error id=\"" + transactionId + "\">" + "you have no permission to query order does not belong to you" + "</error>\n";
                    continue;
                }
                if (order.getStatus() == Status.OPEN) {
                    order.setStatus(Status.CANCELED);
                    order.setTime();
                    orderMapper.updateOrder(order);
                    //  add to old position || new position
                    double price = order.getLimit_price();
                    // todo: how to make sure refund is atomic with set cancel status? maybe lua script? use lock is also ok!
                    if (price < 0) {
                        Account accountToBeRefund = order.getAccount();
                        System.out.println(order.getSymbol() + " account num: " + accountToBeRefund.getAccountNum() + " balance" + accountToBeRefund.getBalance()
                                + "price " + price + "amount: " + order.getAmount());
                        accountToBeRefund.setBalance(accountToBeRefund.getBalance() + price * order.getAmount());
                        System.out.println("new balance: " + accountToBeRefund.getBalance());
                        accountMapper.updateAccount(accountToBeRefund);
                    } else {
                        List<Position> positionList = positionMapper.getPositionsByAccountNum(accountNum);
                        boolean hasOldPos = false;
                        for (Position posToBeReAdd: positionList) {
                            if (posToBeReAdd.getSymbol().equals(order.getSymbol())) {
                                hasOldPos = true;
                                posToBeReAdd.setAmount(posToBeReAdd.getAmount() + order.getAmount());
                                System.out.println("set " + posToBeReAdd.getSymbol() + " to " +posToBeReAdd.getAmount());
                                positionMapper.updatePosition(posToBeReAdd);
                                break;
                            }
                        }
                        if (!hasOldPos) {
                            Account account = accountMapper.getAccountByNum(accountNum);
                            Position posToBeReAdd = new Position(order.getAmount(), order.getSymbol(), account);
                            positionMapper.insertPosition(posToBeReAdd);
                            System.out.println("add new " + posToBeReAdd.getSymbol() + " to " + posToBeReAdd.getAmount());
                        }
                    }
                    res += "<canceled shares=\"" + order.getAmount() + "\" time=\"" + order.getFormalTime() + "\">\n";
                    sqlSession.commit();
                } else if (order.getStatus() == Status.EXECUTED) {
                    res += "<executed shares=\"" + order.getAmount() + "\" price=\"" + order.getLimit_price() +"\" time=\"" + order.getFormalTime() + "\">\n";
                }
            }
            res += "</canceled>\n";
            System.out.println(res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAccountId() {
        return accountNum;
    }

    public long getTransactionId() {
        return transactionId;
    }
}
