import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class QueryHandler extends ActionsHandler {
    private long transactionId;
    private String accountNum;

    public QueryHandler(long transactionId, String accountNum) {
        this.transactionId = transactionId;
        this.accountNum = accountNum;
    }

    @Override
    public String executeAction() {
        String res = "<status id=\"" + transactionId + "\">\n";
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
            List<Order> orderList = orderMapper.findOrderByTransId(transactionId);
            if (orderList.isEmpty()) {
                res += "<error id=\"" + transactionId + "\">" + "transaction id does not exist" + "</error>\n";
                res += "</status>\n";
                System.out.println(res);
                return res;
            }
            for (Order order: orderList) {
                if (!order.getAccount().getAccountNum().equals(accountNum)) {
                    res += "<error id=\"" + transactionId + "\">" + "you have no permission to query order does not belong to you" + "</error>\n";
                    continue;
                }
                switch (order.getStatus()) {
                    case OPEN:
                        res += "<open shares=\"" + order.getAmount() + "\">\n";
                        break;
                    case EXECUTED:
                        // todo: please change the limit price -> real price after execute!
                        res += "<executed shares=\"" + order.getAmount() + "\" price=\"" + order.getLimit_price() +"\" time=\"" + order.getTime() + "\">\n";
                        break;
                    case CANCELED:
                        res += "<canceled shares=\"" + order.getAmount() + "\" time=\"" + order.getTime() + "\">\n";
                        break;
                    default:
                        break;
                }
            }
            res += "</status>\n";
            System.out.println(res);
            return res;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public long getTransactionId() {
        return transactionId;
    }
}
