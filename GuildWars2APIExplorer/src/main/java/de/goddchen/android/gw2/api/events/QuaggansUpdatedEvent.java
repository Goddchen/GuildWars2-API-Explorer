package de.goddchen.android.gw2.api.events;

import java.io.Serializable;
import java.util.List;

import de.goddchen.android.gw2.api.data.Quaggan;

/**
 * Created by Goddchen on 25.10.2014.
 */
public class QuaggansUpdatedEvent implements Serializable {

    private List<Quaggan> quaggans;

    public QuaggansUpdatedEvent(List<Quaggan> quaggans) {
        this.quaggans = quaggans;
    }

    public List<Quaggan> getQuaggans() {
        return quaggans;
    }
}
