package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "die",
        paramtypez = {boolean.class}
)
public class MonsterDeathPatch {
    @SpireInsertPatch(
            rloc = 2,
            localvars = {"triggerRelics"}
    )
    public static void Insert(AbstractMonster __instance, boolean triggerRelics) {
        if (triggerRelics && CuratedChallenges.currentChallengeId != null) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
            if (challenge != null) {
                challenge.onMonsterDeath(AbstractDungeon.player, __instance);
            }
        }
    }
}