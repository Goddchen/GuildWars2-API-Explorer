package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Goddchen on 22.05.13.
 */
@DatabaseTable(tableName = "item")
public class Item {
    @DatabaseField(id = true, columnName = "_id")
    public int item_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public String type;

    @DatabaseField
    public int level;

    @DatabaseField
    public String ratity;

    @DatabaseField
    public int vendor_value;

    @DatabaseField
    public String[] game_types;

    @DatabaseField
    public String[] flags;

    @DatabaseField
    public String[] restrictions;

    @DatabaseField
    public String carfting_material;
}
