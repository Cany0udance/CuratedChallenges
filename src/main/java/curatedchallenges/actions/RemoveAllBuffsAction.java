package curatedchallenges.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class RemoveAllBuffsAction extends AbstractGameAction {
    private AbstractCreature target;

    public RemoveAllBuffsAction(AbstractCreature target) {
        this.target = target;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.DEBUFF;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.target.powers) {
                if (power.type == AbstractPower.PowerType.BUFF) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.target.powers.remove(power);
            }
        }
        this.tickDuration();
    }
}