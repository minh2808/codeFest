import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;

import java.util.List;

public class StuckHandler {

    private GameMap gameMap;
    private Node lastPosition = new Node(-1, -1);

    public StuckHandler(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public boolean isStuck(Player player) {
        return player.x == lastPosition.x && player.y == lastPosition.y;
    }

    public void updateLastPosition(Player player) {
        lastPosition.setPosition(player.x, player.y);
    }

    public String findDodgeDirection(List<Node> nodesToAvoid, Player player, Player nearestEnemy) {
        String[] directions = {"u", "d", "l", "r"};
        Node currentPlayerPos = new Node(player.x, player.y);

        for (String dir : directions) {
            Node nextPos = getNextPosition(currentPlayerPos, dir);
            if (isSafeDodgePosition(nodesToAvoid, nextPos, nearestEnemy)) {
                return dir;
            }
        }
        return null;
    }

    private boolean isSafeDodgePosition(List<Node> nodesToAvoid, Node nextPos, Player nearestEnemy) {
        if (!PathUtils.checkInsideSafeArea(nextPos, gameMap.getSafeZone(), gameMap.getMapSize()))
            return false;
        if (nodesToAvoid.contains(nextPos)) return false;

        double newDistance = PathUtils.distance(nextPos, nearestEnemy);
        double currentDistance = PathUtils.distance(gameMap.getCurrentPlayer(), nearestEnemy);

        return newDistance > currentDistance;
    }

    private Node getNextPosition(Node currentPos, String direction) {
        int newX = currentPos.x;
        int newY = currentPos.y;

        switch (direction) {
            case "u":
                newY--;
                break;
            case "d":
                newY++;
                break;
            case "l":
                newX--;
                break;
            case "r":
                newX++;
                break;
        }
        return new Node(newX, newY);
    }

}
