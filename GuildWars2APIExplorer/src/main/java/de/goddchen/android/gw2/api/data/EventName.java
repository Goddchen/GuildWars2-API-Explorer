package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 22.05.13.
 */
@DatabaseTable(tableName = "eventname")
public class EventName implements Serializable {
    @DatabaseField(id = true, columnName = "_id")
    public String id;

    @DatabaseField
    public String name;
}
