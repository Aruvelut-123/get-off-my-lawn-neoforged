package draylar.goml.compat.webmap;

import java.awt.Color;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.phys.AABB;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.MapWorld;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.Squaremap;
import xyz.jpenilla.squaremap.api.SquaremapProvider;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.Point;

public class SquaremapCompat extends WebmapCompat {
    private static final SquaremapCompat INSTANCE = new SquaremapCompat();

    // SimpleLayerProvider is registered per SquareMap world, so one layer
    // instance is kept for each registered MapWorld and every marker
    // change is fanned out to all of them.
    private final Set<MapWorld> registeredWorlds = ConcurrentHashMap.newKeySet();
    private final CopyOnWriteArrayList<SimpleLayerProvider> layers = new CopyOnWriteArrayList<>();

    private SquaremapCompat() {
        // SquareMap exposes no apiEnabled hook; everything is driven from
        // WebmapCompat's integration callbacks plus a deferred sync.
    }

    static SquaremapCompat getInstance() {
        return INSTANCE;
    }

    @Override
    protected void onIntegrationRegistered() {
        runOnServer(this::syncLayer);
    }

    @Override
    protected void handleMarkerCreated(ClaimMarker marker) {
        runOnServer(() -> {
            forAllLayers(layer -> applyMarker(layer, marker));
        });
    }

    @Override
    protected void handleMarkerUpdated(ClaimMarker marker) {
        handleMarkerRemoved(marker);
        handleMarkerCreated(marker);
    }

    @Override
    protected void handleMarkerRemoved(ClaimMarker marker) {
        runOnServer(() -> {
            forAllLayers(layer -> layer.removeMarker(keyFor(marker)));
        });
    }

    /**
     * Ensure the GOML layer exists on every registered SquareMap world and
     * re-render all known claim markers.
     */
    private void syncLayer() {
        Squaremap squaremap = safeProvider();
        if (squaremap == null) {
            return;
        }

        for (MapWorld mapWorld : squaremap.mapWorlds()) {
            registerLayer(mapWorld);
        }

        for (ClaimMarker marker : snapshotMarkers()) {
            forAllLayers(layer -> applyMarker(layer, marker));
        }
    }

    private void registerLayer(MapWorld mapWorld) {
        if (!registeredWorlds.add(mapWorld)) {
            return;
        }

        SimpleLayerProvider layer = SimpleLayerProvider.builder(SquaremapCompat::layerLabel)
                .defaultHidden(HIDE_BY_DEFAULT)
                .showControls(true)
                .build();

        try {
            mapWorld.layerRegistry().register(Key.of(MARKER_SET_ID), layer);
        } catch (IllegalArgumentException exception) {
            // The layer key is already in use by another mod — leave it alone
            // and do not track this world.
            registeredWorlds.remove(mapWorld);
            return;
        }

        layers.add(layer);
    }

    private void forAllLayers(java.util.function.Consumer<SimpleLayerProvider> consumer) {
        if (layers.isEmpty()) {
            syncLayer();
        }
        layers.forEach(consumer);
    }

    private void applyMarker(SimpleLayerProvider layer, ClaimMarker marker) {
        AABB box = marker.getClaimBox().minecraftBox();

        MarkerOptions options = MarkerOptions.builder()
                .stroke(true)
                .strokeColor(colorFor(marker))
                .strokeWeight(2)
                .strokeOpacity(0.8D)
                .fill(true)
                .fillColor(colorFor(marker))
                .fillOpacity(0.25D)
                .clickTooltip(marker.renderHtml())
                .build();

        Marker rectangle = Marker.rectangle(
                Point.of(box.minX, box.minZ),
                Point.of(box.maxX, box.maxZ));
        rectangle.markerOptions(options);

        layer.addMarker(keyFor(marker), rectangle);
    }

    // SquareMap keys only accept [a-zA-Z0-9._-], but marker ids contain
    // spaces ("world - 123_456"). Sanitize the id into a valid key.
    private static Key keyFor(ClaimMarker marker) {
        String key = marker.getId().replaceAll("[^a-zA-Z0-9._-]", "_");
        return Key.of(key);
    }

    private static Color colorFor(ClaimMarker marker) {
        return new Color(marker.getColor());
    }

    private static String layerLabel() {
        return MARKER_SET_LABEL;
    }

    private static Squaremap safeProvider() {
        try {
            return SquaremapProvider.get();
        } catch (IllegalStateException exception) {
            return null;
        }
    }
}
