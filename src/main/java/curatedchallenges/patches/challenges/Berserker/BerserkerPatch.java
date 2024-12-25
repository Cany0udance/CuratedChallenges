
/*

package curatedchallenges.patches.challenges.Berserker;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Berserker;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = "update"
)
public class BerserkerPatch {
    private static final Logger logger = LogManager.getLogger(BerserkerPatch.class.getName());

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(
                    ApplyPowerAction.class, "powerToApply");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"powerToApply", "target"}
    )
    public static void Insert(ApplyPowerAction __instance, AbstractPower powerToApply, AbstractCreature target) {
        logger.info("BerserkerPatch triggered");

        if (powerToApply == null) {
            logger.info("powerToApply is null");
            return;
        }

        if (target == null || target.hasPower("Artifact")) {
            logger.info("Target is null or has Artifact");
            return;
        }

        logger.info("Power ID: " + powerToApply.ID);

        if (CuratedChallenges.currentChallengeId == null) {
            logger.info("No challenge active");
            return;
        }

        logger.info("Current challenge: " + CuratedChallenges.currentChallengeId);

        if (Berserker.ID.equals(CuratedChallenges.currentChallengeId) &&
                VulnerablePower.POWER_ID.equals(powerToApply.ID)) {

            logger.info("Applying Strength to player");
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(
                            AbstractDungeon.player,
                            AbstractDungeon.player,
                            new StrengthPower(AbstractDungeon.player, 1),
                            1
                    )
            );
        }
    }
}

 */