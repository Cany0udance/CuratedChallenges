package curatedchallenges.challenge.Silent;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Melter;
import com.megacrit.cardcrawl.cards.colorless.Finesse;
import com.megacrit.cardcrawl.cards.colorless.GoodInstincts;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.DexterityPotion;
import com.megacrit.cardcrawl.potions.SpeedPotion;
import com.megacrit.cardcrawl.potions.StancePotion;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.EnvenomPower;
import com.megacrit.cardcrawl.powers.SadisticPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class TheBestDefense implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("TheBestDefense"));

    public static final String ID = "THE_BEST_DEFENSE";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return uiStrings.TEXT[0];    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.THE_SILENT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            deck.add(new Strike_Green());
        }

        for (int i = 0; i < 3; i++) {
            deck.add(new QuickSlash());
        }

        deck.add(new Neutralize());

        deck.add(new TalkToTheHand());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(OrnamentalFan.ID).makeCopy());
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
    public List<Class<? extends AbstractCard>> getCardsToRemove() {
        return Arrays.asList(
                Defend_Green.class,
                Survivor.class,
                Backflip.class,
                CloakAndDagger.class,
                Deflect.class,
                DodgeAndRoll.class,
                Blur.class,
                Dash.class,
                EscapePlan.class,
                Footwork.class,
                LegSweep.class,
                GoodInstincts.class,
                PanicButton.class,
                Finesse.class
        );
    }

    @Override
    public List<String> getRelicsToRemove() {
        return Arrays.asList(PrismaticShard.ID);
    }

    @Override
    public List<Class<? extends AbstractPotion>> getPotionsToRemove() {
        return Arrays.asList(DexterityPotion.class, SpeedPotion.class);
    }

    @Override
    public Integer getCardRewardAdjustment() {
        return -1;
    }

}