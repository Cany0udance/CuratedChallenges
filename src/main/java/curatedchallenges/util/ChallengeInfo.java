package curatedchallenges.util;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;

public class ChallengeInfo implements CustomSavable<ChallengeInfo> {
    public static final String SAVE_KEY = "ChallengeInfo";
    public static ChallengeInfo challengeInfo;
    public String challengeId;
    public int ascensionLevel;

    public ChallengeInfo() {

    }

    @Override
    public ChallengeInfo onSave() {
        return challengeInfo;
    }

    @Override
    public void onLoad(ChallengeInfo loadedChallengeInfo) {
        challengeInfo = loadedChallengeInfo;
    }
}