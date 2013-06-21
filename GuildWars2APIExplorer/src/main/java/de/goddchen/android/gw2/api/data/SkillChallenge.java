package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 21.06.13.
 */
@DatabaseTable(tableName = "skill_challenge")
public class SkillChallenge implements Serializable {

    @DatabaseField(generatedId = true, columnName = "_id")
    public long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Map map;

    @DatabaseField
    public float coord_x;

    @DatabaseField
    public float coord_y;

}
