package curatedchallenges.patches.challenges.FlyingRobot;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.RestRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Defect.FlyingRobot;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.megacrit.cardcrawl.map.MapGenerator.generateDungeon;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import javassist.CtBehavior;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import javassist.CtBehavior;

import java.lang.reflect.Method;
import java.util.ArrayList;

@SpirePatch(
        clz = MapGenerator.class,
        method = "generateDungeon"
)
public class TwoUnconnectedPathsPatch {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"map"}
    )
    public static void Insert(int height, int width, int pathDensity, Random rng, @ByRef ArrayList<ArrayList<MapRoomNode>>[] map) {
        if (FlyingRobot.ID.equals(CuratedChallenges.currentChallengeId)) {
            ArrayList<ArrayList<MapRoomNode>> newMap = createTwoUnconnectedPaths(height, width, rng);
            map[0] = newMap;
        }
    }

    private static ArrayList<ArrayList<MapRoomNode>> createTwoUnconnectedPaths(int height, int width, Random rng) {
        ArrayList<ArrayList<MapRoomNode>> map = createNodesReflection(height, width);

        int midPoint = width / 2;
        // Create two separate paths
        int leftEndX = createPath(map, 0, midPoint - 1, rng);
        int rightEndX = createPath(map, midPoint, width - 1, rng);

        // Add final row (rest sites)
        addFinalRow(map, leftEndX, rightEndX);

        return map;
    }

    private static ArrayList<ArrayList<MapRoomNode>> createNodesReflection(int height, int width) {
        try {
            Method createNodesMethod = MapGenerator.class.getDeclaredMethod("createNodes", int.class, int.class);
            createNodesMethod.setAccessible(true);
            return (ArrayList<ArrayList<MapRoomNode>>) createNodesMethod.invoke(null, height, width);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list if reflection fails
        }
    }

    private static int createPath(ArrayList<ArrayList<MapRoomNode>> map, int startX, int endX, Random rng) {
        int currentX = startX;
        for (int y = 0; y < map.size() - 2; y++) {  // Stop one row before the last
            MapRoomNode currentNode = map.get(y).get(currentX);
            int nextX = getNextX(currentX, startX, endX, rng);
            MapRoomNode nextNode = map.get(y + 1).get(nextX);

            currentNode.addEdge(new MapEdge(currentX, y, currentNode.offsetX, currentNode.offsetY,
                    nextX, y + 1, nextNode.offsetX, nextNode.offsetY, false));
            nextNode.addParent(currentNode);

            currentX = nextX;
        }
        return currentX;
    }

    private static void addFinalRow(ArrayList<ArrayList<MapRoomNode>> map, int leftEndX, int rightEndX) {
        int secondLastRow = map.size() - 2;
        int lastRow = map.size() - 1;
        int bossRow = lastRow + 1;  // The boss is effectively one row beyond the last

        // Add edges to rest sites
        MapRoomNode leftNode = map.get(secondLastRow).get(leftEndX);
        MapRoomNode rightNode = map.get(secondLastRow).get(rightEndX);
        MapRoomNode leftRest = map.get(lastRow).get(0);
        MapRoomNode rightRest = map.get(lastRow).get(map.get(lastRow).size() - 1);

        leftNode.addEdge(new MapEdge(leftEndX, secondLastRow, leftNode.offsetX, leftNode.offsetY,
                0, lastRow, leftRest.offsetX, leftRest.offsetY, false));
        rightNode.addEdge(new MapEdge(rightEndX, secondLastRow, rightNode.offsetX, rightNode.offsetY,
                map.get(lastRow).size() - 1, lastRow, rightRest.offsetX, rightRest.offsetY, false));

        leftRest.addParent(leftNode);
        rightRest.addParent(rightNode);

        // Add edges from rest sites to boss
        int bossX = map.get(lastRow).size() / 2;
        MapRoomNode bossNode = map.get(lastRow).get(bossX);

        // Adjust the boss node's y-coordinate
        float bossOffsetY = bossNode.offsetY + Settings.MAP_DST_Y;

        leftRest.addEdge(new MapEdge(0, lastRow, leftRest.offsetX, leftRest.offsetY,
                bossX, bossRow, bossNode.offsetX, bossOffsetY, true));
        rightRest.addEdge(new MapEdge(map.get(lastRow).size() - 1, lastRow, rightRest.offsetX, rightRest.offsetY,
                bossX, bossRow, bossNode.offsetX, bossOffsetY, true));

        // Ensure the boss node has correct parents
        bossNode.addParent(leftRest);
        bossNode.addParent(rightRest);
    }

    private static int getNextX(int currentX, int startX, int endX, Random rng) {
        if (currentX == startX) return rng.random(startX, startX + 1);
        if (currentX == endX) return rng.random(endX - 1, endX);
        return rng.random(currentX - 1, currentX + 1);
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MapGenerator.class, "filterRedundantEdgesFromRow");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}