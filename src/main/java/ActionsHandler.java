import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ActionsHandler {

    public abstract String executeAction();

    public boolean checkAccountFormat(String accountNum) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(accountNum);
        return matcher.matches();
    }

    public boolean checkAccountExist(String accountNum) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            List<Account> res = accountMapper.getAccountByNum(accountNum);
            return !res.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean checkPositive(String num) { // check num is positive number
        try {
            double d = Double.parseDouble(num);
            return (d > 0);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
