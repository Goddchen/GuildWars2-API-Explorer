package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 22.05.13.
 */
@DatabaseTable(tableName = "world")
public class World implements Serializable {
    @DatabaseField(id = true, columnName = "_id")
    public int id;

    @DatabaseField
    public String name;
}
