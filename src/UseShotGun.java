import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.weapon.Weapon;
import util.TargetSearcher;

import java.util.List;

public class UseShotGun extends TargetSearcher<Weapon> {

    private final GameMap gameMap;
    private final Player player;
    private final List<Weapon> targets;

    public UseShotGun(GameMap gameMap, List<Node> nodesToAvoid, Player player , List <Weapon> targets) {
        super(gameMap , nodesToAvoid , player , targets );
        this.targets = targets;
        this.player = player;
        this.gameMap = gameMap;
    }

    @Override
    public Weapon findNearestTarget() {
        Weapon nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Weapon target : targets) {
            if (PathUtils.checkInsideSafeArea(target , gameMap.getSafeZone() , gameMap.getMapSize())) {
                if (target.getId().equals("SHOTGUN")) {
                    double dist = PathUtils.distance(player, target);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = target;
                    }
                }
            }
        }
        return nearest;
    }

}
