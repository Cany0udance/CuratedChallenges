package curatedchallenges.patches.challenges.Matchmaker;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Matchmaker;
import curatedchallenges.screens.MatchAndKeepCardRewardScreen;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static curatedchallenges.CuratedChallenges.makeID;

public class MatchmakerPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MatchmakerText"));
    private static boolean isProcessing = false;
    private static boolean isMatchAndKeepActive = false;

    @SpirePatch(clz = CardRewardScreen.class, method = "open")
    public static class CardRewardScreenOpenPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
            if (Matchmaker.ID.equals(CuratedChallenges.currentChallengeId) && !isProcessing) {
                isProcessing = true;
                isMatchAndKeepActive = true;
                MatchAndKeepCardRewardScreen customScreen = new MatchAndKeepCardRewardScreen();
                customScreen.openImpl(cards, rItem, header);

                // Replace the instance in AbstractDungeon
                try {
                    Field cardRewardScreenField = AbstractDungeon.class.getDeclaredField("cardRewardScreen");
                    cardRewardScreenField.setAccessible(true);
                    cardRewardScreenField.set(null, customScreen);

                    // Force an update of the game state
                    AbstractDungeon.overlayMenu.showBlackScreen();
                    AbstractDungeon.screen = AbstractDungeon.CurrentScreen.CARD_REWARD;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                isProcessing = false;
                return SpireReturn.Return(null);
            }
            isMatchAndKeepActive = false;
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class SetupItemRewardPatch {
        @SpirePostfixPatch
        public static void Postfix(CombatRewardScreen __instance) {
            if (Matchmaker.ID.equals(CuratedChallenges.currentChallengeId)) {
                for (RewardItem reward : __instance.rewards) {
                    if (reward.type == RewardItem.RewardType.CARD) {
                        reward.text = uiStrings.TEXT[0];
                    }
                }
            }
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "reopen")
    public static class CardRewardScreenReopenPatch {
        @SpirePrefixPatch
        public static void Prefix(CardRewardScreen __instance) {
            if (isMatchAndKeepActive) {
                ReflectionHacks.setPrivate(__instance, CardRewardScreen.class, "draft", true);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(CardRewardScreen __instance) {
            if (isMatchAndKeepActive) {
                ReflectionHacks.setPrivate(__instance, CardRewardScreen.class, "draft", false);
                AbstractDungeon.dynamicBanner.hide();
            }
        }
    }
}