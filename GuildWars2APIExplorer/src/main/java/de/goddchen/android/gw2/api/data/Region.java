package de.goddchen.android.gw2.api.data;

/**
 * Created by Goddchen on 21.06.13.
 */

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "region")
public class Region implements Serializable {

    @DatabaseField(id = true, columnName = "_id")
    public long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Floor floor;

    @DatabaseField
    public String name;

    @DatabaseField
    public int label_coord_x;

    @DatabaseField
    public int label_coord_y;

    @ForeignCollectionField
    public transient ForeignCollection<Map> maps;
}
