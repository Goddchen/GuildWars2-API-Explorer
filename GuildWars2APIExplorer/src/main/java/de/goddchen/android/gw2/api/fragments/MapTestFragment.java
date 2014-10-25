package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.views.MapView;

/**
 * Created by Goddchen on 21.06.13.
 */
public class MapTestFragment extends Fragment {

    public static MapTestFragment newInstance() {
        MapTestFragment fragment = new MapTestFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ITileSource tileSource = new OnlineTileSourceBase("GW2 API Tiles",
                ResourceProxy.string.unknown, 0, 10, 256,
                ".jpg", "https://tiles.guildwars2.com/1/1/") {
            @Override
            public String getTileURLString(MapTile mapTile) {
                return getBaseUrl() + +mapTile.getZoomLevel() +
                        "/" + mapTile.getX() +
                        "/" + mapTile.getY() + ".jpg";
            }
        };
        MapView mapView = new MapView(getActivity(), 256);
        mapView.setTileSource(tileSource);
        mapView.setUseDataConnection(true);
        mapView.setBuiltInZoomControls(true);
        return mapView;
    }
}
