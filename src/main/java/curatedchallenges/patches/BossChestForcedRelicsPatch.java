package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

import java.util.Collections;
import java.util.List;

@SpirePatch(
        clz = BossChest.class,
        method = SpirePatch.CONSTRUCTOR
)
public class BossChestForcedRelicsPatch {
    @SpirePostfixPatch
    public static void Postfix(BossChest __instance) {
        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
            if (challenge != null) {
                List<String> forcedRelics = Collections.emptyList();

                // Only check for Acts 1 and 2
                if (AbstractDungeon.actNum == 1) {
                    forcedRelics = challenge.getForcedBossRelicsAct1();
                } else if (AbstractDungeon.actNum == 2) {
                    forcedRelics = challenge.getForcedBossRelicsAct2();
                }

                // If we have forced relics for this act
                if (!forcedRelics.isEmpty()) {
                    // Limit to max 5 relics
                    int relicCount = Math.min(forcedRelics.size(), 5);

                    // Clear existing relics and add our forced ones
                    __instance.relics.clear();
                    for (int i = 0; i < relicCount; i++) {
                        String relicId = forcedRelics.get(i);
                        AbstractRelic relic = RelicLibrary.getRelic(relicId);
                        if (relic != null) {
                            __instance.relics.add(relic);
                        }
                    }

                    // If somehow we ended up with no valid relics, generate default ones
                    if (__instance.relics.isEmpty()) {
                        for (int i = 0; i < 3; i++) {
                            __instance.relics.add(AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS));
                        }
                    }
                }
            }
        }
    }
}