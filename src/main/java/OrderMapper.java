import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    void insertOrder(Order order);

    Order findOrderById(int id);

    List<Order> findOrderByTransId(long transId);

    List<Order> findOrderBySymbolAndStatus(@Param("symbol") String symbol, @Param("status") Status status);

    void updateOrder(Order order);

    void deleteOrder(int id);
}