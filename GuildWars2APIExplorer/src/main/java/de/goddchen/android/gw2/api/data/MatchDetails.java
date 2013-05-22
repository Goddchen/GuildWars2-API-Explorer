package de.goddchen.android.gw2.api.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchDetails implements Serializable {
    public String match_id;
    public int[] scores;
    public List<Map> maps;

    public class Map implements Serializable {
        public String type;
        public int[] scores;
        public List<Objective> objectives;
    }

    public class Objective implements Serializable {
        public int id;
        public String owner;
        public String owner_build;
        public ObjectiveName name;

        public int getOwnerPriority() {
            if ("green".equalsIgnoreCase(owner)) {
                return 3;
            } else if ("blue".equalsIgnoreCase(owner)) {
                return 2;
            } else if ("red".equalsIgnoreCase(owner)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
