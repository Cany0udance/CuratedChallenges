package curatedchallenges.patches.challenges.FlyingRobot;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.WingBoots;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Defect.FlyingRobot;

public class FlyingRobotPatch {
    @SpirePatch(clz = WingBoots.class, method = SpirePatch.CONSTRUCTOR)
    public static class WingBootsConstructorPatch {
        public static void Postfix(WingBoots __instance) {
            if (FlyingRobot.ID.equals(CuratedChallenges.currentChallengeId)) {
                __instance.counter = 2;
            }
        }
    }
}