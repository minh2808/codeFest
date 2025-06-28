import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.Element;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.Hero;
import jsclub.codefest.sdk.model.obstacles.Obstacle;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.weapon.Weapon;


import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Main {
    private static final String SERVER_URL = "https://cf25-server.jsclub.dev";
    private static final String GAME_ID = "192115";
    private static final String PLAYER_NAME = "minh";
    private static final String SECRET_KEY = "sk-hkssKiW7SrK3tK_RAGSTsw:lphzsTtrbEYS-tMqvzffA9E5N56hvHuZhYSD8BMuvJm-nyNSMzctxoqzpdr1EikPdtR4U-f8t87CSwI98mLdfg";
    

    public static void main(String[] args) throws IOException {
        Hero hero = new Hero(GAME_ID, PLAYER_NAME, SECRET_KEY);
        Emitter.Listener onMapUpdate = new MapUpdateListener(hero);

        hero.setOnMapUpdate(onMapUpdate);
        hero.start(SERVER_URL);
    }
}

class MapUpdateListener implements Emitter.Listener {
    private final Hero hero;
    

    public MapUpdateListener(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void call(Object... args) {
        try {
            if (args == null || args.length == 0) return;

            GameMap gameMap = hero.getGameMap();
            gameMap.updateOnUpdateMap(args[0]);
            Player player = gameMap.getCurrentPlayer();

            if (player == null || player.getHealth() == 0) {
                System.out.println("Player is dead or data is not available.");
                return ;
            }

            // Danh sach vat can tranh
            List<Node> nodesToAvoid = AvoidNodes.getNodesToAvoid(gameMap);

            // Di tim sung
            if (hero.getInventory().getGun() == null) {
                String pathToGun = UseGun.handleSearchForGun(gameMap, player, nodesToAvoid);
                if (pathToGun != null) {
                    if (pathToGun.isEmpty()) {
                        System.out.println("DA NHAT SUNG");
                        hero.pickupItem();
                    } else {
                        hero.move(pathToGun);
                    }
                }
                return ;
            }

//            // Di tim vu khi can chien
//            String pathToMelee = UseMelee.handleSearchForMelee(hero.getGameMap() , player , nodesToAvoid) ;
//            if (pathToMelee != null) {
//                if (pathToMelee.isEmpty()) {
//                    System.out.println("DA NHAT VU KHI CAN CHIEN");
//                    hero.pickupItem();
//                } else {
//                    hero.move(pathToMelee);
//                }
//            }
//            else {
//                System.out.println("Khong tim thay vu khi can chien");
//            }

            // tim chest va dap chest
            String pathToChess = FindChest.handleSearchForChest(hero.getGameMap() , player , nodesToAvoid) ;
            if (pathToChess != null) {
                if ( pathToChess.length() == 1 ){
                    System.out.println("Da pha ruong");
                    hero.attack(pathToChess.substring(0 , 1 ));
                }
                else {
                    hero.move(pathToChess);
                }
                return ;
            }



            //tim nguoi va combat
            Player nearestPlayer = Combat.getNearestPlayer(hero.getGameMap(), player);
            String pathToEnemy = Combat.findPathToOtherPlayer(hero.getGameMap(), nodesToAvoid, player, nearestPlayer);
            if (pathToEnemy != null && PathUtils.distance(player, nearestPlayer) <= hero.getInventory().getGun().getRange()) {
                System.out.println("Enemy in range. Shooting!");
                hero.shoot(pathToEnemy.substring(0, 1));
            } else if (pathToEnemy != null) {
                System.out.println("Moving closer to enemy: " + pathToEnemy);
                hero.move(pathToEnemy);
            }








        } catch (Exception e) {
            System.err.println("Critical error in call method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Start tim ruong
    private void  handleSearchForChest (GameMap gameMap, Player player, List<Node> nodesToAvoid) throws IOException {
        String pathToChess =  findPathToChest (gameMap, nodesToAvoid, player) ;

        Obstacle nearestChest = getNearestChest(gameMap, nodesToAvoid , player) ;
        Node myPos = player.getPosition();


        if (pathToChess != null) {
            if ( pathToChess.isEmpty()){
                hero.shoot(pathToChess);
            }
            else {
                hero.move(pathToChess);
            }
        }
    }

    private String findPathToChest (GameMap gameMap , List<Node> nodesToVoid , Player player ) {

        Obstacle nearestChest = getNearestChest(gameMap , nodesToVoid, player) ;
        if (nearestChest == null) return null;
        return PathUtils.getShortestPath(gameMap , nodesToVoid , player , nearestChest, false);

    }

    private Obstacle getNearestChest (GameMap gameMap , List<Node> nodesToVoid , Player player ) {
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