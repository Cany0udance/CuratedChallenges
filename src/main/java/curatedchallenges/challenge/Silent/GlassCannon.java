package curatedchallenges.challenge.Silent;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.HandOfGreed;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.winconditions.GoldThresholdWinCondition;

import java.util.ArrayList;
import java.util.List;

public class GlassCannon implements ChallengeDefinition {
    public static final String ID = "GLASS_CANNON";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Glass Cannon";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.THE_SILENT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AbstractCard strike = new Strike_Green();
            strike.upgrade();
            deck.add(strike);
        }
        for (int i = 0; i < 5; i++) {
            AbstractCard defend = new Defend_Green();
            defend.upgrade();
            deck.add(defend);
        }
        AbstractCard neutralize = new Neutralize();
        neutralize.upgrade();
        deck.add(neutralize);

        AbstractCard survivor = new Survivor();
        survivor.upgrade();
        deck.add(survivor);

        AbstractCard glassKnife = new GlassKnife();
        glassKnife.upgrade();
        deck.add(glassKnife);

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(SnakeRing.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(PenNib.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(FaceOfCleric.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "Start the run with 1 max HP.";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 3.";
    }

    @Override
    public void applyStartOfRunEffect(AbstractPlayer p) {
        // Set the player's max HP to 1
        p.maxHealth = 1;
        // Set the player's current HP to 1
        p.currentHealth = 1;
        // Update the health bar to reflect the changes
        p.healthBarUpdatedEvent();
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        return conditions;
    }

    @Override
    public String getTopPanelSummary() {
        return "#ySpecial #yRules: NL NL - Start the run with 1 max HP. NL NL #yWin #yConditions: Complete Act 3.";
    }

}