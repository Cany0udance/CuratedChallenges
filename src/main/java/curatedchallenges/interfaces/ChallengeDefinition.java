package curatedchallenges.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static curatedchallenges.CuratedChallenges.makeID;

public interface ChallengeDefinition {
    String getId();
    String getName();
    AbstractPlayer.PlayerClass getCharacterClass();
    ArrayList<AbstractCard> getStartingDeck();
    ArrayList<AbstractRelic> getStartingRelics();
    String getSpecialRules();
    String getWinConditions();
    List<WinCondition> getWinConditionLogic();
    default Map<String, String> getPowerDelimiters() {
        return Collections.emptyMap();
    }

    default ArrayList<AbstractPotion> getStartingPotions() {
        return new ArrayList<>();
    }

    default Integer getStartingGold() {
        return null; // Return null if not specified
    }

    default Integer getCardRewardAdjustment() {
        return null; // Return null if not specified
    }

    // New method for start of run effects
    default void applyStartOfRunEffect(AbstractPlayer p) {
        // Default implementation does nothing
    }

    // New method for start of battle actions
    default void applyStartOfBattleEffect(AbstractPlayer p) {
        // Default implementation does nothing
    }

    // New method for pre-combat logic
    default void applyPreCombatLogic(AbstractPlayer p) {
        // Default implementation does nothing
    }

    // New method for start of turn logic
    default void applyStartOfTurnEffect(AbstractPlayer p) {
        // Default implementation does nothing
        // Note that this method is applied post-draw
    }

    default void applyEndOfTurnEffect(AbstractPlayer p) {
    }

    // New method for post battle logic
    default void applyPostBattleEffect(AbstractPlayer p) {
        // Default implementation does nothing
    }

    // New method for start of act effects
    default void applyStartOfActEffect(AbstractPlayer p, int actNumber) {
        // Default implementation does nothing
    }

    default void onMonsterDeath(AbstractPlayer p, AbstractMonster m) {
        // Default implementation does nothing
    }

    default void applyPostGameEffect(AbstractPlayer p) {
        // Usually just for cleanup after a challenge ends
    }

    default boolean requiresBossRelicSelection() {
        return false;
    }

    default String getTopPanelSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(CardCrawlGame.languagePack.getUIString(makeID("TopPanel")).TEXT[0]).append(" NL NL ");

        String[] specialRules = getSpecialRules().split("NL");
        for (int i = 0; i < specialRules.length; i++) {
            summary.append("- ").append(specialRules[i].trim());
            if (i < specialRules.length - 1) {
                summary.append(" NL NL ");
            }
        }

        summary.append(CardCrawlGame.languagePack.getUIString(makeID("TopPanel")).TEXT[1]).append(" NL NL ");

        String[] winConditions = getWinConditions().split("NL");
        for (int i = 0; i < winConditions.length; i++) {
            summary.append("- ").append(winConditions[i].trim());
            if (i < winConditions.length - 1) {
                summary.append(" NL NL ");
            }
        }

        return summary.toString().trim();
    }


    default List<Class<? extends AbstractCard>> getCardsToRemove() {
        return Collections.emptyList();
    }

    default List<String> getRelicsToRemove() {
        return Collections.emptyList();
    }

    default List<Class<? extends AbstractPotion>> getPotionsToRemove() {
        return Collections.emptyList();
    }

    default List<Class<? extends AbstractCard>> getCardsToAdd() {
        return Collections.emptyList();
    }

    default List<String> getRelicsToAdd() {
        return Collections.emptyList();
    }

    default List<Class<? extends AbstractPotion>> getPotionsToAdd() {
        return Collections.emptyList();
    }

    default List<Class<? extends AbstractEvent>> getEventsToRemove() {
        return Collections.emptyList();
    }

    // New methods for forced boss relics, note that it's min 1 max 5
    default List<String> getForcedBossRelicsAct1() {
        return Collections.emptyList();
    }

    default List<String> getForcedBossRelicsAct2() {
        return Collections.emptyList();
    }

}
