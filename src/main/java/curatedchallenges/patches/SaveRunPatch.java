package curatedchallenges.patches;

import curatedchallenges.CuratedChallenges;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

public class SaveRunPatch {
    @SpirePatch(cls = "com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue", method = "save")
    public static class SaveGame {
        public static void Prefix(final SaveFile save) {
            CuratedChallenges.saveChallengeData();
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue", method = "deleteSave")
    public static class DeleteSave {
        public static void Prefix(final AbstractPlayer p) {
            CuratedChallenges.clearChallengeData();
        }
    }
}