package com.example.os.positionin;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.os.navigationsdk.DijkstraAlgorithm;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener , GoogleMap.OnMarkerClickListener{

    final String TAG= "dijkstra";
    private GoogleMap mMap;
    private List<Edge> mEdges;
    Graph mGraph;
    DijkstraAlgorithm mDijkstra;
    private Polyline mClickablePolyline;

    private ArrayList<Vertex> mVertices;
    
    ArrayList<LatLng> MarkerPoints;     // stores source and destination selected 
    ArrayList<Marker> mMarkerList;      //list of markers on graph
    LinkedList<Vertex> path;            
    ArrayList<LatLng> points;
    LatLng origin;
    LatLng destination, nearestPointOnEdge;
    int edgeStart,edgeEnd, source, end;
    ArrayList<LatLng> arrayPointsOnPath=null;

    PolylineOptions polylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MarkerPoints= new ArrayList<LatLng>();
        mVertices= new ArrayList<Vertex>();
        points= new ArrayList<LatLng>();
  //      nodes = new ArrayList<Vertex>();
        mEdges = new ArrayList<Edge>();
        mMarkerList= new ArrayList<Marker>();
        arrayPointsOnPath= new ArrayList<LatLng>();





        /*nodes.add(0,new Vertex("Node "+0,new LatLng(27.011086, 71.253937)));
        nodes.add(1,new Vertex("Node "+1,new LatLng(28.618274, 77.208286)));
        nodes.add(2,new Vertex("Node "+2,new LatLng(21.539940, 83.712194)));
        nodes.add(3,new Vertex("Node "+3,new LatLng(24.691932, 80.020788)));
        nodes.add(4,new Vertex("Node "+4,new LatLng(19.388880, 82.965124)));
        nodes.add(5,new Vertex("Node "+5,new LatLng(14.365511, 82.976110)));
        nodes.add(6,new Vertex("Node "+6,new LatLng(12.420487, 78.933142)));
        nodes.add(7,new Vertex("Node "+7,new LatLng(16.430825, 74.714392)));
        nodes.add(8,new Vertex("Node "+8,new LatLng(19.585682, 78.230017)));
        nodes.add(9,new Vertex("Node "+9,new LatLng(22.128922, 68.342322)));
        nodes.add(10,new Vertex("Node "+10,new LatLng(24.269529, 73.879431)));*/

       /* vertices= new ArrayList<LatLng>();
        vertices.add(nodes.get(0).getName());
        vertices.add(nodes.get(1).getName());
        vertices.add(nodes.get(2).getName());
        vertices.add(nodes.get(3).getName());
        vertices.add(nodes.get(4).getName());
        vertices.add(nodes.get(5).getName());
        vertices.add(nodes.get(6).getName());
        vertices.add(nodes.get(7).getName());
        vertices.add(nodes.get(8).getName());
        vertices.add(nodes.get(9).getName());
        vertices.add(nodes.get(10).getName());
*/
       /* addLane("Edge_0", 0, 1, 85);
        addLane("Edge_1", 0, 2, 217);
        addLane("Edge_2", 0, 4, 173);
        addLane("Edge_3", 2, 6, 186);
        addLane("Edge_4", 2, 7, 103);
        addLane("Edge_5", 3, 7, 183);
        addLane("Edge_6", 5, 8, 250);
        addLane("Edge_7", 8, 9, 84);
        addLane("Edge_8", 7, 9, 167);
        addLane("Edge_9", 4, 9, 502);
        addLane("Edge_10", 9, 10, 40);
        addLane("Edge_11", 1, 10, 600);
*/
        // Lets check from location Loc_1 to Loc_10
     /*   graph = new Graph(nodes, edges);
        dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(0));
        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));

        assertNotNull(path);
        assertTrue(path.size() > 0);

        for (Vertex vertex : path) {
            Log.i(TAG, String.valueOf(vertex));

        }
*/

  //      mMap.setOnMapLongClickListener(this);


    }

    /**
     * Adds a new Edge object to the ArrayList
     *
     * @param laneId ID of the edge to be added
     * @param sourceLocNo source vertex index of the new edge
     * @param destLocNo  destination vertex index of the new edge
     * @param distance length of the edge in km
     */
    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         double distance) {
        Edge lane = new Edge(laneId,mVertices.get(sourceLocNo), mVertices.get(destLocNo), distance);
        mEdges.add(lane);
    }

    @Override
    public void onMapClick(LatLng point) {

            // Already two locations
            if (MarkerPoints.size() > 1) {
                MarkerPoints.clear();

            }

            MarkerPoints.add(point);

            insertVertex(point);

            Log.i(TAG, "marker at " + point);
            MarkerOptions options = new MarkerOptions();
            options.position(point);


            if (MarkerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (MarkerPoints.size() == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            Marker marker = mMap.addMarker(options);
            mMarkerList.add(marker);
            if (MarkerPoints.size() >= 2) {
                origin = MarkerPoints.get(0);
                destination = MarkerPoints.get(1);
            Log.i(TAG,"edge origin: "+MarkerPoints.get(0)+" dest: "+MarkerPoints.get(1));
                mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                        .add(origin, destination)
                        .width(3)
                        .color(Color.RED)
                        .geodesic(false));


                insertEdge();
            }



    }


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

                mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                        .add(origin, destination)
                        .width(3)
                        .color(Color.RED)
                        .geodesic(false));

                insertEdge();

        }
        return false;
    }


    /**
     * Inserts param point that is selected while drawing polylines to the ArrayList of vertices
     *
     * @param point the location selected on map as a vertex
     */
    private void insertVertex(LatLng point){

        if (!mVertices.contains(point)) {
            mVertices.add(mVertices.size(), new Vertex("Node " + mVertices.size(), point));
            for (Vertex vertex : mVertices) {
                Log.i(TAG, "vertex: id "+vertex.getId()+" latlng "+vertex.getName());
            }
        }
    }

    /**
     * Check for appropriate start and end indexes for the edge to be inserted and call addLane to insert a new edge.
     */
    private void insertEdge(){

        for(Vertex v: mVertices){
            if(v.getName().equals(MarkerPoints.get(0)))
                edgeStart = mVertices.indexOf(v);

            if(v.getName().equals(MarkerPoints.get(1)))
                edgeEnd = mVertices.indexOf(v);
        }
        addLane(String.valueOf(mEdges.size()), edgeStart, edgeEnd, distance(mVertices.get(edgeStart).getName().latitude,mVertices.get(edgeStart).getName().longitude, mVertices.get(edgeEnd).getName().latitude, mVertices.get(edgeEnd).getName().longitude));

        for(Edge edge: mEdges){
            Log.i(TAG,"Edge from: "+edge.getSource()+" to : "+ edge.getDestination()+" dist: "+edge.getWeight());
        }
    }

    /**
     * This method implements Dijkstra algorithm on the graph on a button click.
     * It implements OnMarkerClickListener and OnMapClickListener that keep track of the source and destination locations selected.
     * If the location selected does not lie on an edge, findNearestPoint is called to get the approximate location on the edge closest to the selected location.
     * Dijkstra algorithm is then implemented on the augmented graph.
     * @param v
     */
    public void implementDijkstraOnClick(View v){
        mGraph = new Graph(mVertices, mEdges);
        mDijkstra = new DijkstraAlgorithm(mGraph);

     /*   for(Edge e: mEdges) {
            LatLng latLng= midOfTwoLocations(e.getSource().getName().latitude,e.getSource().getName().longitude,e.getDestination().getName().latitude,e.getDestination().getName().longitude);
            Marker marker= mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }*/
        for(Marker marker: mMarkerList){
            Log.i(TAG,marker.getPosition().toString());
        }

        Toast.makeText(MapsActivity.this, "Select source and destination vertex", Toast.LENGTH_SHORT).show();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                points.add(marker.getPosition());
                Log.i(TAG,"location selected");
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                if (points.size() >= 2) {
                    Log.i(TAG, "point "+String.valueOf(points.get(0)));
                    Log.i(TAG, "point "+String.valueOf(points.get(1)));
                    mGraph = new Graph(mVertices, mEdges);
                    mDijkstra = new DijkstraAlgorithm(mGraph);
                    getPathNodes();
                    showShortestPath();

                }
                return false;
            }

        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
                    mGraph = new Graph(mVertices, mEdges);
                    mDijkstra = new DijkstraAlgorithm(mGraph);
                    getPathNodes();
                    showShortestPath();

                }

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

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
//            Marker marker = mMap.addMarker(new MarkerOptions().position(midPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
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

}
