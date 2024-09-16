package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;
import java.util.List;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "generateEvent"
)
public class EventRemovalPatch {
    @SpirePostfixPatch
    public static AbstractEvent Postfix(AbstractEvent __result, Random rng) {
        ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
        if (currentChallenge != null) {
            List<Class<? extends AbstractEvent>> eventsToRemove = currentChallenge.getEventsToRemove();
            int attempts = 0;
            while (__result != null && isEventBanned(__result.getClass(), eventsToRemove)) {
                attempts++;
                __result = AbstractDungeon.getEvent(rng);
                if (attempts > 100) {
                    break;
                }
            }
        }
        return __result;
    }

    private static boolean isEventBanned(Class<? extends AbstractEvent> eventClass, List<Class<? extends AbstractEvent>> bannedEvents) {
        return bannedEvents.stream().anyMatch(bannedClass -> bannedClass.isAssignableFrom(eventClass));
    }
}