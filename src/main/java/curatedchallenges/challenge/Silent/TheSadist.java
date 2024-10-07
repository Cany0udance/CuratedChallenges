package curatedchallenges.challenge.Silent;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Melter;
import com.megacrit.cardcrawl.cards.colorless.HandOfGreed;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.CombustPower;
import com.megacrit.cardcrawl.powers.EnvenomPower;
import com.megacrit.cardcrawl.powers.SadisticPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class TheSadist implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("TheSadist"));

    public static final String ID = "THE_SADIST";

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
        return AbstractPlayer.PlayerClass.THE_SILENT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            deck.add(new Strike_Green());
        }

        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Green());
        }

        deck.add(new Neutralize());

        deck.add(new Survivor());

        deck.add(new Melter());

        AbstractCard riddleWithHoles = new RiddleWithHoles();
        riddleWithHoles.upgrade();
        deck.add(riddleWithHoles);

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(TwistedFunnel.ID).makeCopy());
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
    public void applyPreCombatLogic(AbstractPlayer p) {
        // Player loses 3 Strength
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p,
                new StrengthPower(p, -3), -3));

        // Player gains 1 stack of Envenom
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p,
                new EnvenomPower(p, 1), 1));

        // Player gains 5 stacks of Sadistic Nature
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p,
                new SadisticPower(p, 3), 3));
    }

}