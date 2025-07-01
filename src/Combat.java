import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;

import java.util.List;

public class Combat {

    public static String findPathToOtherPlayer(GameMap gameMap , List<Node> nodesToAvoid, Player player, Player nearestPlayer) {
        return PathUtils.getShortestPath(gameMap, nodesToAvoid, player, nearestPlayer, false);
    }

    public static Player getNearestPlayer(GameMap gameMap, Player player) {
        List<Player> otherPlayers = gameMap.getOtherPlayerInfo();
        Player target = null;
        int minDistance = 99999;
        for (Player otherPlayer : otherPlayers) {
            if (PathUtils.checkInsideSafeArea(otherPlayer , gameMap.getSafeZone() , gameMap.getMapSize())) {
                if (otherPlayer.getHealth() > 0) {
                    int distance = PathUtils.distance(player, otherPlayer);
                    if (distance < minDistance) {
                        minDistance = distance;
                        target = otherPlayer;
                    }
                }
            }
        }
        return target;
    }

}
