package com.example.os.navigationsdk.model;

import android.content.ContentValues;

import com.example.os.navigationsdk.contentprovider.NavigationDbHelper;

/**
 * Created by os on 8/31/2016.
 */
public class Edge {
    private final String id;
    private final Vertex source;
    private final Vertex destination;
    private final double weight;

    public Edge(String id, Vertex source, Vertex destination, double weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }
    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }


    public ContentValues getContentValues() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(NavigationDbHelper.EDGES_COL_EDGE_DISTANCE, this.getWeight());
        return contentValues;
    }
}
