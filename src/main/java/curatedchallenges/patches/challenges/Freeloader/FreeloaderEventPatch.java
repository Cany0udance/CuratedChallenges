package curatedchallenges.patches.challenges.Freeloader;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.EventRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Freeloader;
import javassist.CtBehavior;

@SpirePatch(
        clz = EventRoom.class,
        method = "onPlayerEntry"
)
public class FreeloaderEventPatch {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"eventRngDuplicate"}
    )
    public static SpireReturn<Void> Insert(EventRoom __instance, Random eventRngDuplicate) {
        if (Freeloader.ID.equals(CuratedChallenges.currentChallengeId)) {
            float randomRoll = eventRngDuplicate.random(100.0F);

            if (randomRoll < 60.0F && AbstractDungeon.player.relics.size() >= 3) {
                __instance.event = new Nloth();
                __instance.event.onEnterRoom();
                AbstractDungeon.overlayMenu.proceedButton.hide();
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "generateEvent");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}