package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import de.goddchen.android.gw2.api.async.FloorLoader;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Floor;
import de.goddchen.android.gw2.api.data.Map;
import de.goddchen.android.gw2.api.data.POI;
import de.goddchen.android.gw2.api.data.Region;
import microsoft.mappoint.TileSystem;

/**
 * Created by Goddchen on 21.06.13.
 */
public class ContinentFragment extends SherlockFragment {

    private static final String EXTRA_CONTINENT = "contintent";

    private Continent mContinent;

    private MapView mMapView;

    private ItemizedOverlay<OverlayItem> mPOIOverlay;

    public static ContinentFragment newInstance(Continent continent) {
        ContinentFragment fragment = new ContinentFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CONTINENT, continent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContinent = (Continent) getArguments().getSerializable(EXTRA_CONTINENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ITileSource tileSource = new XYTileSource(mContinent.name + " tiles",
                null, mContinent.min_zoom, mContinent.max_zoom, 256, ".jpg",
                String.format("https://tiles.guildwars2.com/%d/1/", mContinent.id));
        mMapView = new MapView(getActivity(), 256);
        mMapView.setTileSource(tileSource);
        mMapView.setMultiTouchControls(true);
        mMapView.setUseDataConnection(true);
        mMapView.setBuiltInZoomControls(true);
        return mMapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getLoaderManager()
//                .initLoader(Application.Loaders.FLOOR_METADATA, null, mFloorLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*try {
            Floor floor = Application.getDatabaseHelper().getFloorDao().queryForFirst(
                    Application.getDatabaseHelper().getFloorDao().queryBuilder().where().eq
                            ("floor_id", 1).and().eq("continent_id", mContinent.id).prepare());
            if (floor != null) {
                displayFloorMetadata(floor);
            }
        } catch (Exception e) {
            Log.w(Application.Constants.LOG_TAG, "Error loading floor from db", e);
        }*/
    }

    private void displayFloorMetadata(Floor floor) {
        mMapView.getOverlayManager().clear();
        //Display POI marker
        for (Region region : floor.regions) {
            for (final Map map : region.maps) {
                List<OverlayItem> poiOverlayItems = new ArrayList<OverlayItem>();
                for (POI poi : map.pois) {
                    GeoPoint p = TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                            mMapView.getZoomLevel(), null);
                    poiOverlayItems.add(new OverlayItem(poi.name, poi.type, p));
                }
                mPOIOverlay = new ItemizedIconOverlay<OverlayItem>(getActivity(), poiOverlayItems,
                        new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                            @Override
                            public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {
                                return true;
                            }

                            @Override
                            public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                                return false;
                            }
                        }) {
                    @Override
                    protected boolean onTap(int index) {
                        POI poi = (POI) map.pois.toArray()[index];
                        Toast.makeText(getActivity(), poi.name + "(" + poi.type + ")",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                };
            }
        }
        mMapView.getOverlayManager().add(mPOIOverlay);
    }

    private LoaderManager.LoaderCallbacks<Floor> mFloorLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Floor>() {
                @Override
                public Loader<Floor> onCreateLoader(int i, Bundle bundle) {
                    return new FloorLoader(getActivity(), mContinent, 1);
                }

                @Override
                public void onLoadFinished(Loader<Floor> floorLoader, Floor floor) {
                    if (floor != null) {
                        displayFloorMetadata(floor);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Floor> floorLoader) {

                }
            };

}
