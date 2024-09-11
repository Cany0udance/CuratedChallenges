package curatedchallenges.util;

import curatedchallenges.CuratedChallenges;

import static com.megacrit.cardcrawl.unlock.UnlockTracker.achievementPref;

public class ChallengeAchievementUnlocker {
    public static void unlockAchievement(String key) {
        String fullKey = CuratedChallenges.makeID(key);
            if (!achievementPref.getBoolean(fullKey, false)) {
                achievementPref.putBoolean(fullKey, true);
            }

            achievementPref.flush();
        }
    }