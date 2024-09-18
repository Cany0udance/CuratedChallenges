package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.screens.DeathScreen;
import curatedchallenges.CuratedChallenges;

@SpirePatch(clz = DeathScreen.class, method = "getDeathBannerText")
public class DeathScreenPatch {

    @SpirePostfixPatch
    public static String Postfix(String __result) {
        if (CuratedChallenges.currentChallengeId != null) {
            return "Challenge Failed!";
        }
        return __result;
    }
}

