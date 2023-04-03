import org.apache.ibatis.annotations.*;

import java.util.List;

public interface AccountMapper {

    Account getAccountById(String accountId);

    void insertAccount(Account account);

    void updateAccount(Account account);

    void deleteAccount(String accountId);

    List<Account> getAccountByNum(String accountNum);

}
