package de.goddchen.android.gw2.api.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.Arrays;
import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.MapSearchResultAdapter;
import de.goddchen.android.gw2.api.async.FloorLoader;
import de.goddchen.android.gw2.api.async.MapSearchLoader;
import de.goddchen.android.gw2.api.async.OverlayLoader;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.data.Floor;
import de.goddchen.android.gw2.api.data.Map;
import de.goddchen.android.gw2.api.data.POI;
import de.goddchen.android.gw2.api.data.Task;
import de.goddchen.android.gw2.api.fragments.dialogs.ProgressDialogFragment;
import microsoft.mappoint.TileSystem;

/**
 * Created by Goddchen on 21.06.13.
 */
public class ContinentFragment extends SherlockFragment implements View.OnClickListener {

    private static final String EXTRA_CONTINENT = "contintent";

    private static final String EXTRA_EVENT = "event";

    private Continent mContinent;

    private MapView mMapView;

    private Floor mFloor;

    private ItemizedOverlay<OverlayItem> mLandMarkOverlay, mTaskOverlay, mWaypointOverlay,
            mVistaOverlay, mSkillChallengeOverlay, mEventOverlay;

    public static ContinentFragment newInstance(Continent continent) {
        ContinentFragment fragment = new ContinentFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CONTINENT, continent);
        fragment.setArguments(args);
        return fragment;
    }

    public static ContinentFragment newInstance(Continent continent, Event event) {
        ContinentFragment fragment = new ContinentFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CONTINENT, continent);
        args.putSerializable(EXTRA_EVENT, event);
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
        return inflater.inflate(R.layout.fragment_continent, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Floor floor = Application.getDatabaseHelper().getFloorDao().queryForFirst(
                    Application.getDatabaseHelper().getFloorDao().queryBuilder().where().eq
                            ("floor_id", 1).and().eq("continent_id", mContinent.id).prepare());
            if (floor != null) {
                mFloor = floor;
                displayFloorMetadata();
            } else {
                ProgressDialogFragment.newInstance().show(getFragmentManager(), "floor-loading");
            }
        } catch (Exception e) {
            Log.w(Application.Constants.LOG_TAG, "Error loading floor from db", e);
        }
        getLoaderManager()
                .initLoader(Application.Loaders.FLOOR_METADATA, null, mFloorLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.search).setOnClickListener(this);
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
        ((FrameLayout) view.findViewById(R.id.map_wrapper)).addView(mMapView);
        if (getArguments().containsKey(EXTRA_EVENT)) {
            try {
                final Event event = (Event) getArguments().getSerializable(EXTRA_EVENT);
                Map map = Application.getDatabaseHelper().getMapDao().queryForId(event.map_id);
                float percentageX = (float)
                        (event.center_x - map.map_rect_x1) / (map.map_rect_x2 - map.map_rect_x1);
                float percentageY = (float)
                        (event.center_y - map.map_rect_y1) / (map.map_rect_y2 - map.map_rect_y1);
                int continentX = (int) (map.continent_rect_x1 + (map.continent_rect_x2 - map
                        .continent_rect_x1) * percentageX);
                int continentY = (int) (map.continent_rect_y1 + (map.continent_rect_y2 - map
                        .continent_rect_y1) * percentageY);
                OverlayItem overlayItem = new OverlayItem(event.name, null,
                        TileSystem.PixelXYToLatLong(
                                continentX,
                                continentY,
                                mContinent.max_zoom, null));
                overlayItem.setMarker(getResources().getDrawable(R.drawable.marker_event));
                mEventOverlay = new ItemizedIconOverlay<OverlayItem>(getActivity(),
                        Arrays.asList(overlayItem), null) {
                    @Override
                    protected boolean onTap(int index) {
                        Toast.makeText(getActivity(), event.name, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                };
                mMapView.getController().setZoom(6);
                mMapView.getController().animateTo(
                        TileSystem.PixelXYToLatLong(continentX, continentY,
                                mContinent.max_zoom, null));
            } catch (Exception e) {
                Log.e(Application.Constants.LOG_TAG, "Error loading event coordinates", e);
            }
        }
    }

    private void displayFloorMetadata() {
        try {
            if (mEventOverlay != null) {
                mMapView.getOverlayManager().add(mEventOverlay);
            }
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
                getLoaderManager().initLoader(Application.Loaders.FLOOR_MARKERS, null,
                        mOverlayLoaderCallbacks);
            } else {
                if (mMapView.getZoomLevel() >= 4) {
                    if (mMapView.getOverlayManager().size() < 2) {
                        mMapView.getOverlayManager().add(mLandMarkOverlay);
                        mMapView.getOverlayManager().add(mWaypointOverlay);
                        mMapView.getOverlayManager().add(mTaskOverlay);
                        mMapView.getOverlayManager().add(mVistaOverlay);
                        mMapView.getOverlayManager().add(mSkillChallengeOverlay);
                    }
                } else {
                    mMapView.getOverlayManager().remove(mLandMarkOverlay);
                    mMapView.getOverlayManager().remove(mWaypointOverlay);
                    mMapView.getOverlayManager().remove(mTaskOverlay);
                    mMapView.getOverlayManager().remove(mVistaOverlay);
                    mMapView.getOverlayManager().remove(mSkillChallengeOverlay);
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
                    try {
                        Fragment fragment = getFragmentManager().findFragmentByTag
                                ("floor-loading");
                        if (fragment != null && fragment instanceof ProgressDialogFragment) {
                            ((ProgressDialogFragment) fragment).dismiss();
                        }
                    } catch (Exception e) {
                        Log.w(Application.Constants.LOG_TAG, "Error dismissing progress dialog");
                    }
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
                    Log.d(Application.Constants.LOG_TAG, "Loaded " + (itemizedOverlays == null ?
                            0 : itemizedOverlays.size()) + " overlays");
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

    private LoaderManager.LoaderCallbacks<List<List<?>>> mSearchLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<List<?>>>() {
                @Override
                public Loader<List<List<?>>> onCreateLoader(int i, Bundle bundle) {
                    return new MapSearchLoader(getActivity(), mContinent,
                            ((EditText) getView().findViewById(R.id.query)).getText().toString());
                }

                @Override
                public void onLoadFinished(Loader<List<List<?>>> listLoader, List<List<?>> lists) {
                    try {
                        Fragment fragment = getFragmentManager().findFragmentByTag
                                ("loading-search");
                        if (fragment != null && fragment instanceof ProgressDialogFragment) {
                            ((ProgressDialogFragment) fragment).dismiss();
                        }
                    } catch (Exception e) {
                        Log.w(Application.Constants.LOG_TAG, "Error dismissing progress dialog");
                    }
                    if (lists != null) {
                        List<POI> pois = (List<POI>) lists.get(0);
                        List<Task> tasks = (List<Task>) lists.get(1);
                        if (pois.size() + tasks.size() == 0) {
                            Toast.makeText(getActivity(), R.string.toast_no_search_results,
                                    Toast.LENGTH_SHORT).show();
                        } else if (pois.size() + tasks.size() == 1) {
                            if (pois.size() == 1) {
                                POI poi = pois.get(0);
                                Toast.makeText(getActivity(), poi.name, Toast.LENGTH_SHORT).show();
                                if (mMapView.getZoomLevel() < 4) {
                                    mMapView.getController().setZoom(4);
                                }
                                mMapView.getController().animateTo(TileSystem.PixelXYToLatLong((int)
                                        poi.coord_x, (int) poi.coord_y, mContinent.max_zoom, null));
                            } else if (tasks.size() == 1) {
                                Task task = tasks.get(0);
                                Toast.makeText(getActivity(), task.objective, Toast.LENGTH_SHORT).show();
                                if (mMapView.getZoomLevel() < 4) {
                                    mMapView.getController().setZoom(4);
                                }
                                mMapView.getController().animateTo(TileSystem.PixelXYToLatLong((int)
                                        task.coord_x, (int) task.coord_y, mContinent.max_zoom, null));
                            }
                        } else if (pois.size() + tasks.size() > 1) {
                            final MapSearchResultAdapter adapter = new MapSearchResultAdapter
                                    (getActivity(), pois, tasks);
                            new AlertDialog.Builder(getActivity())
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Object item = adapter.getItem(which);
                                            if (item instanceof POI) {
                                                Toast.makeText(getActivity(), ((POI) item).name,
                                                        Toast.LENGTH_SHORT).show();
                                                if (mMapView.getZoomLevel() < 4) {
                                                    mMapView.getController().setZoom(4);
                                                }
                                                mMapView.getController().animateTo(TileSystem.PixelXYToLatLong((int)
                                                        ((POI) item).coord_x, (int) ((POI) item).coord_y,
                                                        mContinent.max_zoom, null));
                                            } else if (item instanceof Task) {
                                                Toast.makeText(getActivity(), ((Task) item).objective,
                                                        Toast.LENGTH_SHORT).show();
                                                if (mMapView.getZoomLevel() < 4) {
                                                    mMapView.getController().setZoom(4);
                                                }
                                                mMapView.getController().animateTo(TileSystem.PixelXYToLatLong((int)
                                                        ((Task) item).coord_x, (int) ((Task) item).coord_y,
                                                        mContinent.max_zoom,
                                                        null));
                                            }
                                        }
                                    })
                                    .show();
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<List<?>>> listLoader) {

                }
            };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search) {
            EditText query = (EditText) getView().findViewById(R.id.query);
            if (query.length() > 0) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(query.getWindowToken(), 0);
                getLoaderManager().restartLoader(Application.Loaders.MAP_SEARCH, null,
                        mSearchLoaderCallbacks);
                ProgressDialogFragment.newInstance().show(getFragmentManager(),
                        "loading-search");
            }
        }
    }
}
