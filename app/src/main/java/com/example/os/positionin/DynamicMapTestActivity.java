package com.example.os.positionin;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class DynamicMapTestActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener , GoogleMap.OnMarkerClickListener {

    final String TAG= "dijkstra";
    private GoogleMap mMap;
    ArrayList<Vertex> mVertices;
    private List<Edge> mEdges;
    Graph mGraph;
    DijkstraAlgorithm mDijkstra;
    ArrayList<LatLng> MarkerPoints;     // stores source and destination selected
    ArrayList<LatLng> points;
    LatLng origin;
    LatLng destination, nearestPointOnEdge;
    private Polyline mClickablePolyline;
    PolylineOptions polylineOptions;
    ArrayList<Marker> mMarkerList;      //list of markers on graph
    int edgeStart,edgeEnd, source, end;
    ArrayList<LatLng> arrayPointsOnPath=null;
    LinkedList<Vertex> path;
    ArrayList<Vertex> mEdgeEnds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_map_test);

        mVertices= new ArrayList<Vertex>();
        MarkerPoints= new ArrayList<LatLng>();
        points= new ArrayList<LatLng>();
        mMarkerList= new ArrayList<Marker>();
        mEdges = new ArrayList<Edge>();
        arrayPointsOnPath= new ArrayList<LatLng>();
        mEdgeEnds= new ArrayList<Vertex>();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

    }


    /**
     * Retrieves the saved graph
     */
    public void loadGraph() {

        LatLng latLng_in = null;
        LatLng latLng_out = null;
        int v_in,v_out, edgeSource = 0, edgeEnd = 0;
        Double dist;
        Log.i(TAG,"flag0");
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

        Cursor c1 = getContentResolver().query(NavigationContract.VERTICES_CONTENT_URI,  verticesListColumns,null, null, null);
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

        Cursor c2 = getContentResolver().query(NavigationContract.EDGES_CONTENT_URI, edgeListColumns,null, null, null);
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

    public void dropStart(View pView){
        mMap.setOnMapClickListener(this);
    }

    public void dropEnd(View pView){
        mMap.setOnMapClickListener(this);
    }

    /**
     * Make calls to get the path and display on map
     * @param pView
     */
    public void navigate(View pView){

        mGraph = new Graph("graph", mVertices, mEdges);
        mDijkstra = new DijkstraAlgorithm(mGraph);
        getPathNodes();
        showShortestPath();

    }


    /**
     * Calls method findNearestPoint that returns nearest point on edge from selected latLng
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {

        nearestPointOnEdge = findNearestPoint(latLng,mEdges);
        points.add(nearestPointOnEdge);

        Marker marker = mMap.addMarker(new MarkerOptions().position(nearestPointOnEdge).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        Marker currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMarkerList.add(marker);
        Log.i(TAG,"location selected");

        if (points.size() >= 2) {
            Log.i(TAG, "point "+String.valueOf(points.get(0)));
            Log.i(TAG, "point "+String.valueOf(points.get(1)));

        }

    }



    /**
     *
     *  makes function calls for findNearestPoint and splitEdge appropriately and returns the nearest point on edge
     *
     * @param test selected point on map from where nearest point on edge is to be located
     * @param target List of edge
     * @return lat long of the point on edge that is nearest to test point
     */
    private LatLng findNearestPoint(LatLng test, List<Edge> target) {
        double distance = -1;
        LatLng minimumDistancePoint = test;
        Edge nearestEdge = null;
        if (test == null || target == null) {
            return minimumDistancePoint;
        }

        for (int i = 0; i < target.size(); i++) {
            LatLng point = target.get(i).getSource().getName();


            double currentDistance = distanceToLine(point.latitude,point.longitude, target.get(i).getDestination().getName().latitude,target.get(i).getDestination().getName().longitude, test.latitude,test.longitude);
            Log.i(TAG +"current dist", String.valueOf(currentDistance));
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
                Log.i(TAG+"i: ", String.valueOf(i));
                nearestEdge=target.get(i);
                minimumDistancePoint = nearestPoint(test, point, target.get(i).getDestination().getName());
            }
            Log.i(TAG+"distance: ", String.valueOf(distance));
        }
        Log.i(TAG+ " mindistpoint: ", String.valueOf(minimumDistancePoint));
        splitEdge(nearestEdge, minimumDistancePoint);
        return minimumDistancePoint;
    }


    /**
     * returns the lat long of the location on the edge closest to point p
     * @param p point from where nearest point on edge is to be located
     * @param start edge source
     * @param end edge end
     * @return
     */
    private LatLng nearestPoint(final LatLng p, final LatLng start, final LatLng end){
        LatLng minimumDistancePoint;
        LatLng first, last, midPoint = null;
        first= start;
        last= end;
        while(distance(first.latitude,first.longitude,last.latitude,last.longitude)>0.001){

            Log.i(TAG,"flag");
            midPoint=midOfTwoLocations(first.latitude,first.longitude,last.latitude,last.longitude);
            double d1= distance(first.latitude,first.longitude,p.latitude,p.longitude);
            double d2= distance(last.latitude,last.longitude,p.latitude,p.longitude);

            if(d2<d1){
                first=midPoint;
            }
            if(d1<d2){
                last=midPoint;
            }
            else if(d1==d2){
                break;
            }
        }
        minimumDistancePoint= midPoint;
        return minimumDistancePoint;

    }


    /**
     * Calculates the distance between two lat longs.
     * It is called to find the edge length.
     *
     * @param lat1 source latitude
     * @param lon1 source longitude
     * @param lat2 destination latitude
     * @param lon2 destination longitude
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
     * implements dijkstra algorithm by finding the appropriate source index from the Marker ArrayList.
     * The getPath method call with the end vertex returns the list of vertices that lie on the shortest route connecting the two locations.
     *
     */
    private void getPathNodes(){

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
     * Finds the shortest distance between line(edge) connecting two lat longs and a point.
     *
     * @param lat1 edge source latitude
     * @param lon1 edge source longitude
     * @param lat2 edge destination latitude
     * @param lon2 edge destination longitude
     * @param latp latitude of location from where distance to edge is to be calculated
     * @param lonp longitude of location from where distance to edge is to be calculated
     * @return shortest distance between an edge and a point.
     */
    public Double distanceToLine(double lat1,double lon1,double lat2,double lon2,double latp,double lonp){

        double y = sin(lonp - lon2) * cos(latp);
        double x = cos(lat2) * sin(latp) - sin(lat2) * cos(latp) * cos(latp - lat2);
        double bearing1 = toDegrees(atan2(y, x));
        bearing1 = 360 - (bearing1 + 360 % 360);

        double y2 = sin(lon1 - lon2) * cos(lat1);
        double x2 = cos(lat2) * sin(lat1) - sin(lat2) * cos(lat1) * cos(lat1 - lat2);
        double bearing2 = toDegrees(atan2(y2, x2));
        bearing2 = 360 - (bearing2 + 360 % 360);

        double lat2Rads = toRadians(lat2);
        double latpRads = toRadians(latp);
        double dLon = toRadians(lonp - lon2);

        double distanceAC = acos(sin(lat2Rads) * sin(latpRads)+cos(lat2Rads)*cos(latpRads)*cos(dLon)) * 6371;
        double distance = abs(asin(sin(distanceAC/6371)*sin(toRadians(bearing1)-toRadians(bearing2))) * 6371)*1000;

        Log.d("RAY CAST : dist ", String.valueOf(distance));
        return distance;

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
     * finds the midpoint of two given lat longs
     * @param lat1 latitude of first point
     * @param lon1 longitude of first point
     * @param lat2 latitude of second point
     * @param lon2 longitude of second point
     * @return lat long of calculated midpoint
     */
    public LatLng midOfTwoLocations(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        LatLng latLng= new LatLng(toDegrees(lat3),toDegrees(lon3));
        Log.i(TAG+" mid point: ", String.valueOf(latLng));
        return latLng;
    }


    /**
     * When the nearest point on edge is located, the original edge is split in two (source, minimumDistancePoint), (minimumDistancePoint,end)
     *
     * @param nearestEdge edge closest to the selected location on map
     * @param minimumDistancePoint point on the edge(nearestEdge) that is at shortest distance from location selected on map
     */
    private void splitEdge(Edge nearestEdge, LatLng minimumDistancePoint){

        mVertices.add(new Vertex("Node " + mVertices.size(),minimumDistancePoint));
        mEdges.add(new Edge(String.valueOf(mEdges.size()),nearestEdge.getSource(),mVertices.get(mVertices.size()-1), distance(nearestEdge.getSource().getName().latitude, nearestEdge.getSource().getName().longitude, minimumDistancePoint.latitude,minimumDistancePoint.longitude)));
        mEdges.add(new Edge(nearestEdge.getId(), mVertices.get(mVertices.size()-1), nearestEdge.getDestination(), distance(minimumDistancePoint.latitude,minimumDistancePoint.longitude,nearestEdge.getDestination().getName().latitude,nearestEdge.getDestination().getName().longitude)));
        mEdges.remove(nearestEdge);

        for (Edge edge: mEdges){
            Log.i(TAG+" new edges :", edge.toString());
        }
    }



    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadGraph();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
