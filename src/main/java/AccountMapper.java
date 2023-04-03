import org.apache.ibatis.annotations.*;

import java.util.List;

public interface AccountMapper {

    Account getAccountById(int accountId);

    void insertAccount(Account account);

    void updateAccount(Account account);

    void deleteAccount(int accountId);

    List<Account> getAccountByNum(String accountNum);

}
