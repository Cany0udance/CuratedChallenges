package curatedchallenges.util;

import curatedchallenges.interfaces.ChallengeDefinition;

import java.util.HashMap;
import java.util.Map;

public class ChallengeRegistry {
    private static final Map<String, ChallengeDefinition> challenges = new HashMap<>();

    public static void registerChallenge(ChallengeDefinition challenge) {
        challenges.put(challenge.getId(), challenge);
    }

    public static ChallengeDefinition getChallenge(String id) {
        return challenges.get(id);
    }

    public static Map<String, ChallengeDefinition> getAllChallenges() {
        return new HashMap<>(challenges);
    }
}
