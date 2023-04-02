import org.apache.ibatis.annotations.*;

public interface AccountMapper {

    Account getAccountById(int accountId);

    void insertAccount(Account account);

    void updateAccount(Account account);

    void deleteAccount(int accountId);

}
