package com.example.os.navigationsdk.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by os on 10/29/2016.
 */
public class NavigationDbHelper extends SQLiteOpenHelper {

    //definitions for database
    private static final String DATABASE_NAME = "navigation.db";
    private static final int DATABASE_VERSION = 1;

    //definitions for table names
    public static final String GRAPHS_TABLE_NAME = "graphs";
    public static final String VERTICES_TABLE_NAME = "vertices";
    public static final String EDGES_TABLE_NAME = "edges";

    //definitions for 'graphs' table
    public static final String GRAPHS_COL_ID =  "_id";
    public static final String GRAPHS_COL_NAME =  "name";

    //definitions for 'vertices' table
    public static final String VERTICES_COL_ID =  "_id";
    public static final String VERTICES_COL_GRAPH_ID = "graph_id";
    public static final String VERTICES_COL_LATITUDE = "latitude";
    public static final String VERTICES_COL_LONGITUDE = "longitude";

    //definitions for 'edges' table
    public static final String EDGES_COL_ID = "_id";
    public static final String EDGES_COL_GRAPH_ID = "graph_id";
    public static final String EDGES_COL_VERTEX_ID_IN = "v_id_in";
    public static final String EDGES_COL_VERTEX_ID_OUT = "v_id_out";
    public static final String EDGES_COL_EDGE_DISTANCE = "distance";


    public static final String CREATE_TABLE_GRAPHS = "create table " +
            GRAPHS_TABLE_NAME + "(" +
            GRAPHS_COL_ID + " integer   primary key autoincrement, " +
            GRAPHS_COL_NAME + " text not null" +
            ");";

    public static final String CREATE_TABLE_VERTICES = "create table " +
            VERTICES_TABLE_NAME + "(" +
            VERTICES_COL_ID + " integer   primary key autoincrement, " +
            VERTICES_COL_GRAPH_ID + " integer not null, " +
            VERTICES_COL_LATITUDE + " real not null, " +
            VERTICES_COL_LONGITUDE + " real not null, " +
            "foreign key (" + VERTICES_COL_GRAPH_ID + ") " +
            "references " + GRAPHS_TABLE_NAME + "(" + GRAPHS_COL_ID + ") " +
            "on delete cascade " +
            ");";

    public static final String CREATE_TABLE_EDGES = "create table " +
            EDGES_TABLE_NAME + "(" +
            EDGES_COL_ID + " integer   primary key autoincrement, " +
            EDGES_COL_GRAPH_ID + " integer not null, " +
            EDGES_COL_VERTEX_ID_IN + " integer not null, " +
            EDGES_COL_VERTEX_ID_OUT + " integer not null, " +
            EDGES_COL_EDGE_DISTANCE + " real not null, " +
            "foreign key (" + EDGES_COL_GRAPH_ID + ") " +
            "references " + GRAPHS_TABLE_NAME + "(" + GRAPHS_COL_ID + ") " +
            "on delete cascade " +
            ");";


    public NavigationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL(CREATE_TABLE_GRAPHS);
        db.execSQL(CREATE_TABLE_VERTICES);
        db.execSQL(CREATE_TABLE_EDGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NavigationDbHelper.class.getName(),"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + GRAPHS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VERTICES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EDGES_TABLE_NAME);

        onCreate(db);
    }
}
