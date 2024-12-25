package curatedchallenges.util;

import com.evacipated.cardcrawl.modthespire.Loader;

public class ModCharacterHandler {
    private static final String VACANT_MOD_ID = "theVacant";
  //  private static final String BOGWARDEN_MOD_ID = "TheBogwarden";

    private static boolean isVacantLoaded = false;
   // private static boolean isBogwardenLoaded = false;

    static {
        isVacantLoaded = Loader.isModLoaded(VACANT_MOD_ID);
      //  isBogwardenLoaded = Loader.isModLoaded(BOGWARDEN_MOD_ID);
    }

    public static boolean isVacantModLoaded() {
        return isVacantLoaded;
    }

    /*
    public static boolean isBogwardenModLoaded() {
        return isBogwardenLoaded;
    }

     */

}