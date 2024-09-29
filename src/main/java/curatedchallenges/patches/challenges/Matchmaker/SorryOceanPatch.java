package curatedchallenges.patches.challenges.Matchmaker;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Matchmaker;

@SpirePatch(
        optional = true,
        cls = "oceanmod.patches.visiblecardrewards.ReplaceCardRewards",
        method = SpirePatch.CLASS
)
public class SorryOceanPatch {
    @SpirePatch(
            optional = true,
            cls = "oceanmod.patches.visiblecardrewards.ReplaceCardRewards",
            method = "Postfix"
    )
    public static class PostfixPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> conditionalDisable(CombatRewardScreen __instance) {
            if (Loader.isModLoaded("oceanmod") && Matchmaker.ID.equals(CuratedChallenges.currentChallengeId)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            optional = true,
            cls = "oceanmod.patches.visiblecardrewards.ReplaceCardRewards",
            method = "replaceReward"
    )
    public static class ReplaceRewardPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> conditionalDisable(RewardItem reward) {
            if (Loader.isModLoaded("oceanmod") && Matchmaker.ID.equals(CuratedChallenges.currentChallengeId)) {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }
}