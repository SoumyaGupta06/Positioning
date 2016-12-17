package com.example.os.positionin;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.CursorAdapter;

import com.example.os.navigationsdk.contentprovider.NavigationContract;
import com.example.os.navigationsdk.contentprovider.NavigationDbHelper;
import com.example.os.navigationsdk.model.Vertex;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class StaticMapTestActivity  extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener , GoogleMap.OnMarkerClickListener {

    CursorAdapter mCursorAdapter;
    String[] mProjection;
    String mSelectionClause;
    LatLng destination, origin;
    ArrayList<LatLng> MarkerPoints;     // stores source and destination selected
    private GoogleMap mMap;
    final String TAG= "dijkstra";
    PolylineOptions polylineOptions;
    private Polyline mClickablePolyline;
    ArrayList<Vertex> mVertices;
    ArrayList<Vertex> mEdgeEnds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_map_test);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        MarkerPoints= new ArrayList<LatLng>();
        mEdgeEnds= new ArrayList<Vertex>();
        mSelectionClause = null;
        mVertices= new ArrayList<Vertex>();

        loadGraph();

    }

     @Override
    public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;
         mMap.setOnMapClickListener(this);
         mMap.addMarker(new MarkerOptions().position(new LatLng(-33.870781, 151.194218)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

    }

    /**
     * Retrieves the saved graph
     */
    public void loadGraph() {

        LatLng latLng_in = null;
        LatLng latLng_out = null;
        int v_in,v_out;
        Double dist;
        Cursor cursor = null;
        Log.i(TAG,"flag0");
        long lat;
        long lng;
        ArrayList<String> vertex_ids = new ArrayList<String>();

        String[] graphListColumns = {
                NavigationDbHelper.GRAPHS_COL_ID,
                NavigationDbHelper.GRAPHS_COL_NAME
        };

        String[] verticesListColumns =  {
                NavigationDbHelper.VERTICES_COL_ID,
                NavigationDbHelper.VERTICES_COL_GRAPH_ID,
                NavigationDbHelper.VERTICES_COL_LATITUDE,
                NavigationDbHelper.VERTICES_COL_LONGITUDE
        };

        String[] edgeListColumns = {
                NavigationDbHelper.EDGES_COL_ID,
                NavigationDbHelper.EDGES_COL_GRAPH_ID,
                NavigationDbHelper.EDGES_COL_VERTEX_ID_IN,
                NavigationDbHelper.EDGES_COL_VERTEX_ID_OUT,
                NavigationDbHelper.EDGES_COL_EDGE_DISTANCE
        };

        mProjection = verticesListColumns;
      Cursor c1 = getContentResolver().query(NavigationContract.VERTICES_CONTENT_URI, mProjection,mSelectionClause, null, null);
        Long lastId;
        if (c1 != null && c1.moveToLast()) {
            Log.i(TAG, "flag1");
            lastId = c1.getLong(1); // 1 is the column index
            do {
                if(Integer.parseInt(c1.getString((c1.getColumnIndex("graph_id"))))==lastId) {

                    Log.i(TAG, "flag2");
                    vertex_ids.add(c1.getString(c1.getColumnIndex("_id")));
                    lat = c1.getLong(2);
                    lng = c1.getLong(3);
                    mVertices.add(mVertices.size(), new Vertex("Node " + mVertices.size(), new LatLng(lat,lng)));
                    Log.i(TAG + "id: ", c1.getString(c1.getColumnIndex("graph_id")));
                    Log.i(TAG + "latlng :", String.valueOf(new LatLng(lat, lng)));
                    if(mMap==null) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                mMap = googleMap;
                            }
                        });
                    }
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                }
                else
                    break;

            } while (c1.moveToPrevious());

            for(Vertex v: mVertices){
                Log.i(TAG+"mVertices: ", String.valueOf(v.getName()));
            }
        }


        mProjection = edgeListColumns;
        Cursor c2 = getContentResolver().query(NavigationContract.EDGES_CONTENT_URI, mProjection,mSelectionClause, null, null);

        if (c2 != null && c2.moveToLast()) {
            Log.i(TAG, "flag3");
            lastId = c2.getLong(1);
            do {
                if(Integer.parseInt(c2.getString((c2.getColumnIndex("graph_id"))))==lastId) {


                    Log.i(TAG +" edge_id ", String.valueOf(c2.getInt(0)));
                    v_in = c2.getInt(c2.getColumnIndex("v_id_in"));
                    v_out = c2.getInt(c2.getColumnIndex("v_id_out"));
                    dist = c2.getDouble(c2.getColumnIndex("distance"));
                    Log.i(TAG + "id: ", c2.getString(c2.getColumnIndex("graph_id")));
                    Log.i(TAG +"v_in", String.valueOf(v_in));
                    Log.i(TAG +"v_out", String.valueOf(v_out));
                    Log.i(TAG + "dist" , String.valueOf(dist));


                    if (c1 != null) {
                        c1.moveToLast();
                        do{
                            if(v_in==Integer.parseInt(c1.getString(c1.getColumnIndex("_id")))){
                                latLng_in = new LatLng(c1.getLong(2), c1.getLong(3));
                                Log.i(TAG +" latlng_in ", String.valueOf(latLng_in));

                            }
                            if(v_out==Integer.parseInt(c1.getString(c1.getColumnIndex("_id")))){
                                latLng_out = new LatLng(c1.getLong(2), c1.getLong(3));
                                Log.i(TAG +" latlng_out ", String.valueOf(latLng_out));

                            }

                        }while(c1.moveToPrevious());
                    }

                    /*
                    if(cursor != null && cursor.getCount()>0) {
                        Log.i(TAG + "cursor count_in", String.valueOf(cursor.getCount()));
                        cursor.moveToFirst();
                        latLng_in = new LatLng(Double.parseDouble(String.valueOf((cursor.getLong(2)))), Double.parseDouble(String.valueOf((cursor.getLong(3)))));

                        Log.i(TAG +" latlng_in ", String.valueOf(latLng_in));
                    }
                    cursor = getContentResolver().query(NavigationContract.VERTICES_CONTENT_URI, verticesListColumns, v_out + "=" + NavigationDbHelper.VERTICES_COL_ID, null, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        Log.i(TAG + "cursorcount_out", String.valueOf(cursor.getCount()));
                        cursor.moveToFirst();
                        latLng_out = new LatLng(Double.parseDouble(String.valueOf((cursor.getLong(2)))), Double.parseDouble(String.valueOf((cursor.getLong(3)))));

                        Log.i(TAG +" latlng_out ", String.valueOf(latLng_out));
                    }
                    */

                    mEdgeEnds.add(new Vertex(String.valueOf(v_in), latLng_in));
                    mEdgeEnds.add(new Vertex(String.valueOf(v_out), latLng_out));

                    if (mMap==null){
                        MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map1);
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                mMap = googleMap;
                            }
                        });
                    }
                    mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                                .add(mEdgeEnds.get(0).getName(), mEdgeEnds.get(1).getName())
                                .width(3)
                                .color(Color.RED)
                                .geodesic(false));

                    mEdgeEnds.clear();

                }

            } while (c2.moveToPrevious());
        }
        if (c2 != null && cursor != null) {
            c2.close();
            cursor.close();
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


    /**
     * selects origin or destination markers
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng markerLocation=marker.getPosition();
        Log.i(TAG,"marker clicked at" +markerLocation);

        // Already two locations
        if (MarkerPoints.size() > 1) {
            MarkerPoints.clear();

        }
        // Adding new item to the ArrayList
        MarkerPoints.add(markerLocation);

        if (MarkerPoints.size() >= 2) {
            origin = MarkerPoints.get(0);
            destination = MarkerPoints.get(1);

        }
        return false;
    }

    public void selectStart(){
        mMap.setOnMarkerClickListener(this);
    }

    public void selectEnd(){
        mMap.setOnMarkerClickListener(this);
    }

    public void navigate(){

    }


}
