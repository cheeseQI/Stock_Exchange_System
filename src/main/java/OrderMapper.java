import java.util.List;

public interface OrderMapper {
    void insertOrder(Order order);

    Order findOrderById(int id);

    List<Order> findOrderByTransId(long transId);

    int updateOrder(Order order, Status status);

    void deleteOrder(int id);
}