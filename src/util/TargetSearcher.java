package util;

import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;

import java.util.List;

public class TargetSearcher<T extends Node> {

    private final GameMap gameMap;
    private final List<Node> nodesToAvoid;
    private final Player player;
    private final List<T> targets;

    public TargetSearcher(GameMap gameMap, List<Node> nodesToAvoid, Player player, List<T> targets) {
        this.gameMap = gameMap;
        this.nodesToAvoid = nodesToAvoid;
        this.player = player;
        this.targets = targets;
    }

    public  T findNearestTarget() {
        T nearest = null;
        double minDist = Double.MAX_VALUE;
        for (T target : targets) {
            if ( PathUtils.checkInsideSafeArea(target , gameMap.getSafeZone() , gameMap.getMapSize())) {
                double dist = PathUtils.distance(player, target);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = target;
                }
            }
        }
        return nearest;
    }

    public String findPathToNearest() {
        T nearest = findNearestTarget();
        if (nearest == null) return null;
        return PathUtils.getShortestPath(gameMap, nodesToAvoid, player, nearest, false);
    }

    public List<T> getTargets() {
        return targets;
    }
}

