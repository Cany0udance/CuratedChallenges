package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.DeathScreen;
import curatedchallenges.CuratedChallenges;

import static curatedchallenges.CuratedChallenges.makeID;

@SpirePatch(clz = DeathScreen.class, method = "getDeathBannerText")
public class DeathScreenPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DeathScreenText"));

    @SpirePostfixPatch
    public static String Postfix(String __result) {
        if (CuratedChallenges.currentChallengeId != null) {
            return uiStrings.TEXT[0];
        }
        return __result;
    }
}

