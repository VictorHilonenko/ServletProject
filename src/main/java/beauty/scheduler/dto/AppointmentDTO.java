package beauty.scheduler.dto;

import java.util.HashMap;

//NOTE: through this class implemented security policy of business logic with providing proper access to each field
//and also the volume of data transfered will be less when we use a map instead of many "null"s
public class AppointmentDTO {
    private HashMap<String, String> map;

    public AppointmentDTO() {
        this.map = new HashMap<>();
    }

    public AppointmentDTO(HashMap<String, String> fieldsMap) {
        this.map = fieldsMap;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
}