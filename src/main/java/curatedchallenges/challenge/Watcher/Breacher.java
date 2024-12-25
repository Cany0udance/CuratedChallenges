package curatedchallenges.challenge.Watcher;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.MalleablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.HandDrill;
import com.megacrit.cardcrawl.relics.PureWater;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class Breacher implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Breacher"));
    public static final String ID = "BREACHER";

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
        return AbstractPlayer.PlayerClass.WATCHER;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deck.add(new Strike_Purple());
        }
        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Watcher());
        }
        deck.add(new Eruption());
        deck.add(new Vigilance());
        deck.add(new ConjureBlade());
        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(PureWater.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(HandDrill.ID).makeCopy());
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
    public ArrayList<AbstractPotion> getStartingPotions() {
        return new ArrayList<>();
    }

    @Override
    public void applyPreCombatLogic(AbstractPlayer p) {
        List<AbstractMonster> validMonsters = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != null && !m.hasPower(MalleablePower.POWER_ID)) {
                validMonsters.add(m);
            }
        }

        if (!validMonsters.isEmpty()) {
            AbstractMonster randomMonster = validMonsters.get(AbstractDungeon.miscRng.random(0, validMonsters.size() - 1));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(randomMonster, randomMonster,
                    new MalleablePower(randomMonster)));
            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxHpAction(randomMonster, 0.25F, true));
        }
    }
}