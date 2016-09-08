package com.example.os.positionin;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.os.positionin.model.Edge;
import com.example.os.positionin.model.Vertex;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.os.positionin.model.Graph;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static java.lang.Math.*;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener , GoogleMap.OnMarkerClickListener{

    final String TAG= "dijkstra";
    private GoogleMap mMap;
    private List<Vertex> nodes;
    private List<Edge> edges;
    Graph graph;
    DijkstraAlgorithm dijkstra;
    private Polyline mClickablePolyline;
    private static int prevLoc=0;
    private static int currLoc=0;
    private ArrayList<Vertex> verticesAdded;
    LatLng markerLocation=null;
    ArrayList<LatLng> MarkerPoints;
    ArrayList<Marker> markerList;

    ArrayList<LatLng> points;
    LatLng origin;
    LatLng dest;
    int edgeStart,edgeEnd, source, end;


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
        verticesAdded= new ArrayList<Vertex>();
        points= new ArrayList<LatLng>();
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        markerList= new ArrayList<Marker>();

        Button button= (Button)findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph = new Graph(verticesAdded, edges);
                dijkstra = new DijkstraAlgorithm(graph);

                Toast.makeText(MapsActivity.this, "Select source and destination vertex", Toast.LENGTH_SHORT).show();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        points.add(marker.getPosition());
                        if (points.size() >= 2) {
                            Log.i(TAG, "point "+String.valueOf(points.get(0)));
                            Log.i(TAG, "point "+String.valueOf(points.get(1)));
                            getPathNodes();

                        }
                        return false;
                    }
                });


            }
        });





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

    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        Edge lane = new Edge(laneId,verticesAdded.get(sourceLocNo), verticesAdded.get(destLocNo), duration);
        edges.add(lane);
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
            markerList.add(marker);
            if (MarkerPoints.size() >= 2) {
                origin = MarkerPoints.get(0);
                dest = MarkerPoints.get(1);
            Log.i(TAG,"edge origin: "+MarkerPoints.get(0)+" dest: "+MarkerPoints.get(1));
                mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                        .add(origin, dest)
                        .width(5)
                        .color(Color.RED)
                        .geodesic(true));


                insertEdge();
            }



    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i(TAG,"marker clicked at" +markerLocation);

        markerLocation=marker.getPosition();
        // Already two locations
        if (MarkerPoints.size() > 1) {
            MarkerPoints.clear();

        }
        // Adding new item to the ArrayList
        MarkerPoints.add(markerLocation);

        if (MarkerPoints.size() >= 2) {
            origin = MarkerPoints.get(0);
            dest = MarkerPoints.get(1);

                mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                        .add(origin, dest)
                        .width(5)
                        .color(Color.RED)
                        .geodesic(true));

                insertEdge();

        }
        return false;
    }


    private void insertVertex(LatLng point){
        // add vertex
        if (!verticesAdded.contains(point)) {
            verticesAdded.add(verticesAdded.size(), new Vertex("Node " + verticesAdded.size(), point));
            for (Vertex vertex : verticesAdded) {
                Log.i(TAG, "vertex: id "+vertex.getId()+" latlng "+vertex.getName());
            }
        }
    }

    private void insertEdge(){

        for(Vertex v: verticesAdded){
            if(v.getName().equals(MarkerPoints.get(0)))
                edgeStart = verticesAdded.indexOf(v);

            if(v.getName().equals(MarkerPoints.get(1)))
                edgeEnd = verticesAdded.indexOf(v);
        }
        addLane(String.valueOf(edges.size()), edgeStart, edgeEnd, (int) distance(verticesAdded.get(edgeStart).getName().latitude,verticesAdded.get(edgeStart).getName().longitude, verticesAdded.get(edgeEnd).getName().latitude, verticesAdded.get(edgeEnd).getName().longitude));

        for(Edge edge: edges){
            Log.i(TAG,"Edge from: "+edge.getSource()+" to : "+ edge.getDestination()+" dist: "+edge.getWeight());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
    /*    LatLng sydney = new LatLng(26.576324, 76.338604);
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

   //     verticesAdded.add(0,new Vertex("Node "+0,new LatLng(26.576324, 76.338604)));
    /*    arrayPoints.add(sydney);
        mClickablePolyline = mMap.addPolyline((new PolylineOptions())
                .addAll(vertices)
                .width(5)
                .color(Color.BLUE)
                .geodesic(true));
*/
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = sin(toRadians(lat1)) * sin(toRadians(lat2)) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * cos(toRadians(theta));
        dist = Math.acos(dist);
        dist = toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private void getPathNodes(){

        for(Marker m: markerList) {
            if (m.getPosition().equals(points.get(0)))
                source = markerList.indexOf(m);

            if(m.getPosition().equals(points.get(1)))
                end=markerList.indexOf(m);
        }
        Log.i(TAG, String.valueOf(source+" "+end));
        Log.i(TAG,"origin: "+markerList.get(source).getPosition()+" dest: "+markerList.get(end).getPosition());
        dijkstra.execute(new Vertex(markerList.get(source).getId(),markerList.get(source).getPosition()));
        Log.i(TAG,"dijkstra executed");
        LinkedList<Vertex> path = dijkstra.getPath(new Vertex(markerList.get(end).getId(),markerList.get(end).getPosition()));

        assertNotNull(path);
        assertTrue(path.size() > 0);

        for (Vertex vertex : path) {
            Log.i(TAG, "path "+String.valueOf(vertex));

        }
    }

}
