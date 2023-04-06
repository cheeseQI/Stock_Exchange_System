import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class CreateSymbolHandler extends ActionsHandler {
    private String symbol;
    private String accountNum;
    private String amount;

    public CreateSymbolHandler(String symbol, String accountNum, String amount) {
        this.symbol = symbol;
        this.accountNum = accountNum;
        this.amount = amount;
    }

    @Override
    public String executeAction() {
        //check account exist: exist -> add; no exist -> error
        if (!checkAccountFormat(accountNum)) {
            return "<error sym=\"" + symbol + "\" id=\"" + accountNum + "\">" + "Invalid Account number format" + "</error>";
        }
        if (!checkAccountExist(accountNum)) {
            return "<error sym=\"" + symbol + "\" id=\"" + accountNum + "\">" + "Account not exist" + "</error>";
        }
        if (!checkPositive(amount)) { //check num is positive
            return "<error sym=\"" + symbol + "\" id=\"" + accountNum + "\">" + "NUM shares of the symbol must be positive" + "</error>";
        }
        if (!checkSymbolFormat(symbol)) {
            return "<error sym=\"" + symbol + "\" id=\"" + accountNum + "\">" + "Symbol must be alphanumeric characters" + "</error>";
        }
        return updatePosition(symbol, accountNum, amount);
    }

    private String updatePosition(String symbol, String accountNum, String amount) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            List<Position> positions = positionMapper.getPositionsByAccountNum(accountNum);
            Account account = accountMapper.getAccountByNum(accountNum);
            Position position = new Position(Double.parseDouble(amount), symbol, account);
            positionMapper.insertOrUpdatePosition(position);
            sqlSession.commit();
            return "<created sym=\"" + symbol + "\" id=\"" + accountNum + "\"/>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<error id=\"" + accountNum + "\">" + "cannot open sql session" + "</error>";
    }
}
