package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 21.06.13.
 */
@DatabaseTable(tableName = "continent")
public class Continent implements Serializable {

    @DatabaseField(id = true, columnName = "_id")
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public long dims_x;

    @DatabaseField
    public long dims_y;

    @DatabaseField
    public int min_zoom;

    @DatabaseField
    public int max_zoom;

    public long[] continent_dims;

}
