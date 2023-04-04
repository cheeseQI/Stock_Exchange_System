import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class CancelHandler extends ActionsHandler {
    private long transactionId;
    private int accountId;

    public CancelHandler(long transactionId, int accountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
    }

    @Override
    public String executeAction() {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            List<Order> orderList = orderMapper.findOrderByTransId(transactionId);
            for (Order order: orderList) {
                if (order.getStatus() == Status.OPEN) {
                    order.setStatus(Status.CANCELED);
                    // cancel buy - add balance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getAccountId() {
        return accountId;
    }

    public long getTransactionId() {
        return transactionId;
    }
}
