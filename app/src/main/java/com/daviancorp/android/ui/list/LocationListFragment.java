package com.daviancorp.android.ui.list;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daviancorp.android.data.classes.Location;
import com.daviancorp.android.data.database.LocationCursor;
import com.daviancorp.android.loader.LocationListCursorLoader;
import com.daviancorp.android.mh4udatabase.R;
import com.daviancorp.android.ui.ClickListeners.LocationClickListener;
import com.daviancorp.android.ui.detail.LocationDetailActivity;

public class LocationListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private LocationListCursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.location_grid_fragment, getArguments(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list, parent,
				false);

		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		return new LocationListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		mAdapter = new LocationListCursorAdapter(getActivity(),
				(LocationCursor) cursor);
        setListAdapter(mAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class LocationListCursorAdapter extends CursorAdapter {

		private LocationCursor mLocationCursor;

		public LocationListCursorAdapter(Context context, LocationCursor cursor) {
			super(context, cursor, 0);
			mLocationCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_location_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the monster for the current row
			Location location = mLocationCursor.getLocation();
			AssetManager manager = context.getAssets();

			RelativeLayout listLayout = (RelativeLayout) view
					.findViewById(R.id.listitem);

			// Set up the text view
			TextView locationNameTextView = (TextView) view
					.findViewById(R.id.grid_item_label);
			ImageView locationImage = (ImageView) view
					.findViewById(R.id.grid_item_image);

			String cellText = location.getName();
			String cellImage = "icons_location/" + location.getFileLocation();

			locationNameTextView.setText(cellText);

			// Read a Bitmap from Assets
			try {
				InputStream open = manager.open(cellImage);
				Bitmap bitmap = BitmapFactory.decodeStream(open);
				// Assign the bitmap to an ImageView in this layout
				locationImage.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}

			listLayout.setTag(location.getId());
            listLayout.setOnClickListener(new LocationClickListener(context, location.getId()));
		}
	}

}