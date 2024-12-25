package curatedchallenges.challenge.Vacant;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Akabeko;
import curatedchallenges.actions.RemoveAllBuffsAction;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import theVacant.cards.Attacks.*;
import theVacant.cards.Powers.BurdenBreak;
import theVacant.cards.Skills.AwMan;
import theVacant.cards.Skills.BattleScars;
import theVacant.cards.Skills.RubyRage;
import theVacant.cards.Skills.VacantStarterDefend;
import theVacant.characters.TheVacant;
import theVacant.powers.AntifactPower;
import theVacant.relics.SilkTouch;

import java.util.ArrayList;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class VigorMortis implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("VacantVigorMortis"));

    public static final String ID = "VACANT_VIGOR_MORTIS";

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

        for (int i = 0; i < 2; i++) {
            deck.add(new VacantStarterStrike());
        }

        deck.add(new SoulBash());

        deck.add(new Doomed());

        for (int i = 0; i < 4; i++) {
            deck.add(new VacantStarterDefend());
        }

        deck.add(new BattleScars());

        deck.add(new RubyRage());

        deck.add(new BurdenBreak());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(Akabeko.ID).makeCopy());
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
        conditions.add(new CompleteActWinCondition(3));
        return conditions;
    }

    @Override
    public void onMonsterDeath(AbstractPlayer p, AbstractMonster m) {
        if (m.currentHealth == 0 && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            // Remove buffs via action
            AbstractDungeon.actionManager.addToBottom(
                    new RemoveAllBuffsAction(p)
            );

            // Add Antifact
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(
                            p,
                            p,
                            new AntifactPower(p, p, 3),
                            3
                    )
            );
        }
    }

}