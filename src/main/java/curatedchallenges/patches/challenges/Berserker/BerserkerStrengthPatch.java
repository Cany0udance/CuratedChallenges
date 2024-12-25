
/*

package curatedchallenges.patches.challenges.Berserker;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Berserker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = StrengthPower.class,
        method = "stackPower"
)
public class BerserkerStrengthPatch {

    @SpireInsertPatch(
            rloc = 0,
            localvars = {"stackAmount"}
    )
    public static void Insert(StrengthPower __instance, int stackAmount) {
        BaseMod.logger.info("BerserkerStrengthPatch triggered with stackAmount: " + stackAmount);

        if (Berserker.ID.equals(CuratedChallenges.currentChallengeId) &&
                stackAmount > 0 &&
                __instance.owner != null &&
                __instance.owner.isPlayer) {

            BaseMod.logger.info("Reducing player energy by 1");
            AbstractDungeon.player.loseEnergy(1);
        }
    }
}

 */