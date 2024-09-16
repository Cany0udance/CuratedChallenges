package curatedchallenges.patches.challenges.Avarice;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Avarice;
import javassist.CtBehavior;

@SpirePatch(
        clz = EventHelper.class,
        method = "roll",
        paramtypez = {
                Random.class
        }
)
public class NoShopsInEventsPatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(Random eventRng, @ByRef float[] ___SHOP_CHANCE) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId)) {
            ___SHOP_CHANCE[0] = 0f;
        }
    }

    @SpirePostfixPatch
    public static EventHelper.RoomResult Postfix(EventHelper.RoomResult __result, Random eventRng) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId) && __result == EventHelper.RoomResult.SHOP) {
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