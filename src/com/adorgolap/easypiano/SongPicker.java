package com.adorgolap.easypiano;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

public class SongPicker extends Activity {
	private Context context;
	private Activity act;
	ListView lvSongs;
	private ShowcaseView showcase;
	public static final String MyPREFERENCES = "MyPrefs";
	SharedPreferences sharedpreferences;
	public static final String Tutorial = "tutorialKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_picker_layout);
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		if (sharedpreferences.contains(Tutorial)) {
			//notShownTutorial = sharedpreferences.getBoolean(Tutorial, true);
		}
		context = this;
		act = this;
		lvSongs = (ListView) findViewById(R.id.lvSongs);
		new DBLoader().execute(lvSongs);
//		if (notShownTutorial) {
//			showcase = new ShowcaseView.Builder(act).setTarget(Target.NONE)
//					.setContentTitle("Song Selection")
//					.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							showcase.hide();
//						}
//					}).setContentText("Select a song from this menu to play.")
//					.build();
//			showcase.setButtonText("Next");
//		}

	}

	private class DBLoader extends AsyncTask<ListView, Void, Cursor> {
		ListView lv = null;

		@Override
		protected Cursor doInBackground(ListView... params) {
			lv = params[0];
			PianoDB db = new PianoDB(context);
			Cursor c = db.getSongNames();
			return c;
		}

		@Override
		protected void onPostExecute(Cursor c) {
			if (c.moveToFirst()) {
				int i = 1;
				String items[] = new String[c.getCount() + 1];
				items[0] = "Free Play";
				do {
					String songName = c.getString(c.getColumnIndex("songname"));
					String artist = c.getString(c.getColumnIndex("artist"));
					if (artist == null) {
						items[i++] = songName;
					} else {
						if (artist.equals("")) {
							items[i++] = songName;
						} else {
							items[i++] = songName + " by " + artist;
						}
					}

				} while (c.moveToNext());
				lv.setAdapter(new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, items));
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
//						if (notShownTutorial) {
//							if (position == 0) {
//								Toast.makeText(
//										context,
//										"Please select other song than free Play",
//										Toast.LENGTH_LONG).show();
//							} else {
//								notShownTutorial = false;
//								Editor editor = sharedpreferences.edit();
//								editor.putBoolean(Tutorial, notShownTutorial);
//								editor.commit();
//							}
//
//						} else 
						{

							Intent i = new Intent();
							i.putExtra("songNumber", position);
							setResult(RESULT_OK, i);
							finish();
						}
					}
				});
				registerForContextMenu(lv);

			} else {
				Toast.makeText(context, "Dhoner bal", Toast.LENGTH_LONG).show();
			}
		}
	}

	long selectedViewId = -1;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		selectedViewId = v.getId();
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.song_edit, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.viewSequenceData:

			return true;
		case R.id.deleteSong:

		default:
			return false;
		}
	}

}
