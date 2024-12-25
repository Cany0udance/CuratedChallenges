package curatedchallenges.challenge.Vacant;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.relics.Shovel;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import theVacant.cards.Attacks.StealSoul;
import theVacant.cards.Attacks.VacantStarterStrike;
import theVacant.cards.Skills.Dig;
import theVacant.cards.Skills.VacantStarterDefend;
import theVacant.characters.TheVacant;
import theVacant.relics.BrassGoblet;
import theVacant.relics.TombstoneRelic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class Gravedigger implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("VacantGravedigger"));

    public static final String ID = "VACANT_GRAVEDIGGER";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return uiStrings.TEXT[0];
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return TheVacant.Enums.THE_VACANT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            deck.add(new VacantStarterStrike());
        }

        deck.add(new StealSoul());

        for (int i = 0; i < 4; i++) {
            deck.add(new VacantStarterDefend());
        }

        deck.add(new Dig());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(BrassGoblet.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(Shovel.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(LizardTail.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return uiStrings.TEXT[1];
    }

    @Override
    public String getWinConditions() {
        return uiStrings.TEXT[2];
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(4));
        return conditions;
    }

    @Override
    public List<String> getRelicsToRemove() {
        return Arrays.asList(TombstoneRelic.ID);
    }

    @Override
    public void applyStartOfRunEffect(AbstractPlayer p) {
        // Cut the player's current HP in half first
        p.currentHealth = p.currentHealth / 2;

        // Then cut the max HP in half
        p.maxHealth = p.maxHealth / 2;

        // Ensure current HP doesn't exceed new max HP
        p.currentHealth = Math.min(p.currentHealth, p.maxHealth);

        p.healthBarUpdatedEvent();
    }

}