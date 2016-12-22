package com.example.os.positionin;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;

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
import java.util.LinkedList;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

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
    ArrayList<Edge> mEdges;
    Graph mGraph;
    DijkstraAlgorithm mDijkstra;
    ArrayList<LatLng> arrayPointsOnPath=null;
    ArrayList<Marker> mMarkerList;      //list of markers on graph
    ArrayList<LatLng> points;
    LinkedList<Vertex> path;

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
        mEdges = new ArrayList<Edge>();
        arrayPointsOnPath= new ArrayList<LatLng>();
        points= new ArrayList<LatLng>();
        mMarkerList= new ArrayList<Marker>();


    }

     @Override
    public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;
         mMap.setOnMapClickListener(this);

         loadGraph();
    }

    /**
     * Retrieves the saved graph
     */
    public void loadGraph() {

        LatLng latLng_in = null;
        LatLng latLng_out = null;
        int v_in,v_out, edgeSource = 0, edgeEnd = 0;
        Double dist;

        Float lat, lng;

        ArrayList<Integer> vertex_ids = new ArrayList<Integer>();

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
        double precision =  Math.pow(10, 6);

        //load vertices
        mProjection = verticesListColumns;
      Cursor c1 = getContentResolver().query(NavigationContract.VERTICES_CONTENT_URI, mProjection,mSelectionClause, null, null);
        int lastId;

        LatLng point;
        if (c1 != null && c1.moveToLast()) {
            Log.i(TAG, "flag1");
            lastId = c1.getInt(1); // 1 is the column index
            do {
                if(Integer.parseInt(c1.getString((c1.getColumnIndex("graph_id"))))==lastId) {

                    Log.i(TAG, "flag2");
                    vertex_ids.add(c1.getInt(c1.getColumnIndex("_id")));
                    lat = c1.getFloat(2);
                    lng = c1.getFloat(3);

                    point = new LatLng( ((int)(precision * lat))/precision, ((int)(precision * lng))/precision);

                    Log.i(TAG + "graphid: ", c1.getString(c1.getColumnIndex("graph_id")));
                    Log.i(TAG + "latlng :", String.valueOf(point));
                    mVertices.add(mVertices.size(), new Vertex("Node " + mVertices.size(), point));
                    Marker marker =  mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    mMarkerList.add(marker);
                }
                else
                    break;

            } while (c1.moveToPrevious());

            for(Vertex v: mVertices){
                Log.i(TAG+"mVertices: ", String.valueOf(v.getId()));
            }
        }

        //load edges
        mProjection = edgeListColumns;
        Cursor c2 = getContentResolver().query(NavigationContract.EDGES_CONTENT_URI, mProjection,mSelectionClause, null, null);
        LatLng latLng_in_new = null, latLng_out_new = null;
        if (c2 != null && c2.moveToLast()) {
            Log.i(TAG, "flag3");
            lastId = c2.getInt(1);
            do {
                if(Integer.parseInt(c2.getString((c2.getColumnIndex("graph_id"))))==lastId) {


                    Log.i(TAG +" edge_id ", String.valueOf(c2.getInt(0)));
                    v_in = c2.getInt(c2.getColumnIndex("v_id_in"));
                    v_out = c2.getInt(c2.getColumnIndex("v_id_out"));
                    dist = c2.getDouble(c2.getColumnIndex("distance"));
                    Log.i(TAG + "graphid: ", c2.getString(c2.getColumnIndex("graph_id")));
                    Log.i(TAG +"v_in", String.valueOf(v_in));
                    Log.i(TAG +"v_out", String.valueOf(v_out));
                    Log.i(TAG + "dist" , String.valueOf(dist));

                    for(int id=0; id<vertex_ids.size(); id++){
                        if(v_in==vertex_ids.get(id))
                            edgeSource = id;
                        if(v_out== vertex_ids.get(id))
                            edgeEnd = id;
                    }
                    addLane(String.valueOf(mEdges.size()),edgeSource, edgeEnd, dist);

                    if (c1 != null) {
                        c1.moveToLast();
                        do{
                            if(v_in==Integer.parseInt(c1.getString(c1.getColumnIndex("_id")))){
                                latLng_in = new LatLng(c1.getFloat(2), c1.getFloat(3));
                                Log.i(TAG +" latlng_in ", String.valueOf(latLng_in));
                                latLng_in_new = new LatLng( ((int)(precision * latLng_in.latitude))/precision, ((int)(precision * latLng_in.longitude))/precision);

                            }
                            if(v_out==Integer.parseInt(c1.getString(c1.getColumnIndex("_id")))){
                                latLng_out = new LatLng(c1.getFloat(2), c1.getFloat(3));
                                Log.i(TAG +" latlng_out ", String.valueOf(latLng_out));
                                latLng_out_new = new LatLng( ((int)(precision * latLng_out.latitude))/precision, ((int)(precision * latLng_out.longitude))/precision);

                            }

                        }while(c1.moveToPrevious());
                    }


                    mEdgeEnds.add(new Vertex(String.valueOf(v_in), latLng_in_new));
                    mEdgeEnds.add(new Vertex(String.valueOf(v_out), latLng_out_new));

                    mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                                .add(mEdgeEnds.get(0).getName(), mEdgeEnds.get(1).getName())
                                .width(3)
                                .color(Color.RED)
                                .geodesic(false));

                    mEdgeEnds.clear();

                }

            } while (c2.moveToPrevious());

            for(Edge edge: mEdges){
                Log.i(TAG +" edge id ", edge.getId());
                Log.i(TAG +" source, end ", edge.getSource().getId()+ String.valueOf(edge.getDestination().getId()));
            }
        }
        if (c2 != null && c1 != null) {
            c2.close();
            c1.close();
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
        points.add(marker.getPosition());
        Log.i(TAG, "location selected");
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        if (points.size() >= 2) {
            Log.i(TAG, "point " + String.valueOf(points.get(0)));
            Log.i(TAG, "point " + String.valueOf(points.get(1)));
        }
        return false;
    }

    public void selectStart(View pView){
        mMap.setOnMarkerClickListener(this);
    }

    public void selectEnd(View pView){
        mMap.setOnMarkerClickListener(this);
    }

    public void navigate(View pView){
        mGraph = new Graph("graph", mVertices, mEdges);
        mDijkstra = new DijkstraAlgorithm(mGraph);
        getPathNodes();
        showShortestPath();


    }


    /**
     * implements dijkstra algorithm by finding the appropriate source index from the Marker ArrayList.
     * The getPath method call with the end vertex returns the list of vertices that lie on the shortest route connecting the two locations.
     *
     */
    private void getPathNodes(){

        int source = 0, end = 0;
        for(Marker m: mMarkerList) {
            if (m.getPosition().equals(points.get(0)))
                source = mMarkerList.indexOf(m);

            if(m.getPosition().equals(points.get(1)))
                end=mMarkerList.indexOf(m);
        }
        Log.i(TAG, String.valueOf(source+" "+end));
        Log.i(TAG,"origin: "+mMarkerList.get(source).getPosition()+" dest: "+mMarkerList.get(end).getPosition());

        mDijkstra.execute(new Vertex(mVertices.get(source).getId(),mVertices.get(source).getName()));
        Log.i(TAG,"dijkstra executed");

        path = mDijkstra.getPath(new Vertex(mVertices.get(end).getId(),mVertices.get(end).getName()));

        assertNotNull(path);
        assertTrue(path.size() > 0);

        for (Vertex vertex : path) {
            Log.i(TAG, "path "+String.valueOf(vertex));

        }

        for(Vertex v: path){
            arrayPointsOnPath.add(v.getName());
        }
    }

    /**
     * draws blue polyline for shortest path
     */
    public void showShortestPath(){
        polylineOptions= new PolylineOptions();
        polylineOptions.addAll(arrayPointsOnPath)
                .width(5)
                .color(Color.BLUE)
                .geodesic(false);

        mClickablePolyline= mMap.addPolyline(polylineOptions);

        for (LatLng shortestPath: arrayPointsOnPath){
            Log.i(TAG+" arrayPts lat= ", String.valueOf(shortestPath.latitude));
        }
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


}
