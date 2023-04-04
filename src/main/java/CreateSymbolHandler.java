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
        return updatePostion(symbol, accountNum, amount);
    }

    private String updatePostion(String symbol, String accountNum, String amount) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            List<Position> positions = positionMapper.getPositionsByAccountNum(accountNum);
            // find if symbol in list
            boolean found = false;
            for(Position position : positions) {
                if(position.getSymbol().equals(symbol)) { // Symbol found in the list
                    found = true;
                    double newShare = position.getAmount() + Double.parseDouble(amount);
                    position.setAmount(newShare);
                    positionMapper.updatePosition(position);
                    sqlSession.commit();
                    break;
                }
            }
            if (!found) { //create symbol
                Account account = accountMapper.getAccountByNum(accountNum);
                Position position = new Position(Double.parseDouble(amount), symbol, account);
                positionMapper.insertPosition(position);
                sqlSession.commit();
            }
            return "<created sym=\"" + symbol + "\" id=\"" + accountNum + "\"/>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
