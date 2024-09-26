package curatedchallenges.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import curatedchallenges.CuratedChallenges;

import java.lang.reflect.Field;

import static curatedchallenges.CuratedChallenges.makeID;

public class VictoryScreenPatches {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("VictoryScreenText"));

    // This will store the challenge-specific banner text
    public static String challengeBannerText = null;

    @SpirePatch(clz = DynamicBanner.class, method = "appear", paramtypez = {float.class, String.class})
    @SpirePatch(clz = DynamicBanner.class, method = "appearInstantly", paramtypez = {float.class, String.class})
    public static class DynamicBannerPatch {
        @SpirePostfixPatch
        public static void Postfix(DynamicBanner __instance, float y, String label) {
            if (label.equals(uiStrings.TEXT[0])) {
                challengeBannerText = label;
            }
        }
    }

    @SpirePatch(
            clz = VictoryScreen.class,
            method = "reopen",
            paramtypez = {
                    boolean.class
            }
    )
    public static class ReopenPatch {
        @SpirePostfixPatch
        public static void Postfix(VictoryScreen __instance, boolean fromVictoryUnlock) {
            if (challengeBannerText != null) {
                AbstractDungeon.dynamicBanner.appearInstantly(challengeBannerText);
            }
        }
    }
}
