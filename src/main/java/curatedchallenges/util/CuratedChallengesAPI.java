package curatedchallenges.util;

import curatedchallenges.interfaces.ChallengeDefinition;

public class CuratedChallengesAPI {
    public static void registerChallenge(ChallengeDefinition challenge) {
        ChallengeRegistry.registerChallenge(challenge);
    }
}
