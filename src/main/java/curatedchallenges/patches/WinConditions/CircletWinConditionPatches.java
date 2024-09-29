package curatedchallenges.patches.WinConditions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;

public class CircletWinConditionPatches {

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "spawnRelicAndObtain"
    )
    public static class AbstractRoomPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRoom __instance, float x, float y, AbstractRelic relic) {
            if (relic.relicId.equals(Circlet.ID) && CuratedChallenges.currentChallengeId != null) {
                ChallengeVictoryHandler.checkCircletCountWinCondition(CuratedChallenges.currentChallengeId);
            }
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "instantObtain",
            paramtypez = {AbstractPlayer.class, int.class, boolean.class}
    )
    @SpirePatch(
            clz = AbstractRelic.class,
            method = "instantObtain",
            paramtypez = {}
    )
    @SpirePatch(
            clz = AbstractRelic.class,
            method = "obtain"
    )
    public static class AbstractRelicPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic instance) {
            if (instance.relicId.equals(Circlet.ID) && CuratedChallenges.currentChallengeId != null) {
                ChallengeVictoryHandler.checkCircletCountWinCondition(CuratedChallenges.currentChallengeId);
            }
        }
    }
}