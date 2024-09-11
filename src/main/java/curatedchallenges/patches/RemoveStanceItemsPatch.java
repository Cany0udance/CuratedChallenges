package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.StancePotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Damaru;
import com.megacrit.cardcrawl.relics.TeardropLocket;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.EmotionalSupportFlower;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class RemoveStanceItemsPatch {

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeCardPools"
    )
    public static class RemoveStanceCardsPatch {
        public static void Postfix(AbstractDungeon __instance) {
            if (EmotionalSupportFlower.ID.equals(CuratedChallenges.currentChallengeId)) {
                Predicate<AbstractCard> shouldRemove = card ->
                        card instanceof Tantrum ||
                                card instanceof Indignation ||
                                card instanceof Devotion ||
                                card instanceof Crescendo ||
                                card instanceof Worship ||
                                card instanceof Eruption ||
                                card instanceof Tranquility ||
                                card instanceof Meditate ||
                                card instanceof Vigilance ||
                                card instanceof Prostrate ||
                                card instanceof Blasphemy ||
                                card instanceof Pray ||
                                card instanceof FearNoEvil ||
                                card instanceof InnerPeace ||
                                card instanceof SimmeringFury;

                AbstractDungeon.commonCardPool.group.removeIf(shouldRemove);
                AbstractDungeon.uncommonCardPool.group.removeIf(shouldRemove);
                AbstractDungeon.rareCardPool.group.removeIf(shouldRemove);

                AbstractDungeon.srcCommonCardPool.group.removeIf(shouldRemove);
                AbstractDungeon.srcUncommonCardPool.group.removeIf(shouldRemove);
                AbstractDungeon.srcRareCardPool.group.removeIf(shouldRemove);
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeRelicList"
    )
    public static class RemoveStanceRelicsPatch {
        public static void Postfix(AbstractDungeon __instance) {
            if (EmotionalSupportFlower.ID.equals(CuratedChallenges.currentChallengeId)) {
                List<String> relicsToRemove = Arrays.asList(TeardropLocket.ID, Damaru.ID);

                for (String relicId : relicsToRemove) {
                    AbstractDungeon.commonRelicPool.remove(relicId);
                    AbstractDungeon.uncommonRelicPool.remove(relicId);
                    AbstractDungeon.rareRelicPool.remove(relicId);
                    AbstractDungeon.bossRelicPool.remove(relicId);
                    AbstractDungeon.shopRelicPool.remove(relicId);
                }
            }
        }
    }

    @SpirePatch(
            clz = PotionHelper.class,
            method = "initialize"
    )
    public static class RemoveStancePotionsPatch {
        public static void Postfix() {
            if (EmotionalSupportFlower.ID.equals(CuratedChallenges.currentChallengeId)) {
                PotionHelper.potions.removeIf(potionId -> {
                    AbstractPotion potion = PotionHelper.getPotion(potionId);
                    return potion instanceof StancePotion;
                });
            }
        }
    }
}