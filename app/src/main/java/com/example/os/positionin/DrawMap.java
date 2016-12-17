package com.example.os.positionin;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.os.navigationsdk.DijkstraAlgorithm;
import com.example.os.navigationsdk.contentprovider.NavigationContract;
import com.example.os.navigationsdk.contentprovider.NavigationDbHelper;
import com.example.os.navigationsdk.model.Edge;
import com.example.os.navigationsdk.model.Graph;
import com.example.os.navigationsdk.model.Vertex;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class DrawMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener , GoogleMap.OnMarkerClickListener {

    final String TAG= "dijkstra";
    private GoogleMap mMap;
    private List<Edge> mEdges;
    Graph mGraph;
    DijkstraAlgorithm mDijkstra;
    private Polyline mClickablePolyline;

    private ArrayList<Vertex> mVertices;
    ArrayList<LatLng> mMarkerArrayList;     // stores source and destination selected
    ArrayList<Marker> mMarkers;      //list of markers on graph
    
    LatLng mOrigin;
    LatLng mDestination;
    int mEdgeStart,mEdgeEnd;
    ArrayList<LatLng> mArrayPointsOnPath=null;
    PolylineOptions polylineOptions;

    ContentValues contentValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMarkerArrayList= new ArrayList<LatLng>();
        mVertices= new ArrayList<Vertex>();
        mEdges = new ArrayList<Edge>();
        mMarkers= new ArrayList<Marker>();
        mArrayPointsOnPath= new ArrayList<LatLng>();



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }


    /**
     * Marks the locations on map and makes calls for saving vertices and edges
     * @param point is added to mMarkerArrayList as  mOrigin or mDestination
     */
    @Override
    public void onMapClick(LatLng point) {

        // Already two locations
        if (mMarkerArrayList.size() > 1) {
            mMarkerArrayList.clear();

        }
        mMarkerArrayList.add(point);

        insertVertex(point);

        Log.i(TAG, "marker at " + point);
        MarkerOptions options = new MarkerOptions();
        options.position(point);

        if (mMarkerArrayList.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (mMarkerArrayList.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        Marker marker = mMap.addMarker(options);
        mMarkers.add(marker);

        saveVertex();

        if (mMarkerArrayList.size() >= 2) {
             mOrigin = mMarkerArrayList.get(0);
            mDestination = mMarkerArrayList.get(1);
            Log.i(TAG,"edge  mOrigin: "+mMarkerArrayList.get(0)+" dest: "+mMarkerArrayList.get(1));
            mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                    .add( mOrigin, mDestination)
                    .width(3)
                    .color(Color.RED)
                    .geodesic(false));

            insertEdge();
            saveEdge();
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    /**
     * adds the selected marker to arraylist as  mOrigin or mDestination and makes call to save edge
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        LatLng markerLocation=marker.getPosition();
        Log.i(TAG,"marker clicked at" +markerLocation);

        // Already two locations
        if (mMarkerArrayList.size() > 1) {
            mMarkerArrayList.clear();

        }
        // Adding new item to the ArrayList
        mMarkerArrayList.add(markerLocation);

        if (mMarkerArrayList.size() >= 2) {
             mOrigin = mMarkerArrayList.get(0);
            mDestination = mMarkerArrayList.get(1);

            mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                    .add( mOrigin, mDestination)
                    .width(3)
                    .color(Color.RED)
                    .geodesic(false));

            insertEdge();
            saveEdge();

        }
        return false;
    }



    /**
     * Calculates the distance between two lat longs.
     * It is called to find the edge length.
     *
     * @param lat1 source latitude
     * @param lon1 source longitude
     * @param lat2 mDestination latitude
     * @param lon2 mDestination longitude
     * @return calculated distance between two lat longs in km
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = sin(toRadians(lat1)) * sin(toRadians(lat2)) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * cos(toRadians(theta));
        dist = Math.acos(dist);
        dist = toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    /**
     * Adds a new Edge object to the ArrayList
     *
     * @param laneId ID of the edge to be added
     * @param sourceLocNo source vertex index of the new edge
     * @param destLocNo  mDestination vertex index of the new edge
     * @param distance length of the edge in km
     */
    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         double distance) {
        Edge lane = new Edge(laneId,mVertices.get(sourceLocNo), mVertices.get(destLocNo), distance);
        mEdges.add(lane);
    }


    /**
     * Inserts param point that is selected while drawing polylines to the ArrayList of vertices
     *
     * @param point the location selected on map as a vertex
     */
    private void insertVertex(LatLng point){

        if (!mVertices.contains(point)) {
            mVertices.add(mVertices.size(), new Vertex(String.valueOf(mVertices.size()), point));
            for (Vertex vertex : mVertices) {
                Log.i(TAG, "vertex: id "+vertex.getId()+" latlng "+vertex.getName());
            }
        }
    }

    /**
     * Check for appropriate start and end indexes for the edge to be inserted and call addLane to insert a new edge.
     */
    public void insertEdge(){

        for(Vertex v: mVertices){
            if(v.getName().equals(mMarkerArrayList.get(0)))
                mEdgeStart = mVertices.indexOf(v);
            if(v.getName().equals(mMarkerArrayList.get(1)))
                mEdgeEnd = mVertices.indexOf(v);
        }
        addLane(String.valueOf(mEdges.size()), mEdgeStart, mEdgeEnd, distance(mVertices.get(mEdgeStart).getName().latitude,mVertices.get(mEdgeStart).getName().longitude, mVertices.get(mEdgeEnd).getName().latitude, mVertices.get(mEdgeEnd).getName().longitude));

        for(Edge edge: mEdges){
            Log.i(TAG,"Edge from: "+edge.getSource()+" to : "+ edge.getDestination()+" dist: "+edge.getWeight());
        }
    }

    /**
     * creates graph and dijkstra objects
     */
    public void createDijkstraObject() {
        mGraph = new Graph("graph", mVertices, mEdges);
        Log.i(TAG+" implemented", mGraph.toString());
        mDijkstra = new DijkstraAlgorithm(mGraph);

        for (Marker marker : mMarkers) {
            Log.i(TAG, marker.getPosition().toString());
        }

    }

    /**
     * Saves a graph in table
     * @param pView
     */
    public void saveGraph(View pView) {
     
        Uri uri;
        Long id;
       createDijkstraObject();
        uri = getContentResolver().insert(NavigationContract.GRAPHS_CONTENT_URI, mGraph.getContentValues());
        Log.i(TAG+" info A:", uri.getLastPathSegment());
        id = Long.valueOf(uri.getLastPathSegment());
    
        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();

        finish();

    }

    /**
     * Returns the ID of last graph saved in table
     * @return int ID of most recent graph stored
     */
    public int lastGraphId(){
        int g_id = 0;
        String[] graphListColumns = {
                NavigationDbHelper.GRAPHS_COL_ID,
                NavigationDbHelper.GRAPHS_COL_NAME
        };
        Cursor cursor = getContentResolver().query(NavigationContract.GRAPHS_CONTENT_URI, graphListColumns, null,null,null);

        if(cursor!=null){
            if(cursor.getCount()==0)
                return 0;

            cursor.moveToLast();
            g_id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        Log.i(TAG +" graph_id ", String.valueOf(g_id));
        return g_id;
    }

    /**
     * Saves a vertex in table
     */
    public void saveVertex(){

        Uri uri;
        int g_id = lastGraphId();
        contentValues = mVertices.get(mVertices.size()-1).getContentValues();
        contentValues.put(NavigationDbHelper.VERTICES_COL_GRAPH_ID, g_id+1);
        uri = getContentResolver().insert(NavigationContract.VERTICES_CONTENT_URI, contentValues);
        Log.i(TAG+" info B:" +(mVertices.size()-1), uri.getLastPathSegment());

        Log.i(TAG, "vertex saved!");
    }

    /**
     * Saves an edge in table
     */
    public void saveEdge(){
        
        int g_id = lastGraphId();
        int vId_in = 0, vId_out = 0;
        String[] verticesListColumns =  {
                NavigationDbHelper.VERTICES_COL_ID,
                NavigationDbHelper.VERTICES_COL_GRAPH_ID,
                NavigationDbHelper.VERTICES_COL_LATITUDE,
                NavigationDbHelper.VERTICES_COL_LONGITUDE
        };
        Cursor c1 = getContentResolver().query(NavigationContract.VERTICES_CONTENT_URI, verticesListColumns,null,null,null);
        if(c1 != null && c1.getCount()>0){
            c1.moveToFirst();
            do{
                Log.i(TAG +"markerpoint ", (String.valueOf(mMarkerArrayList.get(0).latitude)));
                Log.i(TAG +" latlng ", String.valueOf(c1.getFloat(c1.getColumnIndex("latitude"))));
                
                // not getting the correct vertex_in and vertex_out IDs from vertex table if it's entry already exists in the table because the retrieved value cannot be compared to the latlng of the location selected as it is not that accurate
                
                if((String.valueOf(mMarkerArrayList.get(0).latitude))==((String.valueOf(c1.getFloat(c1.getColumnIndex("latitude"))))) && (String.valueOf(mMarkerArrayList.get(0).longitude)) == (String.valueOf(c1.getFloat(c1.getColumnIndex("longitude"))))) {
                    vId_in=c1.getInt(c1.getColumnIndex("_id"));
                    Log.i(TAG +" vId_in ", String.valueOf(vId_in));
                }

                Log.i(TAG +" latlng ", String.valueOf(c1.getFloat(c1.getColumnIndex("latitude"))));
                
                if(mMarkerArrayList.get(1).equals(new LatLng(c1.getLong(c1.getColumnIndex("latitude")),c1.getLong(c1.getColumnIndex("longitude"))))){
                    vId_out=c1.getInt(c1.getColumnIndex("_id"));
                    Log.i(TAG +" vId_out ", String.valueOf(vId_out));
                }

            }while(c1.moveToNext());
            c1.close();
        }

        contentValues = mEdges.get(mEdges.size()-1).getContentValues();
        contentValues.put(NavigationDbHelper.EDGES_COL_GRAPH_ID, g_id+1);
        contentValues.put(NavigationDbHelper.EDGES_COL_VERTEX_ID_IN, vId_in);
        contentValues.put(NavigationDbHelper.EDGES_COL_VERTEX_ID_OUT, vId_out);
        Uri uri = getContentResolver().insert(NavigationContract.EDGES_CONTENT_URI, contentValues);
        Log.i(TAG+ " info C"+ (mEdges.size()-1), uri.getLastPathSegment());
        Log.i(TAG, "Edge saved!");
    }

}
