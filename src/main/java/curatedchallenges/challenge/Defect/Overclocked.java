package curatedchallenges.challenge.Defect;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.powers.CombustPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.CrackedCore;
import com.megacrit.cardcrawl.relics.RegalPillow;
import com.megacrit.cardcrawl.relics.WingBoots;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static basemod.BaseMod.logger;

public class Overclocked implements ChallengeDefinition {
    public static final String ID = "OVERCLOCKED";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Overclocked";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.DEFECT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            deck.add(new Strike_Blue());
        }

        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Blue());
        }

        deck.add(new Zap());
        deck.add(new Dualcast());

        for (int i = 0; i < 2; i++) {
            deck.add(new Overclock());
        }

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(CrackedCore.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(RegalPillow.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "At the start of your turn, gain the effects of the Combust power.";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 3.";
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        return conditions;
    }

    @Override
    public void applyStartOfTurnEffect(AbstractPlayer p) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new CombustPower(p, 1, 5), 5));
    }

}