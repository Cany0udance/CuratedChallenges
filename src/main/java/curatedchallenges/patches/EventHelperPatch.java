package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.EventHelper;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Avarice;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpirePatch(clz = EventHelper.class, method = SpirePatch.CONSTRUCTOR)
public class EventHelperPatch {
    @SpirePostfixPatch
    public static void Postfix(EventHelper __instance) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId)) {
            try {
                Field shopChanceField = EventHelper.class.getDeclaredField("SHOP_CHANCE");
                shopChanceField.setAccessible(true);
                shopChanceField.setFloat(null, 0f);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Logger.getLogger(EventHelperPatch.class.getName()).log(Level.SEVERE, "Failed to set SHOP_CHANCE", e);
            }
        }
    }
}