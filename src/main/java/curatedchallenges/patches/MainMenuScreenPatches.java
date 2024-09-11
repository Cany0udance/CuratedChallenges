package curatedchallenges.patches;

import curatedchallenges.screens.ChallengesScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

@SpirePatch(
        clz = MainMenuScreen.class,
        method = SpirePatch.CLASS
)
public class MainMenuScreenPatches {
    @SpireEnum
    public static MainMenuScreen.CurScreen CURATED_CHALLENGES;
    public static ChallengesScreen curatedChallengesScreen;

    @SpirePatch(
            clz = MainMenuScreen.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    boolean.class
            }
    )
    public static class AddCuratedChallengesScreenPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance, boolean playBgm) {
            curatedChallengesScreen = new ChallengesScreen();
        }
    }

    @SpirePatch(
            clz = MainMenuScreen.class,
            method = "update"
    )
    public static class UpdateCuratedChallengesScreenPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance) {
            if (__instance.screen == CURATED_CHALLENGES) {
                curatedChallengesScreen.update();
            }
        }
    }

    @SpirePatch(
            clz = MainMenuScreen.class,
            method = "render"
    )
    public static class RenderCuratedChallengesScreenPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance, SpriteBatch sb) {
            if (__instance.screen == CURATED_CHALLENGES) {
                curatedChallengesScreen.render(sb);
            }
        }
    }

    @SpirePatch(
            clz = MainMenuScreen.class,
            method = "update"
    )
    public static class OpenCuratedChallengesScreenPatch {
        @SpirePostfixPatch
        public static void Postfix(MainMenuScreen __instance) {
            if (__instance.screen == CURATED_CHALLENGES && !curatedChallengesScreen.isScreenOpened) {
                curatedChallengesScreen.open();
                curatedChallengesScreen.isScreenOpened = true;
            } else if (__instance.screen != CURATED_CHALLENGES) {
                curatedChallengesScreen.isScreenOpened = false;
            }
        }
    }
}