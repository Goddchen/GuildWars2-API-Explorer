package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Floor;
import de.goddchen.android.gw2.api.data.Map;
import de.goddchen.android.gw2.api.data.POI;
import de.goddchen.android.gw2.api.data.Region;
import de.goddchen.android.gw2.api.data.SkillChallenge;
import de.goddchen.android.gw2.api.data.Task;
import microsoft.mappoint.TileSystem;

/**
 * Created by Goddchen on 22.06.13.
 */
public class OverlayLoader extends FixedAsyncTaskLoader<List<ItemizedOverlay<OverlayItem>>> {
    private Floor mFloor;

    private Continent mContinent;

    public OverlayLoader(Context context, Continent continent, Floor floor) {
        super(context);
        mFloor = floor;
        mContinent = continent;
    }

    @Override
    public List<ItemizedOverlay<OverlayItem>> loadInBackground() {
        try {
            List<ItemizedOverlay<OverlayItem>> overlays = new
                    ArrayList<ItemizedOverlay<OverlayItem>>();
            List<OverlayItem> taskOverlayItems = new ArrayList<OverlayItem>();
            for (Region region : mFloor.regions) {
                for (final Map map : region.maps) {
                    for (Task task : map.tasks) {
                        OverlayItem overlayItem = new OverlayItem(task.objective, null,
                                TileSystem.PixelXYToLatLong((int) task.coord_x, (int) task.coord_y,
                                        mContinent.max_zoom, null));
                        overlayItem.setMarker(getContext().getResources()
                                .getDrawable(R.drawable.marker_task));
                        taskOverlayItems.add(overlayItem);
                    }
                }
            }
            overlays.add(new ItemizedIconOverlay<OverlayItem>(
                    getContext(),
                    taskOverlayItems,
                    null) {
                @Override
                protected boolean onTap(int index) {
                    Toast.makeText(getContext(), getItem(index).getTitle(),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            List<OverlayItem> waypointsOverlayItems = new ArrayList<OverlayItem>();
            for (Region region : mFloor.regions) {
                for (final Map map : region.maps) {
                    for (POI poi : map.pois) {
                        OverlayItem overlayItem = new OverlayItem(poi.name, null,
                                TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                                        mContinent.max_zoom, null));
                        if ("waypoint".equals(poi.type)) {
                            overlayItem.setMarker(getContext().getResources()
                                    .getDrawable(R.drawable.marker_waypoint));
                            waypointsOverlayItems.add(overlayItem);
                        }
                    }
                }
            }
            overlays.add(new ItemizedIconOverlay<OverlayItem>(
                    getContext(),
                    waypointsOverlayItems,
                    null) {
                @Override
                protected boolean onTap(int index) {
                    Toast.makeText(getContext(), getItem(index).getTitle(),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            List<OverlayItem> landmarkOverlayItems = new ArrayList<OverlayItem>();
            for (Region region : mFloor.regions) {
                for (final Map map : region.maps) {
                    for (POI poi : map.pois) {
                        OverlayItem overlayItem = new OverlayItem(poi.name, null,
                                TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                                        mContinent.max_zoom, null));
                        if ("landmark".equals(poi.type)) {
                            overlayItem.setMarker(getContext().getResources()
                                    .getDrawable(R.drawable.marker_landmark));
                            landmarkOverlayItems.add(overlayItem);
                        }
                    }
                }
            }
            overlays.add(new ItemizedIconOverlay<OverlayItem>(
                    getContext(),
                    landmarkOverlayItems,
                    null) {
                @Override
                protected boolean onTap(int index) {
                    Toast.makeText(getContext(), getItem(index).getTitle(),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            List<OverlayItem> vistaOverlayItems = new ArrayList<OverlayItem>();
            for (Region region : mFloor.regions) {
                for (final Map map : region.maps) {
                    for (POI poi : map.pois) {
                        OverlayItem overlayItem = new OverlayItem(poi.name, null,
                                TileSystem.PixelXYToLatLong((int) poi.coord_x, (int) poi.coord_y,
                                        mContinent.max_zoom, null));
                        if ("vista".equals(poi.type)) {
                            overlayItem.setMarker(getContext().getResources()
                                    .getDrawable(R.drawable.marker_vista));
                            vistaOverlayItems.add(overlayItem);
                        }
                    }
                }
            }
            overlays.add(new ItemizedIconOverlay<OverlayItem>(
                    getContext(),
                    vistaOverlayItems,
                    null) {
            });
            List<OverlayItem> skillChallengeOverlayItems = new ArrayList<OverlayItem>();
            for (Region region : mFloor.regions) {
                for (final Map map : region.maps) {
                    for (SkillChallenge skillChallenge : map.skill_challenges) {
                        OverlayItem overlayItem = new OverlayItem(null, null,
                                TileSystem.PixelXYToLatLong((int) skillChallenge.coord_x,
                                        (int) skillChallenge.coord_y,
                                        mContinent.max_zoom, null));
                        overlayItem.setMarker(getContext().getResources()
                                .getDrawable(R.drawable.marker_skill_challenge));
                        skillChallengeOverlayItems.add(overlayItem);
                    }
                }
            }
            overlays.add(new ItemizedIconOverlay<OverlayItem>(
                    getContext(),
                    skillChallengeOverlayItems,
                    null) {
            });
            return overlays;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading overlays", e);
            return null;
        }
    }
}
