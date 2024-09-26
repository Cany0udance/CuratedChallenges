package curatedchallenges.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.DeathScreen;
import curatedchallenges.CuratedChallenges;
import javassist.CtBehavior;

import static curatedchallenges.CuratedChallenges.makeID;

public class DeathScreenPatches {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DeathScreenText"));

    // This will store the challenge ID at the time of death
    private static String lastChallengeId = null;

    @SpirePatch(clz = DeathScreen.class, method = "getDeathBannerText")
    public static class GetDeathBannerTextPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result) {
            lastChallengeId = CuratedChallenges.currentChallengeId;
            if (lastChallengeId != null) {
                return uiStrings.TEXT[0];
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "reopen",
            paramtypez = {
                    boolean.class
            }
    )
    public static class ReopenPatch {
        @SpirePostfixPatch
        public static void Postfix(DeathScreen __instance, boolean fromVictoryUnlock) {
            if (lastChallengeId != null && !__instance.isVictory) {
                AbstractDungeon.dynamicBanner.appearInstantly(uiStrings.TEXT[0]);
            }
        }
    }
}