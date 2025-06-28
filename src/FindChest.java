import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.obstacles.Obstacle;
import jsclub.codefest.sdk.model.players.Player;

import java.io.IOException;
import java.util.List;

public class FindChest {
    // Start tim ruong
    public static String  handleSearchForChest (GameMap gameMap, Player player, List<Node> nodesToAvoid) throws IOException {
        String pathToChess =  findPathToChest (gameMap, nodesToAvoid, player) ;

        return pathToChess;
    }

    public static String findPathToChest (GameMap gameMap , List<Node> nodesToVoid , Player player ) {

        Obstacle nearestChest = getNearestChest(gameMap , nodesToVoid, player) ;
        if (nearestChest == null) return null;
        return PathUtils.getShortestPath(gameMap , nodesToVoid , player , nearestChest, false);

    }

    public static Obstacle getNearestChest (GameMap gameMap , List<Node> nodesToVoid , Player player ) {
        List <Obstacle> listOfChest = gameMap.getListChests() ;
        Obstacle nearestChest = null;
        double minDistance = Double.MAX_VALUE;

        for (Obstacle chest : listOfChest) {
            double distance = PathUtils.distance(player, chest);
            if (distance < minDistance) {
                minDistance = distance;
                nearestChest = chest;
            }
        }

        return nearestChest;
    }

}
