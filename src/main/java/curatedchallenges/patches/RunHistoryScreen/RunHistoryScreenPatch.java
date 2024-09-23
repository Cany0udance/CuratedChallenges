
package curatedchallenges.patches.RunHistoryScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.runHistory.ModIcons;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.stats.RunData;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeInfo;
import curatedchallenges.util.ChallengeRegistry;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(clz = RunHistoryScreen.class, method = "renderRunHistoryScreen")
public class RunHistoryScreenPatch {
    @SpireInsertPatch(
            rloc = 1261,
            localvars = {"scoreText", "header1x", "yOffset", "TOP_POSITION"}
    )
    public static void Insert(RunHistoryScreen __instance, SpriteBatch sb, String scoreText, float header1x, float yOffset, float TOP_POSITION) {
        RunData viewedRun = (RunData) ReflectionHacks.getPrivate(__instance, RunHistoryScreen.class, "viewedRun");
        if (viewedRun != null) {
            ChallengeInfo challengeInfo = (ChallengeInfo) ReflectionHacks.getPrivate(viewedRun, RunData.class, ChallengeInfo.SAVE_KEY);
            if (challengeInfo != null && challengeInfo.challengeId != null) {
                ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeInfo.challengeId);
                if (challenge != null) {
                    String challengeText = " (" + challenge.getName() + ")";

                    float scoreYOffset = (float)ReflectionHacks.getPrivate(__instance, RunHistoryScreen.class, "scrollY") +
                            (float)ReflectionHacks.privateMethod(RunHistoryScreen.class, "screenPosY", float.class)
                                    .invoke(__instance, TOP_POSITION);
                    scoreYOffset -= (float)ReflectionHacks.privateMethod(RunHistoryScreen.class, "screenPosY", float.class)
                            .invoke(__instance, 90.0F);

                    float scoreWidth = FontHelper.getSmartWidth(FontHelper.buttonLabelFont, scoreText, 9999.0F, 36.0F * Settings.scale);

                    // To adjust the x position, modify the following line:
                    // Add or subtract a value from (header1x + scoreWidth) to move the text right or left
                    // For example, to move it 20 pixels to the right: (header1x + scoreWidth + 20.0F)
                    ReflectionHacks.privateMethod(RunHistoryScreen.class, "renderSubHeading",
                                    SpriteBatch.class, String.class, float.class, float.class, Color.class)
                            .invoke(__instance, sb, challengeText, (header1x + scoreWidth - 10.0F), scoreYOffset, Settings.GOLD_COLOR);
                }
            }
        }
    }
}