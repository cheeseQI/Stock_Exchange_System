import org.apache.ibatis.annotations.*;

import java.util.List;

public interface AccountMapper {
    @Delete("DELETE FROM account")
    void deleteAll();

    Account getAccountById(int accountId);

    int insertAccount(Account account);

    int updateAccount(Account account);

    void deleteAccount(String accountId);

    Account getAccountByNum(String accountNum);

}
