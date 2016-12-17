package com.example.os.navigationsdk.model;

import android.content.ContentValues;

import com.example.os.navigationsdk.contentprovider.NavigationDbHelper;

import java.util.List;

/**
 * Created by os on 8/31/2016.
 */
public class Graph {

    private final String name;
    private final List<Vertex> vertexes;
    private final List<Edge> edges;

    public Graph(String name, List<Vertex> vertexes, List<Edge> edges) {
        this.name = name;
        this.vertexes = vertexes;
        this.edges = edges;
    }

    @Override
    public String toString(){
        return vertexes.toString()+" "+edges.toString();
    }
    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }



    public ContentValues getContentValues() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(NavigationDbHelper.GRAPHS_COL_NAME, this.getName());
        return contentValues;
    }

    public String getName() {
        return name;
    }
}
