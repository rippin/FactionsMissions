package rippin.bullyscraft.com;

/***
 * Eliminate missions, you must kill all mobs.
 *
 *
 * SPY retrieve item without getting hit.
 *
 */
public enum MissionType {
    RESCUE ("Rescue"), ELIMINATE ("Eliminate"), SPY ("Spy");
    private String value;

   private MissionType(String value){
        this.value = value;
    }

    public String getValue(){
        return  value;
    }
}
