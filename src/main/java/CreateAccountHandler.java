import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class CreateAccountHandler extends ActionsHandler {
    private String accountNum;
    private String balance;

    public CreateAccountHandler(String accountNum, String balance) {
        this.accountNum = accountNum;
        this.balance = balance;
    }

    String checkAccountNum(String accountNum) {
        if (!checkAccountFormat(accountNum)) {
            return "Invalid account number format";
        }
        if (checkAccountExist(accountNum)) {
            return "Account already exists";
        }
        return null;
    }

    @Override
    public String executeAction() {
        String checkAccountNumRes = checkAccountNum(accountNum);
        if (checkAccountNumRes != null) {
            return  "<error id=\"" + accountNum + "\">" + checkAccountNumRes + "</error>";
        }
        if (!checkPositive(balance)) {
            return "<error id=\"" + accountNum + "\">" + "Invalid balance format" + "</error>";
        }
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            // create new account
            Account account = new Account(Double.parseDouble(balance), accountNum);
            try {
                accountMapper.insertAccount(account);
            } catch (Exception e) {
                sqlSession.rollback();
                return  "<error id=\"" + accountNum + "\">" + "Account already exists" + "</error>";
            }
            sqlSession.commit();
            return "<created id=\"" + accountNum + "\"/>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<error id=\"" + accountNum + "\">" + "cannot open sql session" + "</error>";
    }
}
