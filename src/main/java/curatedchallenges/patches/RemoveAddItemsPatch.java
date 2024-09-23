
package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class RemoveAddItemsPatch {

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "initializeCardPools"
)
public static class ModifyCardsPatch {
    public static void Postfix(AbstractDungeon __instance) {
        ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
        if (currentChallenge != null) {
            // Remove cards
            List<Class<? extends AbstractCard>> cardsToRemove = currentChallenge.getCardsToRemove();
            Predicate<AbstractCard> shouldRemove = card -> cardsToRemove.stream().anyMatch(cls -> cls.isInstance(card));
            removeCardsFromPools(shouldRemove);

            // Add cards
            List<Class<? extends AbstractCard>> cardsToAdd = currentChallenge.getCardsToAdd();
            addCardsToPools(cardsToAdd);
        }
    }

    private static void removeCardsFromPools(Predicate<AbstractCard> shouldRemove) {
        AbstractDungeon.commonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.uncommonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.rareCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.colorlessCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.curseCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcCommonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcUncommonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcRareCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcColorlessCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcCurseCardPool.group.removeIf(shouldRemove);
    }

    private static void addCardsToPools(List<Class<? extends AbstractCard>> cardClasses) {
        for (Class<? extends AbstractCard> cardClass : cardClasses) {
            try {
                AbstractCard card = cardClass.newInstance();
                switch (card.rarity) {
                    case COMMON:
                        AbstractDungeon.commonCardPool.addToTop(card);
                        AbstractDungeon.srcCommonCardPool.addToBottom(card);
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonCardPool.addToTop(card);
                        AbstractDungeon.srcUncommonCardPool.addToBottom(card);
                        break;
                    case RARE:
                        AbstractDungeon.rareCardPool.addToTop(card);
                        AbstractDungeon.srcRareCardPool.addToBottom(card);
                        break;
                    case CURSE:
                        AbstractDungeon.curseCardPool.addToTop(card);
                        AbstractDungeon.srcCurseCardPool.addToBottom(card);
                        break;
                }
                if (card.color == AbstractCard.CardColor.COLORLESS) {
                    AbstractDungeon.colorlessCardPool.addToTop(card);
                    AbstractDungeon.srcColorlessCardPool.addToBottom(card);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "initializeRelicList"
)
public static class ModifyRelicsPatch {
    public static void Postfix(AbstractDungeon __instance) {
        ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
        if (currentChallenge != null) {
            // Remove starter relics from pools
            ArrayList<AbstractRelic> starterRelics = currentChallenge.getStartingRelics();
            for (AbstractRelic relic : starterRelics) {
                removeRelicFromPools(relic.relicId);
            }

            // Remove relics
            List<String> relicsToRemove = currentChallenge.getRelicIdsToRemove();
            for (String relicId : relicsToRemove) {
                removeRelicFromPools(relicId);
            }

            // Add relics
            List<String> relicsToAdd = currentChallenge.getRelicIdsToAdd();
            addRelicsToPools(relicsToAdd);
        }
    }

    private static void removeRelicFromPools(String relicId) {
        AbstractDungeon.commonRelicPool.remove(relicId);
        AbstractDungeon.uncommonRelicPool.remove(relicId);
        AbstractDungeon.rareRelicPool.remove(relicId);
        AbstractDungeon.bossRelicPool.remove(relicId);
        AbstractDungeon.shopRelicPool.remove(relicId);
    }

    private static void addRelicsToPools(List<String> relicIds) {
        for (String relicId : relicIds) {
            AbstractRelic relic = RelicLibrary.getRelic(relicId);
            if (relic != null) {
                switch (relic.tier) {
                    case COMMON:
                        AbstractDungeon.commonRelicPool.add(relicId);
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonRelicPool.add(relicId);
                        break;
                    case RARE:
                        AbstractDungeon.rareRelicPool.add(relicId);
                        break;
                    case BOSS:
                        AbstractDungeon.bossRelicPool.add(relicId);
                        break;
                    case SHOP:
                        AbstractDungeon.shopRelicPool.add(relicId);
                        break;
                }
            }
        }
    }
}

@SpirePatch(
        clz = PotionHelper.class,
        method = "initialize"
)
public static class ModifyPotionsPatch {
    public static void Postfix() {
        ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
        if (currentChallenge != null) {
            // Remove potions
            List<Class<? extends AbstractPotion>> potionsToRemove = currentChallenge.getPotionsToRemove();
            PotionHelper.potions.removeIf(potionId -> {
                AbstractPotion potion = PotionHelper.getPotion(potionId);
                return potionsToRemove.stream().anyMatch(cls -> cls.isInstance(potion));
            });

            // Add potions
            List<Class<? extends AbstractPotion>> potionsToAdd = currentChallenge.getPotionsToAdd();
            for (Class<? extends AbstractPotion> potionClass : potionsToAdd) {
                try {
                    AbstractPotion potion = potionClass.newInstance();
                    PotionHelper.potions.add(potion.ID);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
}
