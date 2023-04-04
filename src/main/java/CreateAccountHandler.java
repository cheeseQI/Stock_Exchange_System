import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class CreateAccountHandler extends ActionsHandler {
    private String accountNum;
    private String balance;

    public CreateAccountHandler(String accountNum, String balance) {
        this.accountNum = accountNum;
        this.balance = balance;
    }

    //check格式是不是都是字母

    @Override
    public String executeAction() {
        String res;
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            // find if already exist
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            Account account = accountMapper.getAccountById(accountNum);
            if (account != null) {
                res = "<error id=\"ACCOUNT_ID\">Msg</error>";
                return null;
            }
            // create new account
            account = new Account();
            account.setAccountNum(accountNum);
            account.setBalance(Double.parseDouble(balance));
            sqlSession.insert("account.createAccount", account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
