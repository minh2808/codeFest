import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.util.List;

public class UseMelee {
    public static  Weapon getNearestMelee(GameMap gameMap, Player player) {
        List<Weapon> melees = gameMap.getAllMelee();  // Lấy danh sách melee trên map
        if (melees == null || melees.isEmpty()) return null;

        Weapon nearestMelee = null;
        double minDistance = Double.MAX_VALUE;

        for (Weapon melee : melees) {
            double distance = PathUtils.distance(player, melee);
            if (distance < minDistance) {
                minDistance = distance;
                nearestMelee = melee;
            }
        }
        return nearestMelee;
    }

    public static String findPathToMelee(GameMap gameMap, List<Node> nodesToAvoid, Player player) {
        Weapon nearestMelee = getNearestMelee(gameMap, player);
        if (nearestMelee == null) return null;
        return PathUtils.getShortestPath(gameMap, nodesToAvoid, player, nearestMelee, false);
    }

    public static String  handleSearchForMelee(GameMap gameMap, Player player, List<Node> nodesToAvoid) throws IOException {
        System.out.println("No melee weapon found. Searching for melee weapon.");
        String pathToMelee = findPathToMelee(gameMap, nodesToAvoid, player);

        return pathToMelee;

    }
    

}
