package curatedchallenges.patches.WinConditions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;
import javassist.CtBehavior;

@SpirePatch(clz = TrueVictoryRoom.class, method = SpirePatch.CLASS)
public class TrueVictoryRoomPatch {

    public static SpireField<Boolean> isInChallenge = new SpireField<>(() -> false);

    @SpirePatch(clz = TrueVictoryRoom.class, method = SpirePatch.CONSTRUCTOR)
    public static class ConstructorPatch {
        @SpirePostfixPatch
        public static void Postfix(TrueVictoryRoom __instance) {
            isInChallenge.set(__instance, CuratedChallenges.currentChallengeId != null);
            if (isInChallenge.get(__instance)) {
                __instance.cutscene = null;
            }
        }
    }

    @SpirePatch(clz = TrueVictoryRoom.class, method = "onPlayerEntry")
    public static class OnPlayerEntryPatch {
        @SpirePostfixPatch
        public static void Postfix(TrueVictoryRoom __instance) {
            if (isInChallenge.get(__instance)) {
                AbstractDungeon.isScreenUp = false;
                GameCursor.hidden = false;
                AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
                ChallengeVictoryHandler.checkVictoryConditions(CuratedChallenges.currentChallengeId, false, false, true);
            }
        }
    }

    @SpirePatch(clz = TrueVictoryRoom.class, method = "update")
    public static class UpdatePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(TrueVictoryRoom __instance) {
            if (isInChallenge.get(__instance)) {
                __instance.phase = AbstractRoom.RoomPhase.COMPLETE;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TrueVictoryRoom.class, method = "render")
    @SpirePatch(clz = TrueVictoryRoom.class, method = "renderAboveTopPanel")
    public static class RenderPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(TrueVictoryRoom __instance, SpriteBatch sb) {
            if (isInChallenge.get(__instance)) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TrueVictoryRoom.class, method = "dispose")
    public static class DisposePatch {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(TrueVictoryRoom __instance) {
            if (isInChallenge.get(__instance)) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Cutscene.class, "dispose");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}