package curatedchallenges.patches.RunHistoryScreen;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import curatedchallenges.util.ChallengeInfo;

@SpirePatch(clz = Metrics.class, method = "gatherAllData")
public class GatherAllDataPatch {
    @SpirePostfixPatch
    public static void Postfix(Metrics __instance, boolean death, boolean trueVictor, MonsterGroup monsters) {
        ReflectionHacks.privateMethod(Metrics.class, "addData", Object.class, Object.class)
                .invoke(__instance, ChallengeInfo.SAVE_KEY, ChallengeInfo.challengeInfo);
    }
}
