package curatedchallenges.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public interface ChallengeDefinition {
    String getId();
    String getName();
    AbstractPlayer.PlayerClass getCharacterClass();
    ArrayList<AbstractCard> getStartingDeck();
    ArrayList<AbstractRelic> getStartingRelics();
    String getSpecialRules();
    String getWinConditions();
}