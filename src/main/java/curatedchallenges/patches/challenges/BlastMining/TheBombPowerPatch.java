package curatedchallenges.patches.challenges.BlastMining;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.TheBombPower;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Vacant.BlastMining;
import theVacant.actions.MineGemAction;
import theVacant.orbs.*;

@SpirePatch(
        optional = true,
        cls = "com.megacrit.cardcrawl.powers.TheBombPower",
        method = "atEndOfTurn"
)
public class TheBombPowerPatch {
    private static AbstractGemOrb getRandomGem() {
        int randomSize = AbstractDungeon.cardRandomRng.random(1, 3);
        switch (AbstractDungeon.cardRandomRng.random(7)) {
            case 0: return new AmethystOrb(randomSize);
            case 1: return new DiamondOrb(randomSize);
            case 2: return new EmeraldOrb(randomSize);
            case 3: return new OnyxOrb(randomSize);
            case 4: return new OpalOrb(randomSize);
            case 5: return new RubyOrb(randomSize);
            case 6: return new SapphireOrb(randomSize);
            default: return new TopazOrb(randomSize);
        }
    }

    @SpirePostfixPatch
    public static void mineGemsOnExplosion(TheBombPower __instance, boolean isPlayer) {
        if (__instance.amount == 1 && BlastMining.ID.equals(CuratedChallenges.currentChallengeId)) {
            for (int i = 0; i < 3; i++) {
                AbstractDungeon.actionManager.addToBottom(
                        new MineGemAction(getRandomGem())
                );
            }
        }
    }
}