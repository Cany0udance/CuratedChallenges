package curatedchallenges.patches.challenges.BlastMining;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.TheBombPower;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Vacant.BlastMining;

@SpirePatch(
        optional = true,
        cls = "com.megacrit.cardcrawl.powers.TheBombPower",
        method = "atEndOfTurn"
)
public class TheBombPowerPatch {
    private static Object getRandomGem() {
        try {
            int randomSize = AbstractDungeon.cardRandomRng.random(1, 3);
            String[] orbTypes = {
                    "theVacant.orbs.AmethystOrb",
                    "theVacant.orbs.DiamondOrb",
                    "theVacant.orbs.EmeraldOrb",
                    "theVacant.orbs.OnyxOrb",
                    "theVacant.orbs.OpalOrb",
                    "theVacant.orbs.RubyOrb",
                    "theVacant.orbs.SapphireOrb",
                    "theVacant.orbs.TopazOrb"
            };

            int randomIndex = AbstractDungeon.cardRandomRng.random(7);
            String selectedOrb = orbTypes[randomIndex];

            Class<?> orbClass = Class.forName(selectedOrb);
            Object orb = orbClass.getConstructor(int.class).newInstance(randomSize);
            return orb;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SpirePostfixPatch
    public static void mineGemsOnExplosion(TheBombPower __instance, boolean isPlayer) {

        if (__instance.amount == 1 && BlastMining.ID.equals(CuratedChallenges.currentChallengeId)) {
            try {
                Class<?> actionClass = Class.forName("theVacant.actions.MineGemAction");
                Class<?> abstractGemOrbClass = Class.forName("theVacant.orbs.AbstractGemOrb");

                for (int i = 0; i < 3; i++) {
                    Object gem = getRandomGem();
                    if (gem != null) {
                        Object action = actionClass.getConstructor(abstractGemOrbClass).newInstance(gem);
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)action);
                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}