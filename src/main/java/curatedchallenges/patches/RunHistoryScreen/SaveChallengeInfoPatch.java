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
        BaseMod.logger.info("SaveChallengeInfoPatch: Postfix called for initializeRelicList");
        BaseMod.logger.info("SaveChallengeInfoPatch: Current challenge ID = " + CuratedChallenges.currentChallengeId);

        if (CuratedChallenges.currentChallengeId != null) {
            BaseMod.logger.info("SaveChallengeInfoPatch: Saving challenge info");
            ChallengeInfo.challengeInfo = new ChallengeInfo();
            ChallengeInfo.challengeInfo.challengeId = CuratedChallenges.currentChallengeId;
            ChallengeInfo.challengeInfo.ascensionLevel = AbstractDungeon.ascensionLevel;
            BaseMod.logger.info("SaveChallengeInfoPatch: Saved challenge ID = " + ChallengeInfo.challengeInfo.challengeId);
            BaseMod.logger.info("SaveChallengeInfoPatch: Saved ascension level = " + ChallengeInfo.challengeInfo.ascensionLevel);
        } else {
            BaseMod.logger.info("SaveChallengeInfoPatch: Not saving challenge info (no current challenge)");
        }
    }
}