import org.apache.ibatis.annotations.*;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public interface AccountMapper {

    Account getAccountById(int accountId);

    void insertAccount(Account account);

    int updateAccount(Account account);

    void deleteAccount(String accountId);

    Account getAccountByNum(String accountNum);

}
