package curatedchallenges.patches.RunHistoryScreen;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.stats.RunData;
import curatedchallenges.util.ChallengeInfo;
import javassist.*;

@SpirePatch(clz = CardCrawlGame.class, method = "<ctor>")
public class ChallengeRunInfoPatch {
    @SpireRawPatch
    public static void addChallengeInfoField(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
        CtClass runData = ctBehavior.getDeclaringClass().getClassPool().get(RunData.class.getName());
        String fieldSource = String.format("public %s %s;", ChallengeInfo.class.getName(), ChallengeInfo.SAVE_KEY);
        CtField field = CtField.make(fieldSource, runData);
        runData.addField(field);
    }
}