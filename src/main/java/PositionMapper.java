import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PositionMapper {

    List<Position> getPositionsByAccountId(int accountId);

    Position getPositionById(int positionId);

    void insertPosition(Position position);

    void updatePosition(Position position);

    void deletePositionById(int positionId);
}
