package curatedchallenges.winconditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import curatedchallenges.interfaces.WinCondition;

public class CircletCountWinCondition implements WinCondition {
    private final int requiredCount;

    public CircletCountWinCondition(int requiredCount) {
        this.requiredCount = requiredCount;
    }

    @Override
    public boolean isMet() {
        AbstractRelic circlet = AbstractDungeon.player.getRelic(Circlet.ID);
        return circlet != null && circlet.counter >= requiredCount;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    @Override
    public boolean shouldCheckInBossTreasureRoom() {
        return true;
    }

    @Override
    public boolean shouldCheckInVictoryRoom() {
        return true;
    }

    @Override
    public boolean shouldCheckInTrueVictoryRoom() {
        return true;
    }
}