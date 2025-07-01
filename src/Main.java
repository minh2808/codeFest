import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.Element;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.Hero;
import jsclub.codefest.sdk.model.npcs.Ally;
import jsclub.codefest.sdk.model.npcs.Enemy;
import jsclub.codefest.sdk.model.obstacles.Obstacle;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.weapon.Weapon;
import util.TargetSearcher;


import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Main {
    private static final String SERVER_URL = "https://cf25-server.jsclub.dev";
    private static final String GAME_ID = "107152";
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

    private int countChest = 0;

    private int waitTime = 0 ;
    private int stuckCounter = 0 ;

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
                return;
            }

            // Danh sach vat can tranh
            List<Node> nodesToAvoid = AvoidNodes.getNodesToAvoid(gameMap);


            // Xử lí khi bị kẹt
            handleStuckDetection(player);
            if (stuckCounter >  4 ) {
                System.out.println("Đã bị kẹt và giải thoát");
                hero.move(getRandomDirection());
                stuckCounter = 0;
                return;
            }


            // tìm đồng minh va tri thuong
            if (player.getHealth() <= 20 ) {
                if (hero.getInventory().getListHealingItem() != null) {
                    System.out.println("Đã trị thương bằng đồ ");
                    hero.useItem(hero.getInventory().getListHealingItem().get(0).getId());
                    return ;
                }
                else {
                    System.out.println("Đi tới đồng minh");
                    if (!hero.getGameMap().getListAllies().isEmpty()) {
                        TargetSearcher<Ally> searcherToAlly = new TargetSearcher<>(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getListAllies());
                        String PathToAlly = searcherToAlly.findPathToNearest();
                        if (PathToAlly != null) {
                            hero.move(PathToAlly);
                            return;
                        }
                    }
                    else {
                        System.out.println("Đã đi nhặt đồ ");
                        lootItem(nodesToAvoid , player );
                    }
                }
            }

//            if ( hero.getGameMap().getAllGun().isEmpty()) {
//                if (hero.getInventory().getMelee() != null) {
//                    Player nearestPlayer = Combat.getNearestPlayer(hero.getGameMap(), player);
//                    String pathToEnemy = Combat.findPathToOtherPlayer(hero.getGameMap(), nodesToAvoid, player, nearestPlayer);
//                    if (pathToEnemy != null ) {
//                        if (pathToEnemy.length() == 1 ) {
//                            hero.attack(pathToEnemy);
//                        }
//                        else {
//                            hero.move(pathToEnemy);
//                        }
//                    }
//                }
//            }


//
//            // tim shotgun
//            if ( hero.getInventory().getGun() == null ) {
//                UseShotGun searcherToShotGun = new UseShotGun(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllGun());
//                String pathToShortGun = searcherToShotGun.findPathToNearest();
//                if (pathToShortGun != null) {
//                    if (pathToShortGun.isEmpty()) {
//                        hero.pickupItem();
//                        return ;
//                    } else {
//                        hero.move(pathToShortGun);
//                    }
//
//                }
//            }


//            // nhặt nếu có đồ ở dưới chân
//            TargetSearcher <Weapon> searcher = new TargetSearcher<>(hero.getGameMap() , nodesToAvoid , player , hero.getGameMap().getListWeapons()) ;
//            String pathToItem = searcher.findPathToNearest() ;
//            if (pathToItem.isEmpty()) {
//                hero.pickupItem();
//                return ;
//            }




            // Di tim sung
            if (hero.getInventory().getGun() == null) {
                String pathToGun = UseGun.handleSearchForGun(gameMap, player, nodesToAvoid);
                if (pathToGun != null) {
                    if (pathToGun.isEmpty()) {
                        System.out.println("DA NHAT SUNG");
                        hero.pickupItem();
                        return ;
                    } else {
                        hero.move(pathToGun);

                    }
                }



            }


//
//            // tim chest va dap chest
//
//
//            String pathToChess = FindChest.handleSearchForChest(hero.getGameMap(), player, nodesToAvoid);
//            if (pathToChess != null) {
//                if (pathToChess.length() == 1) {
//                        System.out.println("Da pha ruong");
//                        hero.attack(pathToChess.substring(0, 1));
//                        return ;
//                } else {
//                    hero.move(pathToChess);
//
//                }
//
//            }

            // tim trang bi
            if (hero.getInventory().getArmor() == null) {
                System.out.println("Đã nhặt armor");
                if ( hero.getGameMap().getListHealingItems() != null ) {
                    collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getListArmors());
                }

            }

            // tim do tri thuong
            if ( hero.getInventory().getListHealingItem() == null ) {
                System.out.println("Đã nhặt healing item");
                if ( hero.getGameMap().getListHealingItems() != null ) {
                    collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getListHealingItems());
                }

            }

            // tim vu khi dac biet
            if ( hero.getInventory().getSpecial() == null ) {
                System.out.println("Đã nhặt special");
                if (hero.getGameMap().getAllSpecial() != null ) {
                    collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllSpecial());
                }
            }

            // tim vu khi dang nem
            if (hero.getInventory().getThrowable() == null) {
                System.out.println("Đã nhặt vũ khí dạng ném ");
                if (hero.getGameMap().getAllThrowable() != null ) {
                    collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllThrowable());
                }
            }

            // tim vu khi can chien
            if ( hero.getInventory().getMelee() == null ) {
                System.out.println(" Đã nhặt vũ khí cận chiến");
                if (hero.getGameMap().getAllMelee() != null ) {
                    collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllMelee());
                }

            }


            //tim nguoi va combat
            if (hero.getInventory().getGun() != null) {
                Player nearestPlayer = Combat.getNearestPlayer(hero.getGameMap(), player);
                if ( nearestPlayer != null ) {
                    String pathToEnemy = Combat.findPathToOtherPlayer(hero.getGameMap(), nodesToAvoid, player, nearestPlayer);
                    if (pathToEnemy != null) {
                        boolean sameRowOrCol = player.getX() == nearestPlayer.getX() || player.getY() == nearestPlayer.getY();
                        if (pathToEnemy.length() <= hero.getInventory().getGun().getRange() && sameRowOrCol) {
                            System.out.println("Enemy in range. Shooting!");
                            hero.shoot(pathToEnemy.substring(pathToEnemy.length() - 1));
                            return;
                        } else {
                            System.out.println("Moving closer to enemy: " + pathToEnemy);
                            hero.move(pathToEnemy);

                        }

                    }
                }
                else {
                    lootItem(nodesToAvoid , player );
                }

            }











        } catch (Exception e) {
            System.err.println("Critical error in call method: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public <T extends Node> void collectItem ( GameMap gameMap, List<Node> nodesToAvoid, Player player, List<T> targets ) throws IOException {
            TargetSearcher<T> searcher = new TargetSearcher<>(gameMap, nodesToAvoid, player, targets) ;
            T target = searcher.findNearestTarget() ;
            String pathToItem = searcher.findPathToNearest();
            if (pathToItem != null) {
                if (pathToItem.isEmpty()) {
                    System.out.println("DA NHAT SUNG");

                    hero.pickupItem();
                    return ;
                } else {
                    hero.move(pathToItem);
                }
            }

    }

    // Check xem co bi ket khong
    private String getRandomDirection() {
        String[] directions = {"u", "d", "l", "r"};
        return directions[new Random().nextInt(directions.length)];
    }

    private void handleStuckDetection(Player player ) {
        StuckHandler stuckHandler = new StuckHandler(hero.getGameMap()) ;
        if (stuckHandler.isStuck(player)) {
            stuckCounter++;
            System.out.println("Detected stuck: " + stuckCounter + "/" + 4 );
        } else {
            stuckCounter = 0;
        }
        stuckHandler.updateLastPosition(player);
    }

    private void lootItem (List<Node> nodesToAvoid , Player player ) throws IOException {
        // tim trang bi

        if (hero.getInventory().getArmor() == null) {
            System.out.println("Đã nhặt armor");
            if ( hero.getGameMap().getListHealingItems() != null ) {
                collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getListArmors());
            }

        }

        // tim do tri thuong
        if ( hero.getInventory().getListHealingItem() == null ) {
            System.out.println("Đã nhặt healing item");
            if ( hero.getGameMap().getListHealingItems() != null ) {
                collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getListHealingItems());
            }

        }

        // tim vu khi dac biet
        if ( hero.getInventory().getSpecial() == null ) {
            System.out.println("Đã nhặt special");
            if (hero.getGameMap().getAllSpecial() != null ) {
                collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllSpecial());
            }
        }

        // tim vu khi dang nem
        if (hero.getInventory().getThrowable() == null) {
            System.out.println("Đã nhặt vũ khí dạng ném ");
            if (hero.getGameMap().getAllThrowable() != null ) {
                collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllThrowable());
            }
        }

        // tim vu khi can chien
        if ( hero.getInventory().getMelee() == null ) {
            System.out.println(" Đã nhặt vũ khí cận chiến");
            if (hero.getGameMap().getAllMelee() != null ) {
                collectItem(hero.getGameMap(), nodesToAvoid, player, hero.getGameMap().getAllMelee());
            }

        }

    }




}