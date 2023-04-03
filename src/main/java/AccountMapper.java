import org.apache.ibatis.annotations.*;

public interface AccountMapper {

    Account getAccountById(String accountId);

    void insertAccount(Account account);

    void updateAccount(Account account);

    void deleteAccount(String accountId);

}
