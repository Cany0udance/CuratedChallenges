
/*

package curatedchallenges.patches.challenges.Matchmaker;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Matchmaker;
import curatedchallenges.screens.MatchAndKeepCardRewardScreen;

@SpirePatch(clz = CampfireSleepEffect.class, method = "update")
public class DreamCatcherFixPatch {
    @SpirePostfixPatch
    public static void Postfix(CampfireSleepEffect __instance) {
        // Check if the Match & Keep challenge is active
        if (Matchmaker.ID.equals(CuratedChallenges.currentChallengeId) &&
                AbstractDungeon.cardRewardScreen instanceof MatchAndKeepCardRewardScreen) {
            // Set the room phase to INCOMPLETE
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        }
    }
}

 */