package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 21.06.13.
 */
@DatabaseTable(tableName = "floor")
public class Floor implements Serializable {

    @DatabaseField(generatedId = true, columnName = "_id")
    public long id;

    @DatabaseField
    public long continent_id;

    @DatabaseField
    public long floor_id;

    @ForeignCollectionField
    public ForeignCollection<Region> regions;

}
