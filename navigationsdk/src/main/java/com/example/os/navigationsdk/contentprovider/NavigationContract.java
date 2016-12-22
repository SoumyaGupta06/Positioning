package com.example.os.navigationsdk.contentprovider;

import android.net.Uri;

/**
 * Created by os on 10/31/2016.
 */
public class NavigationContract {

    public static final String AUTHORITY = "com.example.os.navigationsdk.navigation.provider";

    //graphs
    public static final String GRAPHS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.navigation.graphs";
    public static final String GRAPHS_CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.navigation.graphs";

    public static final Uri GRAPHS_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/graphs");

    //vertices
    public static final String VERTICES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.navigation.vertices";
    public static final String VERTICES_CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.navigation.vertices";

    public static final Uri VERTICES_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/vertices");

    //edges
    public static final String EDGES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.navigation.edges";
    public static final String EDGES_CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.navigation.edges";

    public static final Uri EDGES_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/edges");

}
