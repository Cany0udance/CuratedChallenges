package curatedchallenges.patches.challenges.Avarice;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Avarice;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpirePatch(clz = RoomTypeAssigner.class, method = "assignRowAsRoomType")
public class GuaranteedShopPatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(ArrayList<MapRoomNode> row, Class<? extends AbstractRoom> c) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId) && c == TreasureRoom.class) {
            for (MapRoomNode n : row) {
                if (n.getRoom() == null) {
                    try {
                        n.setRoom(new ShopRoom());
                    } catch (Exception e) {
                        Logger.getLogger(GuaranteedShopPatch.class.getName()).log(Level.SEVERE, "Failed to set ShopRoom", e);
                    }
                }
            }
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}