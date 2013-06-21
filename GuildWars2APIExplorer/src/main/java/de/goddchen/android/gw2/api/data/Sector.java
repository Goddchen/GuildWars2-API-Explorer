package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Goddchen on 21.06.13.
 */
@DatabaseTable(tableName = "sector")
public class Sector {

    @DatabaseField(id = true, columnName = "_id")
    public long sector_id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Map map;

    @DatabaseField
    public String name;

    @DatabaseField
    public int level;

    @DatabaseField
    public float coord_x;

    @DatabaseField
    public float coord_y;
}
