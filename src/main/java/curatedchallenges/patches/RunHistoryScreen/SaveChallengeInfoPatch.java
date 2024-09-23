package curatedchallenges.patches.RunHistoryScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeInfo;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "initializeRelicList"
)
public class SaveChallengeInfoPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractDungeon __instance) {

        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeInfo.challengeInfo = new ChallengeInfo();
            ChallengeInfo.challengeInfo.challengeId = CuratedChallenges.currentChallengeId;
            ChallengeInfo.challengeInfo.ascensionLevel = AbstractDungeon.ascensionLevel;
        }
    }
}