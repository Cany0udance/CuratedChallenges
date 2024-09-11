package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.HappyFlower;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.EmotionalSupportFlower;

@SpirePatch(
        clz = HappyFlower.class,
        method = "atTurnStart"
)
public class EmotionalSupportFlowerPatch {

    public static void Postfix(HappyFlower __instance) {
        if (EmotionalSupportFlower.ID.equals(CuratedChallenges.currentChallengeId)) {
            if (__instance.counter == 0) {
                AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction(WrathStance.STANCE_ID));
            } else if (__instance.counter == 1 || __instance.counter == 2) {
                AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction(CalmStance.STANCE_ID));
            }
        }
    }
}