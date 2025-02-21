package curatedchallenges.patches.challenges.GoopLab;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.SlimeBoss.GoopLab;
import javassist.CtBehavior;
import slimebound.cards.SplitGreed;
import slimebound.cards.SplitScrap;

import java.lang.reflect.Constructor;

@SpirePatch(
        optional = true,
        cls = "slimebound.orbs.SpawnedSlime",
        method = "onEvoke"
)
public class SlimeEvokePatch {
    private static final String SPAWNED_SLIME_CLASS = "slimebound.orbs.SpawnedSlime";
    private static final String ACID_TONGUE_CLASS = "slimebound.powers.AcidTonguePowerUpgraded";
    private static final String SPLIT_SCRAP_CLASS = "slimebound.cards.SplitScrap";
    private static final String SPLIT_GREED_CLASS = "slimebound.cards.SplitGreed";

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(Object __instance) {
        if (GoopLab.ID.equals(CuratedChallenges.currentChallengeId)) {
            try {
                // Handle the non-evoke bonus cards part
                boolean noEvokeBonus = (boolean) __instance.getClass().getField("noEvokeBonus").get(__instance);
                if (!noEvokeBonus) {
                    if (__instance.getClass().getSimpleName().equals("ScrapOozeSlime")) {
                        Class<?> splitScrapClass = Class.forName(SPLIT_SCRAP_CLASS);
                        AbstractCard splitScrap = (AbstractCard) splitScrapClass.newInstance();
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(splitScrap));
                    } else if (__instance.getClass().getSimpleName().equals("GreedOozeSlime")) {
                        Class<?> splitGreedClass = Class.forName(SPLIT_GREED_CLASS);
                        AbstractCard splitGreed = (AbstractCard) splitGreedClass.newInstance();
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(splitGreed));
                    }
                }

                // Apply AcidTonguePowerUpgraded instead of Strength
                Class<?> acidTongueClass = Class.forName(ACID_TONGUE_CLASS);
                Constructor<?> constructor = acidTongueClass.getConstructor(
                        AbstractCreature.class,
                        AbstractCreature.class,
                        int.class
                );

                AbstractPower acidTonguePower = (AbstractPower) constructor.newInstance(
                        AbstractDungeon.player,
                        AbstractDungeon.player,
                        1
                );

                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(
                                AbstractDungeon.player,
                                AbstractDungeon.player,
                                acidTonguePower,
                                1
                        )
                );

                // Trigger evoke animation
                __instance.getClass().getMethod("triggerEvokeAnimation").invoke(__instance);

                return SpireReturn.Return(null);
            } catch (Exception e) {
                // If reflection fails, continue with normal behavior
                return SpireReturn.Continue();
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.NewExprMatcher(StrengthPower.class);
            int[] matches = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{matches[0]};
        }
    }
}