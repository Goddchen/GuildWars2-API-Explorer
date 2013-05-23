package de.goddchen.android.gw2.api.data;

import java.io.Serializable;

/**
 * Created by Goddchen on 22.05.13.
 */
public class Match implements Serializable {
    public String wvw_match_id;

    public int red_world_id;

    public int blue_world_id;

    public int green_world_id;

    public World redWorld;

    public World blueWorld;

    public World greenWorld;
}
