package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Goddchen on 25.10.2014.
 */
@DatabaseTable
public class Quaggan implements Serializable {

    @DatabaseField(generatedId = true)
    public int _id;

    @DatabaseField
    public String id;

    @DatabaseField
    public String url;

}
