package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Avarice;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import static basemod.BaseMod.logger;

@SpirePatch(
        clz = EventHelper.class,
        method = "roll",
        paramtypez = {
                Random.class
        }
)
public class EventHelperRollPatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(Random eventRng, @ByRef float[] ___SHOP_CHANCE) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId)) {
            ___SHOP_CHANCE[0] = 0f;
            logger.info("Avarice Challenge: Set SHOP_CHANCE to 0");
        }
    }

    @SpirePostfixPatch
    public static EventHelper.RoomResult Postfix(EventHelper.RoomResult __result, Random eventRng) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId) && __result == EventHelper.RoomResult.SHOP) {
            logger.info("Avarice Challenge: Changing SHOP result to EVENT");
            return EventHelper.RoomResult.EVENT;
        }
        return __result;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(EventHelper.class, "SHOP_CHANCE");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}