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
        //check num is positive
        if (!checkPositive(amount)) {
            return "<error sym=\"" + symbol + "\" id=\"" + accountNum + "\">" + "NUM shares of the symbol must be positive" + "</error>";
        }
        return updatePostion(symbol, accountNum, amount);
    }

    private String updatePostion(String symbol, String accountNum, String amount) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            PositionMapper positionMapper = sqlSession.getMapper(PositionMapper.class);
            List<Position> positions = positionMapper.getPositionsByAccountNum(accountNum); //TODO
            // find if symbol in list
            for(Position position : positions) {
                if(position.getSymbol().equals(symbol)) { // Symbol found in the list
                    double newShare = position.getAmount() + Double.parseDouble(amount);
                    position.setAmount(newShare);
                    positionMapper.updatePosition(position);
                    sqlSession.commit();
                    break;
                }
            }
            if (res.isEmpty()) { //create symbol

            }
            else { // add share

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    <symbol sym="SYM"> #0 or more
        <account id="ACCOUNT_ID">NUM</account> #1 or more
    </symbol>
     */
    //<created sym="SYM" id="ACCOUNT_ID"/>
    //<error sym="SYM" id="ACCOUNT_ID">Msg</error>
}
