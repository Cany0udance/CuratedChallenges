package curatedchallenges.challenge.Ironclad;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Parasite;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Feed;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.DuVuDoll;
import com.megacrit.cardcrawl.relics.NlothsMask;
import com.megacrit.cardcrawl.relics.StrangeSpoon;
import com.megacrit.cardcrawl.vfx.combat.FastingEffect;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.winconditions.MaxHPWinCondition;
import curatedchallenges.winconditions.RemoveAllCardsWinCondition;

import java.util.ArrayList;
import java.util.List;

public class CheatDay implements ChallengeDefinition {
    public static final String ID = "CHEAT_DAY";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Cheat Day";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.IRONCLAD;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            deck.add(new Strike_Red());
        }

        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Red());
        }

        deck.add(new Feed());

        deck.add(new Bash());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(NlothsMask.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(StrangeSpoon.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "Start with half max HP. NL At the start of combat, apply the NL effects of the Fasting power.";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 3 OR NL have at least 200 Max HP.";
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        conditions.add(new MaxHPWinCondition(200));
        return conditions;
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

    @Override
    public void applyPreCombatLogic(AbstractPlayer p) {
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new FastingEffect(p.hb.cX, p.hb.cY, Color.CHARTREUSE)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, 3), 3));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, 3), 3));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new EnergyDownPower(p, 1, true), 1));
    }

}