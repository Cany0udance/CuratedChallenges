package curatedchallenges.util;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;

public class ChallengeInfo implements CustomSavable<ChallengeInfo> {
    public static final String SAVE_KEY = "ChallengeInfo";
    public static ChallengeInfo challengeInfo;
    public String challengeId;
    public int ascensionLevel;

    public ChallengeInfo() {
        BaseMod.logger.info("ChallengeInfo: Constructor called");
    }

    @Override
    public ChallengeInfo onSave() {
        BaseMod.logger.info("ChallengeInfo: onSave called");
        BaseMod.logger.info("ChallengeInfo: Saving challengeInfo = " + (challengeInfo != null ? challengeInfo.challengeId : "null"));
        return challengeInfo;
    }

    @Override
    public void onLoad(ChallengeInfo loadedChallengeInfo) {
        BaseMod.logger.info("ChallengeInfo: onLoad called");
        BaseMod.logger.info("ChallengeInfo: Loaded challengeInfo = " + (loadedChallengeInfo != null ? loadedChallengeInfo.challengeId : "null"));
        challengeInfo = loadedChallengeInfo;
    }
}