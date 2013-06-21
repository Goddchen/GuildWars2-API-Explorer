package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 21.06.13.
 */
@DatabaseTable(tableName = "poi")
public class POI implements Serializable {

    @DatabaseField(id = true, columnName = "_id")
    public long poi_id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Map map;

    @DatabaseField
    public String name;

    @DatabaseField
    public String type;

    @DatabaseField
    public long floor;

    @DatabaseField
    public double coord_x;

    @DatabaseField
    public double coord_y;

    public double[] coord;
}
