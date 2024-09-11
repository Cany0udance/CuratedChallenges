package curatedchallenges.util;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.VictoryScreen;

public class ChallengeVictoryHandler {

    public static void handleVictory(String challengeId) {
        // Unlock achievements
        ChallengeAchievementUnlocker.unlockAchievement(challengeId);
        if (AbstractDungeon.ascensionLevel == 20) {
            ChallengeAchievementUnlocker.unlockAchievement(challengeId + "_A20");
        }

        // Set room phase to complete
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;

        // Open victory screen
        openVictoryScreen();
    }

    private static void openVictoryScreen() {
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.silenceBGMInstantly();
        AbstractMonster.playBossStinger();
        CardCrawlGame.stopClock = true;
        AbstractDungeon.victoryScreen = new VictoryScreen(null);
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.VICTORY;

        AbstractDungeon.dynamicBanner.appear("Challenge Complete!");

        BaseMod.logger.info("Challenge victory screen opened with custom banner text.");
    }

    public static boolean checkDeckForCard(String cardId) {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card.cardID.equals(cardId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkNoDeckForCard(String cardId) {
        return !checkDeckForCard(cardId);
    }

}