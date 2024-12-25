package curatedchallenges.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.beyond.Darkling;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

@SpirePatch(
        clz = Darkling.class,
        method = "damage",
        paramtypez = {DamageInfo.class}
)
public class DarklingDamagePatch {
    @SpireInsertPatch(
            rloc = 1, // Right at the start of the half-dead check
            localvars = {"info"}
    )
    public static void Insert(Darkling __instance, DamageInfo info) {
        BaseMod.logger.info("DarklingDamagePatch triggered");
        BaseMod.logger.info("Current Health: " + __instance.currentHealth);
        BaseMod.logger.info("Half Dead: " + __instance.halfDead);
        BaseMod.logger.info("Challenge ID: " + CuratedChallenges.currentChallengeId);

        if (__instance.currentHealth <= 0 && !__instance.halfDead && CuratedChallenges.currentChallengeId != null) {
            BaseMod.logger.info("Executing onMonsterDeath for challenge");
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
            if (challenge != null) {
                challenge.onMonsterDeath(AbstractDungeon.player, __instance);
            }
        }
    }
}