package curatedchallenges.challenge.Vacant;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import theVacant.cards.Attacks.BackInTheMine;
import theVacant.cards.Attacks.Pickaxe;
import theVacant.cards.Attacks.VacantStarterStrike;
import theVacant.cards.Skills.AwMan;
import theVacant.cards.Skills.VacantStarterDefend;
import theVacant.characters.TheVacant;
import theVacant.powers.VoidPower;
import theVacant.relics.SilkTouch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static curatedchallenges.CuratedChallenges.makeID;

public class Revenge implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("VacantRevenge"));

    public static final String ID = "VACANT_REVENGE";

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

        for (int i = 0; i < 4; i++) {
            deck.add(new VacantStarterDefend());
        }

        deck.add(new AwMan());

        deck.add(new BackInTheMine());

        deck.add(new Pickaxe());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(SilkTouch.ID).makeCopy());
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
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDying && !monster.isDead) {
                    AbstractDungeon.actionManager.addToBottom(
                            new ApplyPowerAction(
                                    monster,
                                    monster,
                                    new StrengthPower(monster, 2),
                                    2
                            )
                    );
                }
            }
        }
    }

    @Override
    public Map<String, String> getPowerDelimiters() {
        Map<String, String> delimiters = new HashMap<>();
        delimiters.put(StrengthPower.POWER_ID, "2");
        return delimiters;
    }

}