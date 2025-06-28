import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;

import java.util.ArrayList;
import java.util.List;

public class AvoidNodes {

    public static List<Node> getNodesToAvoid(GameMap gameMap) {
        List<Node> nodes = new ArrayList<>(gameMap.getListEnemies());

        nodes.removeAll(gameMap.getObstaclesByTag("CAN_GO_THROUGH"));
        nodes.addAll(gameMap.getOtherPlayerInfo());
        return nodes;
    }
}
