import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class OpenHandler extends ActionsHandler {
    private String accountNum;
    private String symbol;
    private String amount;
    private String limit_price;

    public OpenHandler(String accountNum, String symbol, String amount, String limit_price) {
        this.accountNum = accountNum;
        this.symbol = symbol;
        this.amount = amount;
        this.limit_price = limit_price;
    }

    @Override
    public String executeAction() {
        if (!checkAccountFormat(accountNum)) {
            return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Invalid Account number format" + "</error>";
        }
        if (!checkAccountExist(accountNum)) {
            return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Account not exist" + "</error>";
        }
        if (!checkSymbolFormat(symbol)) {
            return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Symbol must be alphanumeric characters" + "</error>";
        }
        if (!checkAmountFormat(amount)) {
            return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Amount should be positive or negative number" + "</error>";
        }
        if (!checkPositive(limit_price)) {
            return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Limit price should be positive number" + "</error>";
        }
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            int retries = 0;
            boolean success = false;
            while (!success && retries < SystemConstant.MAX_RETRY) {
                try {
                    if (Double.parseDouble(amount) < 0) { //sell
                        List<Position> positions = positionMapper.getPositionsByAccountNum(accountNum);
                        for(Position position : positions) {
                            if(position.getSymbol().equals(symbol)) { // Symbol found in the list
                                double existShare = position.getAmount();
                                double needShare = Math.abs(Double.parseDouble(amount));
                                if (needShare > existShare) {
                                    return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Account doesn't have enough shares" + "</error>";
                                }
                                position.setAmount(existShare - needShare);
                                int result = positionMapper.updatePosition(position);
                                if (result == 0) {
                                    System.out.println("position version " + position.getVersion());
                                    throw new RuntimeException("Update position failed due to concurrency conflict");
                                }
                                //sqlSession.commit();
                                //open new sell order
                                return openNewOrder(sqlSession, accountMapper, orderMapper);
                            }
                        }
                        return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Account doesn't have this symbol" + "</error>";
                    } else { //buy
                        double needBalance = Double.parseDouble(amount) * Double.parseDouble(limit_price);
                        Account accountByNum = accountMapper.getAccountByNum(accountNum);
                        double originBalance = accountByNum.getBalance();
                        if (originBalance < needBalance) {
                            return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "Account doesn't have enough balance" + "</error>";
                        }
                        accountByNum.setBalance(originBalance - needBalance);
                        int result = accountMapper.updateAccount(accountByNum);
                        if (result == 0) {
                            //System.out.println();
                            throw new RuntimeException("Update position failed due to concurrency conflict");
                        }
                        //sqlSession.commit();
                        return openNewOrder(sqlSession, accountMapper, orderMapper);
                    }
                } catch (Exception e) {
                    sqlSession.rollback();
                    retries++;
                }
            }
            if (!success) {
                return "<error sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\">" + "exceed retry times for transaction" + "</error>";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String openNewOrder(SqlSession sqlSession, AccountMapper accountMapper, OrderMapper orderMapper) {
        long trans_id = Counter.incrementAndGet();
        Account account = accountMapper.getAccountByNum(accountNum);
        Order orderOpen = new Order(trans_id, symbol, Double.parseDouble(amount), Double.parseDouble(limit_price), Status.OPEN, account);
        orderMapper.insertOrder(orderOpen);
        sqlSession.commit();
        ExecuteHandler executeHandler = new ExecuteHandler();
        executeHandler.addOrder(orderOpen);
        executeHandler.executeAction();
        return "<opened sym=\"" + symbol + "\" amount=\"" + amount + "\" limit=\"" + limit_price + "\" id=\"" + trans_id + "\"/>";
    }

    private boolean checkAmountFormat(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value > 0 || value < 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
