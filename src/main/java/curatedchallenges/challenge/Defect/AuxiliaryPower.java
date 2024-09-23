package curatedchallenges.challenge.Defect;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldPlatedCables;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static curatedchallenges.CuratedChallenges.makeID;

public class AuxiliaryPower implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AuxiliaryPower"));
    public static final String ID = "AUXILIARY_POWER";
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
        return AbstractPlayer.PlayerClass.DEFECT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            deck.add(new Strike_Blue());
        }

        for (int i = 0; i < 2; i++) {
            deck.add(new CompileDriver());
        }

        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Blue());
        }

        deck.add(new Zap());
        deck.add(new Dualcast());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(GoldPlatedCables.ID).makeCopy());
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
    public void applyStartOfRunEffect(AbstractPlayer p) {
        AbstractDungeon.player.masterHandSize -= 2;
        AbstractDungeon.player.masterMaxOrbs += 1;
        --AbstractDungeon.player.energy.energyMaster;
    }

    @Override
    public void applyStartOfBattleEffect(AbstractPlayer p) {
        // Create a list of orb types
        ArrayList<AbstractOrb> orbTypes = new ArrayList<>();
        orbTypes.add(new Lightning());
        orbTypes.add(new Dark());
        orbTypes.add(new Frost());
        orbTypes.add(new Plasma());

        // Shuffle the list to randomize the order
        Collections.shuffle(orbTypes, new Random(AbstractDungeon.miscRng.randomLong()));

        // Channel each orb in the randomized order
        for (AbstractOrb orb : orbTypes) {
            p.channelOrb(orb);
        }
    }

}