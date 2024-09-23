package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import curatedchallenges.CuratedChallenges;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = "loadPlayerSave"
)
public class LoadPlayerSavePatch {
    private static final Logger logger = LogManager.getLogger(LoadPlayerSavePatch.class.getName());

    @SpirePostfixPatch
    public static void Postfix(CardCrawlGame __instance, AbstractPlayer p) {
        // Check if a challenge is active in the loaded save
        if (CuratedChallenges.currentChallengeId != null) {
            // Re-trigger the patched methods
            reinitializeCardPools();
            reinitializeRelicList();
            reinitializePotions(p);
        }
    }

    private static void reinitializeCardPools() {
        try {
            Method initializeCardPools = AbstractDungeon.class.getDeclaredMethod("initializeCardPools");
            initializeCardPools.setAccessible(true);
            initializeCardPools.invoke(null);  // Invoke statically
        } catch (Exception e) {
            logger.error("Failed to reinitialize card pools: " + e.getMessage());
        }
    }

    private static void reinitializeRelicList() {
        try {
            Method initializeRelicList = AbstractDungeon.class.getDeclaredMethod("initializeRelicList");
            initializeRelicList.setAccessible(true);
            initializeRelicList.invoke(null);
        } catch (Exception e) {
            logger.error("Failed to reinitialize relic list: " + e.getMessage());
        }
    }

    private static void reinitializePotions(AbstractPlayer p) {
        PotionHelper.initialize(p.chosenClass);
    }
}