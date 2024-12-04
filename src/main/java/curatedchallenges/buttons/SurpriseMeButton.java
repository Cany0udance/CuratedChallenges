package curatedchallenges.buttons;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;

import java.lang.reflect.Field;

import static curatedchallenges.CuratedChallenges.makeID;

public class SurpriseMeButton extends CustomModeCharacterButton {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("SurpriseMeButton"));

    public SurpriseMeButton() {
        super(CardCrawlGame.characterManager.getCharacter(AbstractPlayer.PlayerClass.IRONCLAD), false);
        try {
            // Set custom button image
            Field buttonImgField = CustomModeCharacterButton.class.getDeclaredField("buttonImg");
            buttonImgField.setAccessible(true);
            buttonImgField.set(this, ImageMaster.loadImage("curatedchallenges/images/surpriseMeButton.png"));

            // Create custom CharacterStrings object
            CharacterStrings customStrings = new CharacterStrings();
            customStrings.NAMES = new String[]{uiStrings.TEXT[0]};  // "Surprise Me!"
            customStrings.TEXT = new String[]{uiStrings.TEXT[1]};   // Description text

            // Set the custom strings
            Field charStringsField = CustomModeCharacterButton.class.getDeclaredField("charStrings");
            charStringsField.setAccessible(true);
            charStringsField.set(this, customStrings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}