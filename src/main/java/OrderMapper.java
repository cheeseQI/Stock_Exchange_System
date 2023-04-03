import java.util.List;

public interface OrderMapper {
    void insertOrder(Order order);

    Order findOrderById(int id);

    List<Order> findOrderByTransId(long transId);

    void updateOrder(Order order);

    void deleteOrder(int id);
}