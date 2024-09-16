package curatedchallenges.challenge.Watcher;

import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BlackStar;
import com.megacrit.cardcrawl.relics.PureWater;
import curatedchallenges.interfaces.WinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Duet implements ChallengeDefinition {

    public static final String ID = "DUET";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Duet";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.WATCHER;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            deck.add(new Strike_Purple());
        }

        for (int i = 0; i < 2; i++) {
            deck.add(new Strike_Red());
        }

        for (int i = 0; i < 2; i++) {
            deck.add(new Defend_Watcher());
        }

        for (int i = 0; i < 2; i++) {
            deck.add(new Defend_Red());
        }

        deck.add(new Eruption());
        deck.add(new Bash());
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
        return "ALL Watcher Attack cards have been replaced by Ironclad Attack cards.";
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
    public List<Class<? extends AbstractCard>> getCardsToAdd() {
        return Arrays.asList(
                // Common
                Clash.class, IronWave.class, PerfectedStrike.class,
                Armaments.class, Anger.class, Cleave.class, Clothesline.class, TwinStrike.class, WildStrike.class,
                Havoc.class, PommelStrike.class, ShrugItOff.class, SwordBoomerang.class, TrueGrit.class,
                BodySlam.class, Flex.class, Headbutt.class, HeavyBlade.class, ThunderClap.class, Warcry.class,

                // Uncommon
                Carnage.class, FlameBarrier.class, GhostlyArmor.class, InfernalBlade.class, Rampage.class, RecklessCharge.class, SearingBlow.class,
                BloodForBlood.class, FireBreathing.class, Metallicize.class, Rupture.class, Uppercut.class,
                BattleTrance.class, Bloodletting.class, BurningPact.class, Disarm.class, Dropkick.class, DualWield.class, Evolve.class,
                FeelNoPain.class, Inflame.class, PowerThrough.class, Pummel.class, SecondWind.class, SeeingRed.class, Shockwave.class,
                SpotWeakness.class, Whirlwind.class,
                Combust.class, DarkEmbrace.class, Entrench.class, Intimidate.class, Rage.class, Sentinel.class, SeverSoul.class, Hemokinesis.class,

                // Rare
                Bludgeon.class, Feed.class, Immolate.class,
                Corruption.class,
                Barricade.class, Berserk.class, Brutality.class, DemonForm.class, DoubleTap.class, Exhume.class, FiendFire.class,
                Impervious.class, Offering.class, Reaper.class,
                Juggernaut.class, LimitBreak.class
        );
    }

    @Override
    public void applyStartOfRunEffect(AbstractPlayer p) {
        AbstractDungeon.player.masterHandSize += 1;
    }

    @Override
    public String getTopPanelSummary() {
        return "#ySpecial #yRules: NL NL - ALL Ironclad cards are added to the card pool. NL NL - You cannot play two consecutive cards of the same color. NL NL - Draw 1 additional card per turn. NL NL - Shuffling is changed to always draw alternating card colors when possible. NL NL #yWin #yConditions: Complete Act 3.";
    }

}