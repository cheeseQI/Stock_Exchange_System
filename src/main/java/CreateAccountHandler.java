import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class CreateAccountHandler extends ActionsHandler {
    private String account_id;
    private String balance;

    public CreateAccountHandler(String account_id, String balance) {
        this.account_id = account_id;
        this.balance = balance;
    }

    @Override
    public String executeAction() {
        String res;
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            // find if already exist
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            Account account = accountMapper.getAccountById(account_id);
            if (account != null) {
                res = "<error id=\"ACCOUNT_ID\">Msg</error>";
                return;
            }
            // create new account
            account = new Account();
            account.setAccount_id(account_id);
            account.setBalance(balance);
            sqlSession.insert("account.createAccount", account);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
