package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.async.FloorLoader;
import de.goddchen.android.gw2.api.async.OverlayLoader;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Floor;

/**
 * Created by Goddchen on 21.06.13.
 */
public class ContinentFragment extends SherlockFragment {

    private static final String EXTRA_CONTINENT = "contintent";

    private Continent mContinent;

    private MapView mMapView;

    private Floor mFloor;

    private ItemizedOverlay<OverlayItem> mLandMarkOverlay, mTaskOverlay, mWaypointOverlay,
            mVistaOverlay, mSkillChallengeOverlay;

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
        mMapView.getController().setZoom(2);
        mMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent scrollEvent) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent zoomEvent) {
                displayFloorMetadata();
                return false;
            }
        });
        return mMapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager()
                .initLoader(Application.Loaders.FLOOR_METADATA, null, mFloorLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Floor floor = Application.getDatabaseHelper().getFloorDao().queryForFirst(
                    Application.getDatabaseHelper().getFloorDao().queryBuilder().where().eq
                            ("floor_id", 1).and().eq("continent_id", mContinent.id).prepare());
            if (floor != null) {
                mFloor = floor;
                displayFloorMetadata();
            }
        } catch (Exception e) {
            Log.w(Application.Constants.LOG_TAG, "Error loading floor from db", e);
        }
    }

    private void displayFloorMetadata() {
        try {
            if (mFloor == null) {
                mFloor = Application.getDatabaseHelper().getFloorDao().queryForFirst(
                        Application.getDatabaseHelper().getFloorDao().queryBuilder()
                                .where().eq("continent_id", mContinent.id).and()
                                .eq("floor_id", 1).prepare());
            }
            if (mTaskOverlay == null ||
                    mVistaOverlay == null ||
                    mWaypointOverlay == null ||
                    mLandMarkOverlay == null ||
                    mSkillChallengeOverlay == null) {
                getLoaderManager().restartLoader(Application.Loaders.FLOOR_MARKERS, null,
                        mOverlayLoaderCallbacks);
            } else {
                mMapView.getOverlayManager().clear();
                //Display POI marker
            /*if (mTaskOverlay == null) {
                List<OverlayItem> taskOverlayItems = new ArrayList<OverlayItem>();
                for (Region region : mFloor.regions) {
                    for (final Map map : region.maps) {
                        for (Task task : map.tasks) {
                            OverlayItem overlayItem = new OverlayItem(task.objective, null,
                                    TileSystem.PixelXYToLatLong((int) task.coord_x, (int) task.coord_y,
                                            mContinent.max_zoom, null));
                            overlayItem.setMarker(getResources().getDrawable(R.drawable.marker_task));
                            taskOverlayItems.add(overlayItem);
                        }
                    }
                }
                mTaskOverlay = new ItemizedIconOverlay<OverlayItem>(
                        getActivity(),
                        taskOverlayItems,
                        null) {
                    @Override
                    protected boolean onTap(int index) {
                        Toast.makeText(getActivity(), getItem(index).getTitle(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                };
            }
            if (mWaypointOverlay == null) {
                List<OverlayItem> waypointsOverlayItems = new ArrayList<OverlayItem>();
                for (Region region : mFloor.regions) {
                    for (final Map map : region.maps) {
                        for (POI poi : map.pois) {
                            OverlayItem overlayItem = new OverlayItem(poi.name, null,
                                    TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                                            mContinent.max_zoom, null));
                            if ("waypoint".equals(poi.type)) {
                                overlayItem.setMarker(getResources().getDrawable(R.drawable.marker_waypoint));
                                waypointsOverlayItems.add(overlayItem);
                            }
                        }
                    }
                }
                mWaypointOverlay = new ItemizedIconOverlay<OverlayItem>(
                        getActivity(),
                        waypointsOverlayItems,
                        null) {
                    @Override
                    protected boolean onTap(int index) {
                        Toast.makeText(getActivity(), getItem(index).getTitle(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                };
            }
            if (mLandMarkOverlay == null) {
                List<OverlayItem> landmarkOverlayItems = new ArrayList<OverlayItem>();
                for (Region region : mFloor.regions) {
                    for (final Map map : region.maps) {
                        for (POI poi : map.pois) {
                            OverlayItem overlayItem = new OverlayItem(poi.name, null,
                                    TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                                            mContinent.max_zoom, null));
                            if ("landmark".equals(poi.type)) {
                                overlayItem.setMarker(getResources().getDrawable(R.drawable
                                        .marker_landmark));
                                landmarkOverlayItems.add(overlayItem);
                            }
                        }
                    }
                }
                mLandMarkOverlay = new ItemizedIconOverlay<OverlayItem>(
                        getActivity(),
                        landmarkOverlayItems,
                        null) {
                    @Override
                    protected boolean onTap(int index) {
                        Toast.makeText(getActivity(), getItem(index).getTitle(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                };
            }
            if (mVistaOverlay == null) {
                List<OverlayItem> vistaOverlayItems = new ArrayList<OverlayItem>();
                for (Region region : mFloor.regions) {
                    for (final Map map : region.maps) {
                        for (POI poi : map.pois) {
                            OverlayItem overlayItem = new OverlayItem(poi.name, null,
                                    TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                                            mContinent.max_zoom, null));
                            if ("vista".equals(poi.type)) {
                                overlayItem.setMarker(getResources().getDrawable(R.drawable
                                        .marker_vista));
                                vistaOverlayItems.add(overlayItem);
                            }
                        }
                    }
                }
                mVistaOverlay = new ItemizedIconOverlay<OverlayItem>(
                        getActivity(),
                        vistaOverlayItems,
                        null) {
                };
            }
            if (mSkillChallengeOverlay == null) {
                List<OverlayItem> skillChallengeOverlayItems = new ArrayList<OverlayItem>();
                for (Region region : mFloor.regions) {
                    for (final Map map : region.maps) {
                        for (SkillChallenge skillChallenge : map.skill_challenges) {
                            OverlayItem overlayItem = new OverlayItem(null, null,
                                    TileSystem.PixelXYToLatLong((int) skillChallenge.coord_x,
                                            (int) skillChallenge.coord_y,
                                            mContinent.max_zoom, null));
                            overlayItem.setMarker(getResources().getDrawable(R.drawable
                                    .marker_skill_challenge));
                            skillChallengeOverlayItems.add(overlayItem);
                        }
                    }
                }
                mSkillChallengeOverlay = new ItemizedIconOverlay<OverlayItem>(
                        getActivity(),
                        skillChallengeOverlayItems,
                        null) {
                };
            }*/
                if (mMapView.getZoomLevel() >= 4) {
                    mMapView.getOverlayManager().add(mLandMarkOverlay);
                    mMapView.getOverlayManager().add(mWaypointOverlay);
                    mMapView.getOverlayManager().add(mTaskOverlay);
                    mMapView.getOverlayManager().add(mVistaOverlay);
                    mMapView.getOverlayManager().add(mSkillChallengeOverlay);
                }
            }
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error displaying markers", e);
        }
        mMapView.invalidate();
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
                        mFloor = floor;
                        displayFloorMetadata();
                    }
                }

                @Override
                public void onLoaderReset(Loader<Floor> floorLoader) {

                }
            };

    private LoaderManager.LoaderCallbacks<List<ItemizedOverlay<OverlayItem>>>
            mOverlayLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<ItemizedOverlay<OverlayItem>>>() {
                @Override
                public Loader<List<ItemizedOverlay<OverlayItem>>> onCreateLoader(int i,
                                                                                 Bundle bundle) {
                    return new OverlayLoader(getActivity(), mContinent, mFloor);
                }

                @Override
                public void onLoadFinished(Loader<List<ItemizedOverlay<OverlayItem>>> listLoader,
                                           List<ItemizedOverlay<OverlayItem>> itemizedOverlays) {
                    if (itemizedOverlays != null) {
                        mTaskOverlay = itemizedOverlays.get(0);
                        mWaypointOverlay = itemizedOverlays.get(1);
                        mLandMarkOverlay = itemizedOverlays.get(2);
                        mVistaOverlay = itemizedOverlays.get(3);
                        mSkillChallengeOverlay = itemizedOverlays.get(4);
                        displayFloorMetadata();
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<ItemizedOverlay<OverlayItem>>> listLoader) {

                }
            };

}
