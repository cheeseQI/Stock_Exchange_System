import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    public static void main(String[] args) {
//        MatchingEngine matchingEngine = new MatchingEngine();
//        matchingEngine.start();
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
//            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
//            OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
//            List<Account> res = accountMapper.getAccountByNum("76453");
//            if (!res.isEmpty()) {
//                System.out.println("same user name already exist!");
//            }
            /* insert column */
//            Account account = new Account(10.0, "76453");
//            accountMapper.insertAccount(account);
//            sqlSession.commit();
            //Account targetAccount = accountMapper.getAccountById(1);
//            Position position = new Position(5.5, "BRZ", targetAccount);
//            positionMapper.insertPosition(position);
//            sqlSession.commit();
//
//            long transid = Counter.incrementAndGet();
//            Order order = new Order(transid, "JPN", 1, 100, Status.OPEN, targetAccount);
//            orderMapper.insertOrder(order);
//            Order splitOrder = new Order(transid, "JPN", 1, 100, Status.EXECUTED, targetAccount);
//            orderMapper.insertOrder(splitOrder);
//            sqlSession.commit();
//            System.out.println("Insert successful");

            /* select and update column */
//            Account target = accountMapper.getAccountById(1);
//            target.setBalance(101);
//            target.setAccountNum("132");
//            accountMapper.updateAccount(target);
//            sqlSession.commit();
//            Position position = positionMapper.getPositionById(1);
//            position.setAmount(1000);
//            positionMapper.updatePosition(position);
//            sqlSession.commit();
//            System.out.println("Update successful");
//            Order order = orderMapper.findOrderById(2);
//            order.setTime();
//            orderMapper.updateOrder(order);
//            sqlSession.commit();
//            System.out.println("Update successful");
//                List<Position> posList = positionMapper.getPositionsByAccountId(1);
//                for (Position pos: posList) {
//                    System.out.println(pos.getSymbol());
//                }
                /* delete column */
//            accountMapper.deleteAccount(3);
//            sqlSession.commit();
//            positionMapper.deletePositionById(2);
//            sqlSession.commit();
//            System.out.println("Delete successful");
//            orderMapper.deleteOrder(3);
//            sqlSession.commit();
//            System.out.println("Delete successful");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
