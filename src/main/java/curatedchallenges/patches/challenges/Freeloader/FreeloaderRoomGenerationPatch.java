package curatedchallenges.patches.challenges.Freeloader;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Freeloader;
import javassist.CtBehavior;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;


@SpirePatch(
        clz = AbstractDungeon.class,
        method = "generateMap"
)
public class FreeloaderRoomGenerationPatch {
    @SpirePostfixPatch
    public static void Postfix() {
        if (Freeloader.ID.equals(CuratedChallenges.currentChallengeId)) {
            ArrayList<MapRoomNode> monsterRooms = new ArrayList<>();
            int totalMonsterRooms = 0;

            // Collect all monster rooms (excluding elites and first floor)
            for (int y = 1; y < AbstractDungeon.map.size() - 1; y++) {
                for (MapRoomNode node : AbstractDungeon.map.get(y)) {
                    if (node.room instanceof MonsterRoom && !(node.room instanceof MonsterRoomElite)) {
                        monsterRooms.add(node);
                        totalMonsterRooms++;
                    }
                }
            }

            int roomsToConvert = Math.round(totalMonsterRooms * 0.5f);

            // Shuffle the list of monster rooms
            Collections.shuffle(monsterRooms, new java.util.Random(AbstractDungeon.mapRng.randomLong()));

            // Convert the first 'roomsToConvert' rooms to event rooms
            for (int i = 0; i < roomsToConvert && i < monsterRooms.size(); i++) {
                MapRoomNode nodeToConvert = monsterRooms.get(i);
                nodeToConvert.room = new EventRoom();
            }
        }
    }
}