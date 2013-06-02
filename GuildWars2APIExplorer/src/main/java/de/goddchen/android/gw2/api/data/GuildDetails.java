package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by Goddchen on 02.06.13.
 */
@DatabaseTable(tableName = "guild_details")
public class GuildDetails {

    @DatabaseField(id = true)
    public String guild_id;

    @DatabaseField
    public String guild_name;

    @DatabaseField
    public String tag;

    public Emblem emblem;

    public class Emblem {
        public int background_id;

        public int foreground_id;

        public List<String> flags;

        public int background_color_id;

        public int foreground_primary_color_id;

        public int foreground_secondary_color_id;
    }
}
