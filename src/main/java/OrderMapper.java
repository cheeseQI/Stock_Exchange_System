import java.util.List;

public interface OrderMapper {
    void insertOrder(Order order);

    Order findOrderById(int id);

    void updateOrder(Order order);

    void deleteOrder(int id);
}