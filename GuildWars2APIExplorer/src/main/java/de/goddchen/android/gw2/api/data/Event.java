package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 22.05.13.
 */
@DatabaseTable(tableName = "event")
public class Event implements Serializable {
    @DatabaseField
    public int world_id;

    @DatabaseField
    public int map_id;

    @DatabaseField
    public String event_id;

    @DatabaseField
    public String state;

    public EventName eventName;

    public MapName mapName;

    public int getStatePriority() {
        if ("active".equalsIgnoreCase(state)) {
            return 4;
        } else if ("warmup".equalsIgnoreCase(state)) {
            return 3;
        } else if ("success".equalsIgnoreCase(state)) {
            return 2;
        } else if ("fail".equalsIgnoreCase(state)) {
            return 1;
        } else {
            return 0;
        }
    }

}
