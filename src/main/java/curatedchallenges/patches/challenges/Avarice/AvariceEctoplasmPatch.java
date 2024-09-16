package curatedchallenges.patches.challenges.Avarice;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Avarice;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "loseGold"
)
public class AvariceEctoplasmPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractPlayer __instance, int goldAmount) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId) &&
                AbstractDungeon.getCurrRoom() instanceof ShopRoom &&
                goldAmount > 0 &&
                !__instance.hasRelic(Ectoplasm.ID)) {

            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                    (float)(Settings.WIDTH / 2),
                    (float)(Settings.HEIGHT / 2),
                    new Ectoplasm()
            );
        }
    }
}