import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    @Delete("DELETE FROM order_table")
    void deleteAll();

    void insertOrder(Order order);

    Order findOrderById(int id);

    List<Order> findOrderByTransId(long transId);

    List<Order> findOrderBySymbolAndStatus(@Param("symbol") String symbol, @Param("status") Status status);

    int updateOrder(Order order);

    void deleteOrder(int id);
}