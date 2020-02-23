package beauty.scheduler.dto;

import java.util.HashMap;

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