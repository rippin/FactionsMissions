package rippin.bullyscraft.com;

/***
 * Eliminate missions, you must kill all mobs.
 *
 *
 * SPY retrieve item without getting hit.
 *
 */
public enum MissionType {
    RESCUE ("Rescue"), ELIMINATE ("Eliminate"), SPY ("Spy"), BOSS ("Boss"), MULTIBOSS ("MultiBoss"), TIME ("Time"), VILLAGER ("Villager");
    private String value;

   private MissionType(String value){
        this.value = value;
    }

    public String getValue(){
        return  value;
    }
}
