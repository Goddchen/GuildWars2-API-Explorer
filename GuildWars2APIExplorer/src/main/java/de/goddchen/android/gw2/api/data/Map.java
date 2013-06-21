package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 21.06.13.
 */
@DatabaseTable(tableName = "map")
public class Map implements Serializable {

    @DatabaseField(id = true, columnName = "_id")
    public long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Region region;

    @DatabaseField
    public String name;

    @DatabaseField
    public int min_level;

    @DatabaseField
    public int max_level;

    @DatabaseField
    public long default_floor;

    @DatabaseField
    public String region_name;

    @DatabaseField
    public long continent_id;

    @DatabaseField
    public String continent_name;

    @ForeignCollectionField
    public transient ForeignCollection<POI> pois;

    @ForeignCollectionField
    public transient ForeignCollection<Task> tasks;

    @ForeignCollectionField
    public transient ForeignCollection<SkillChallenge> skill_challenges;

    @ForeignCollectionField
    public transient ForeignCollection<Sector> sectors;

}
