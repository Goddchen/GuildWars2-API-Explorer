package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

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
    public String rarity;

    @DatabaseField
    public int vendor_value;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String[] game_types;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String[] flags;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String[] restrictions;

    @DatabaseField
    public String crafting_material;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public Consumable consumable;

    public String icon_file_id;

    public String icon_file_signature;

    @DatabaseTable(tableName = "consumable")
    public static class Consumable implements Serializable {
        @DatabaseField(generatedId = true)
        public long id;

        @DatabaseField
        public String type;

        @DatabaseField
        public long duration_ms;

        @DatabaseField
        public String description;
    }
}
