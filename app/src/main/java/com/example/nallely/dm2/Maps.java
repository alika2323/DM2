package com.example.nallely.dm2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class Maps extends AppCompatActivity {


    private MapView map;
    private MapController mapC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        GeoPoint xalapa= new GeoPoint( 19.5420361,-96.9549487 );
        map=(MapView) findViewById( R.id.openmapview );
        map.setBuiltInZoomControls( true );
        mapC=(MapController) map.getController();
        mapC.setCenter( xalapa );
        mapC.setZoom( 12 );

        map.setMultiTouchControls( true );

        ArrayList<OverlayItem> puntos = new ArrayList<OverlayItem>();
        puntos.add(new OverlayItem("Madrid", "Ciudad de Madrid", xalapa));

        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> tap = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                return false;
            }
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }
        };

        ItemizedOverlayWithFocus<OverlayItem> capa = new ItemizedOverlayWithFocus<OverlayItem>(this, puntos, tap);
        capa.setFocusItemsOnTap(true);
        map.getOverlays().add(capa);


        final MyLocationNewOverlay myLocationoverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        map.getOverlays().add(myLocationoverlay); //No añadir si no quieres una marca
        myLocationoverlay.enableMyLocation();
        myLocationoverlay.runOnFirstFix(new Runnable() {
            public void run() {
                System.out.println("funcion");
                System.out.println("location-> ");
                System.out.println(myLocationoverlay.getMyLocation());
                System.out.println("aqui");
                mapC.animateTo(myLocationoverlay.getMyLocation());
                System.out.println("despues");
            }
        });


    }
}
