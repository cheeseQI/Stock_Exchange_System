import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PositionMapper {
    @Delete("DELETE FROM position")
    void deleteAll();

    List<Position> getPositionsByAccountId(int accountId);

    Position getPositionById(int positionId);

    void insertPosition(Position position);

    void insertOrUpdatePosition(Position position);

    int updatePosition(Position position);

    void deletePositionById(int positionId);

    List<Position> getPositionsByAccountNum(String accountNum);

    List<Position> getPositionByAccountNumForUpdate(String accountNum);
}
