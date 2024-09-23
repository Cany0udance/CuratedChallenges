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
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class AmpedEnemies implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AmpedEnemies"));

    public static final String ID = "AMPED_ENEMIES";

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

        for (int i = 0; i < 4; i++) {
            deck.add(new Strike_Purple());
        }
        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Watcher());
        }

        deck.add(new Eruption());
        deck.add(new Vigilance());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(PureWater.ID).makeCopy());
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
        ArrayList<AbstractPotion> potions = new ArrayList<>();
        potions.add(new SmokeBomb());
        return potions;
    }

    @Override
    public void applyPreCombatLogic(AbstractPlayer p) {
        int buffChoice = AbstractDungeon.miscRng.random(0, 3);

        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            switch (buffChoice) {
                case 0:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m,
                            new StrengthPower(m, AbstractDungeon.actNum + 1), AbstractDungeon.actNum + 1));
                    break;
                case 1:
                    AbstractDungeon.actionManager.addToBottom(new IncreaseMaxHpAction(m, 0.25F, true));
                    break;
                case 2:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m,
                            new MetallicizePower(m, AbstractDungeon.actNum * 2 + 2), AbstractDungeon.actNum * 2 + 2));
                    break;
                case 3:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m,
                            new RegenerateMonsterPower(m, 1 + AbstractDungeon.actNum * 2), 1 + AbstractDungeon.actNum * 2));
                    break;
            }
        }
    }

}