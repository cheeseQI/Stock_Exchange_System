import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.SQLException;
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
            int retries = 0;
            boolean success = false;
            while (!success && retries < SystemConstant.MAX_RETRY) {
                try {
                    List<Order> orderList = orderMapper.findOrderByTransId(transactionId);
                    for (Order order: orderList) {
                        if (!order.getAccount().getAccountNum().equals(accountNum)) {
                            res += "<error id=\"" + transactionId + "\">" + "you have no permission to query order does not belong to you" + "</error>\n";
                            continue;
                        }
                        if (order.getStatus() == Status.OPEN) {
                            order.setStatus(Status.CANCELED);
                            order.setTime();
                            int resultForOrder = orderMapper.updateOrder(order);
                            if (resultForOrder == 0) {
                                //System.out.println(order.getVersion() + " is the version");
                                throw new RuntimeException("Update order failed due to concurrency conflict");
                            }
                            //  add to old position || new position
                            double price = order.getLimit_price();
                            if (price < 0) {
                                Account accountToBeRefund = order.getAccount();
//                                System.out.println(order.getSymbol() + " account num: " + accountToBeRefund.getAccountNum() + " balance" + accountToBeRefund.getBalance()
//                                        + "price " + price + "amount: " + order.getAmount());
                                accountToBeRefund.setBalance(accountToBeRefund.getBalance() + price * order.getAmount());
                                //System.out.println("new balance: " + accountToBeRefund.getBalance());
                                int resultForAccount = accountMapper.updateAccount(accountToBeRefund);
                                if (resultForAccount == 0) {
                                    //System.out.println(order.getVersion() + " is the version");
                                    throw new RuntimeException("Update account failed due to concurrency conflict");
                                }
                            } else {
                                List<Position> positionList = positionMapper.getPositionsByAccountNum(accountNum);
                                boolean hasOldPos = false;
                                for (Position posToBeReAdd: positionList) {
                                    if (posToBeReAdd.getSymbol().equals(order.getSymbol())) {
                                        hasOldPos = true;
                                        posToBeReAdd.setAmount(posToBeReAdd.getAmount() + order.getAmount());
                                        //System.out.println("set " + posToBeReAdd.getSymbol() + " to " +posToBeReAdd.getAmount());
                                        int resultForPosition = positionMapper.updatePosition(posToBeReAdd);
                                        if (resultForPosition == 0) {
                                            //System.out.println(order.getVersion() + " is the version");
                                            throw new RuntimeException("Update position failed due to concurrency conflict");
                                        }
                                        break;
                                    }
                                }
                                if (!hasOldPos) {
                                    Account account = accountMapper.getAccountByNum(accountNum);
                                    Position posToBeReAdd = new Position(order.getAmount(), order.getSymbol(), account);
                                    positionMapper.insertPosition(posToBeReAdd);
                                    //System.out.println("add new " + posToBeReAdd.getSymbol() + " to " + posToBeReAdd.getAmount());
                                }
                            }
                            res += "<canceled shares=\"" + order.getAmount() + "\" time=\"" + order.getFormalTime() + "\">\n";
                            sqlSession.commit();
                        } else if (order.getStatus() == Status.EXECUTED) {
                            res += "<executed shares=\"" + order.getAmount() + "\" price=\"" + order.getLimit_price() +"\" time=\"" + order.getFormalTime() + "\">\n";
                        }
                    }
                    success = true;
                } catch (Exception e) {
                    sqlSession.rollback();
                    retries++;
                }
            }
            if (!success) {
                res += "<error id=\"" + transactionId + "\">" + "exceed retry times for transaction" + "</error>\n";
                res += "</canceled>\n";
                return res;
            }
            res += "</canceled>\n";
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
