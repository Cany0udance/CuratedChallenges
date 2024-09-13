package curatedchallenges.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.List;

public interface ChallengeDefinition {
    String getId();
    String getName();
    AbstractPlayer.PlayerClass getCharacterClass();
    ArrayList<AbstractCard> getStartingDeck();
    ArrayList<AbstractRelic> getStartingRelics();
    String getSpecialRules();
    String getWinConditions();
    List<WinCondition> getWinConditionLogic();

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
    }

    // New method for post battle logic
    default void applyPostBattleEffect(AbstractPlayer p) {
        // Default implementation does nothing
    }

    // New method for start of act effects
    default void applyStartOfActEffect(AbstractPlayer p, int actNumber) {
        // Default implementation does nothing
    }

    default String getTopPanelSummary() {
        return getName() + ": " + getSpecialRules();
    }

}
