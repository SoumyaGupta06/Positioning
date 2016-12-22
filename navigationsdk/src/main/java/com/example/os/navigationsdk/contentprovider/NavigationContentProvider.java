package com.example.os.navigationsdk.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import static com.example.os.navigationsdk.contentprovider.NavigationContract.AUTHORITY;

/**
 * Created by os on 10/31/2016.
 */
public class NavigationContentProvider extends ContentProvider {

    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    //graphs
    public static final String GRAPHS_PATH = "graphs";
    public static final int GRAPHS_PATH_TOKEN = 100;
    public static final String GRAPHS_PATH_FOR_ID = "graphs/*";
    public static final int GRAPHS_PATH_FOR_ID_TOKEN = 200;

    //vertices
    public static final String VERTICES_PATH = "vertices";
    public static final int VERTICES_PATH_TOKEN = 300;
    public static final String VERTICES_PATH_FOR_ID = "vertices/*";
    public static final int VERTICES_PATH_FOR_ID_TOKEN = 400;

    //edges
    public static final String EDGES_PATH = "edges";
    public static final int EDGES_PATH_TOKEN = 500;
    public static final String EDGES_PATH_FOR_ID = "edges/*";
    public static final int EDGES_PATH_FOR_ID_TOKEN = 600;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = AUTHORITY;
        matcher.addURI(authority, GRAPHS_PATH, GRAPHS_PATH_TOKEN);
        matcher.addURI(authority, GRAPHS_PATH_FOR_ID, GRAPHS_PATH_FOR_ID_TOKEN);

        matcher.addURI(authority, VERTICES_PATH, VERTICES_PATH_TOKEN);
        matcher.addURI(authority, VERTICES_PATH_FOR_ID, VERTICES_PATH_FOR_ID_TOKEN);

        matcher.addURI(authority, EDGES_PATH, EDGES_PATH_TOKEN);
        matcher.addURI(authority, EDGES_PATH_FOR_ID, EDGES_PATH_FOR_ID_TOKEN);
        return matcher;
    }

    private NavigationDbHelper mNavigationDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mNavigationDbHelper = new NavigationDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mNavigationDbHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case GRAPHS_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(NavigationDbHelper.GRAPHS_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case GRAPHS_PATH_FOR_ID_TOKEN: {
                int Id = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(NavigationDbHelper.GRAPHS_TABLE_NAME);
                builder.appendWhere(NavigationDbHelper.GRAPHS_COL_ID + "=" + Id);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case VERTICES_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(NavigationDbHelper.VERTICES_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case VERTICES_PATH_FOR_ID_TOKEN: {
                int Id = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(NavigationDbHelper.VERTICES_TABLE_NAME);
                builder.appendWhere(NavigationDbHelper.VERTICES_COL_ID + "=" + Id);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case EDGES_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(NavigationDbHelper.EDGES_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case EDGES_PATH_FOR_ID_TOKEN: {
                int Id = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(NavigationDbHelper.EDGES_TABLE_NAME);
                builder.appendWhere(NavigationDbHelper.EDGES_COL_ID + "=" + Id);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case GRAPHS_PATH_TOKEN:
                return NavigationContract.GRAPHS_CONTENT_TYPE_DIR;
            case GRAPHS_PATH_FOR_ID_TOKEN:
                return NavigationContract.GRAPHS_CONTENT_ITEM_TYPE;
            case VERTICES_PATH_TOKEN:
                return NavigationContract.VERTICES_CONTENT_TYPE_DIR;
            case VERTICES_PATH_FOR_ID_TOKEN:
                return NavigationContract.VERTICES_CONTENT_ITEM_TYPE;
            case EDGES_PATH_TOKEN:
                return NavigationContract.EDGES_CONTENT_TYPE_DIR;
            case EDGES_PATH_FOR_ID_TOKEN:
                return NavigationContract.EDGES_CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mNavigationDbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case GRAPHS_PATH_TOKEN: {
                long id = db.insert(NavigationDbHelper.GRAPHS_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                Log.i("info C:", NavigationContract.GRAPHS_CONTENT_URI.toString());
                return NavigationContract.GRAPHS_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case VERTICES_PATH_TOKEN: {
                long id = db.insert(NavigationDbHelper.VERTICES_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return NavigationContract.VERTICES_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case EDGES_PATH_TOKEN: {
                long id = db.insert(NavigationDbHelper.EDGES_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return NavigationContract.EDGES_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }

            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mNavigationDbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        int rowsDeleted = -1;
        String navigationIdWhereClause;
        switch (token) {
            case (GRAPHS_PATH_TOKEN):
                navigationIdWhereClause = NavigationDbHelper.GRAPHS_COL_ID + "=" + uri.getLastPathSegment();
                rowsDeleted = db.delete(NavigationDbHelper.GRAPHS_TABLE_NAME, selection, selectionArgs);
                break;
            case (GRAPHS_PATH_FOR_ID_TOKEN):
                navigationIdWhereClause = NavigationDbHelper.GRAPHS_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    navigationIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(NavigationDbHelper.GRAPHS_TABLE_NAME, navigationIdWhereClause, selectionArgs);
                break;
            case (VERTICES_PATH_TOKEN):
                navigationIdWhereClause = NavigationDbHelper.VERTICES_COL_ID + "=" + uri.getLastPathSegment();
                rowsDeleted = db.delete(NavigationDbHelper.VERTICES_TABLE_NAME, selection, selectionArgs);
                break;
            case (VERTICES_PATH_FOR_ID_TOKEN):
                navigationIdWhereClause = NavigationDbHelper.VERTICES_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    navigationIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(NavigationDbHelper.VERTICES_TABLE_NAME, navigationIdWhereClause, selectionArgs);
                break;
            case (EDGES_PATH_TOKEN):
                navigationIdWhereClause = NavigationDbHelper.EDGES_COL_ID + "=" + uri.getLastPathSegment();
                rowsDeleted = db.delete(NavigationDbHelper.EDGES_TABLE_NAME, selection, selectionArgs);
                break;
            case (EDGES_PATH_FOR_ID_TOKEN):
                navigationIdWhereClause = NavigationDbHelper.EDGES_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    navigationIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(NavigationDbHelper.EDGES_TABLE_NAME, navigationIdWhereClause, selectionArgs);
                break;
            
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notifying the changes, if there are any
        if (rowsDeleted != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
