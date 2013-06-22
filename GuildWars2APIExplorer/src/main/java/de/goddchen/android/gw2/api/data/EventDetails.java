package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 22.06.13.
 */
@DatabaseTable(tableName = "event_details")
public class EventDetails implements Serializable {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int level;

    @DatabaseField
    public long map_id;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String[] flags;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Location location;

    public class Location implements Serializable {
        public String type;

        public double[] center;
    }

}
