package curatedchallenges.winconditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.interfaces.WinCondition;

public class CompleteActWinCondition implements WinCondition {
    private final int targetAct;

    public CompleteActWinCondition(int targetAct) {
        this.targetAct = targetAct;
    }

    @Override
    public boolean isMet() {
        return AbstractDungeon.actNum == targetAct;
    }

    @Override
    public boolean shouldCheckInBossTreasureRoom() {
        return targetAct == 1 || targetAct == 2;
    }

    @Override
    public boolean shouldCheckInVictoryRoom() {
        return targetAct == 3;
    }

    @Override
    public boolean shouldCheckInTrueVictoryRoom() {
        return targetAct == 4;
    }

    public int getTargetAct() {
        return targetAct;
    }
}