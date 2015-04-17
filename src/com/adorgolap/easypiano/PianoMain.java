package com.adorgolap.easypiano;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class PianoMain extends Activity implements OnClickListener {
	private static final int PICK_TO_PLAY = 0;
	private static final int PICK_TO_EDIT = 1;
	public static final String MyPREFERENCES = "MyPrefs";
	SharedPreferences sharedpreferences;
	public static final String Tutorial = "tutorialKey";
	private static final String TAG = "menu";
	Context context;
	TextView tvNotes, tvSongName, tvSong;
	Button ba, bab, bb, bbb, bc, bd, bdb, be, beb, bf, bg, bgb, bHigh, bLow;
	int test_sound;
	SoundPool sp;
	String[] notes;
	float leftVolume;
	float rightVolume;
	int priority = 1;
	int loop = 0;
	private static final float rate = 1.0f;
	String songName = "Free Play";
	String songNotes[] = new String[1];
	String songWords[] = new String[1];
	ArrayList<String> fullSongNotes = new ArrayList<String>();
	ArrayList<String> fullSongWords = new ArrayList<String>();
	ArrayList<Integer> fullSongOffset = new ArrayList<Integer>();
	ArrayList<Integer> load = new ArrayList<Integer>();
	int songOffset[] = new int[1];
	private boolean play;
	int leftThreshold = 1;
	int rightThreshold = 10;
	int ratePointer = 0;
	SparseIntArray sound = new SparseIntArray(36);
	private ShowcaseView showcase;
	private ArrayList<Target> t = new ArrayList<Target>();
	private int song_id = 0;
	int tutorialCounter = 0;

	HashMap<String, Button> keyToButtonMap = new HashMap<String, Button>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piano_main);
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		if (sharedpreferences.contains(Tutorial)) {
			notShownTutorial = sharedpreferences.getBoolean(Tutorial, true);
		}
		initialize();
		sutupAudioProperties();
		loadSounds();
		resetCurrentSong();
	}

	private void sutupAudioProperties() {
		leftVolume = 1;
		rightVolume = 1;
	}

	private void loadSounds() {
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		Soundloader soundLoader = new Soundloader();
		soundLoader.execute(R.raw.ab_low, R.raw.a_low, R.raw.bb_low,
				R.raw.b_low, R.raw.c_low, R.raw.db_low, R.raw.d_low,
				R.raw.eb_low, R.raw.e_low, R.raw.f_low, R.raw.gb_low,
				R.raw.g_low, R.raw.ab, R.raw.a, R.raw.bb, R.raw.b, R.raw.c,
				R.raw.db, R.raw.d, R.raw.eb, R.raw.e, R.raw.f, R.raw.gb,
				R.raw.g, R.raw.ab_high, R.raw.a_high, R.raw.bb_high,
				R.raw.b_high, R.raw.c_high, R.raw.db_high, R.raw.d_high,
				R.raw.eb_high, R.raw.e_high, R.raw.f_high, R.raw.gb_high,
				R.raw.g_high);
	}

	private void initialize() {
		context = this;
		ba = (Button) findViewById(R.id.ba);
		bb = (Button) findViewById(R.id.bb);
		bc = (Button) findViewById(R.id.bc);
		bd = (Button) findViewById(R.id.bd);
		be = (Button) findViewById(R.id.be);
		be.setFocusable(true);
		bf = (Button) findViewById(R.id.bf);
		bg = (Button) findViewById(R.id.bg);
		bab = (Button) findViewById(R.id.bab);
		bbb = (Button) findViewById(R.id.bbb);
		bdb = (Button) findViewById(R.id.bdb);
		beb = (Button) findViewById(R.id.beb);
		beb.setFocusable(true);
		bgb = (Button) findViewById(R.id.bgb);
		bHigh = (Button) findViewById(R.id.bHigh);
		bLow = (Button) findViewById(R.id.bLow);

		ba.setOnClickListener(this);
		bb.setOnClickListener(this);
		bc.setOnClickListener(this);
		bd.setOnClickListener(this);
		be.setOnClickListener(this);
		bf.setOnClickListener(this);
		bg.setOnClickListener(this);
		bab.setOnClickListener(this);
		bbb.setOnClickListener(this);
		bdb.setOnClickListener(this);
		beb.setOnClickListener(this);
		bgb.setOnClickListener(this);
		bHigh.setOnClickListener(this);
		bLow.setOnClickListener(this);

		keyToButtonMap.put("A", ba);
		keyToButtonMap.put("Ab", bab);
		keyToButtonMap.put("B", bb);
		keyToButtonMap.put("Bb", bbb);
		keyToButtonMap.put("C", bc);
		keyToButtonMap.put("D", bd);
		keyToButtonMap.put("Db", bdb);
		keyToButtonMap.put("E", be);
		keyToButtonMap.put("Eb", beb);
		keyToButtonMap.put("F", bf);
		keyToButtonMap.put("G", bg);
		keyToButtonMap.put("Gb", bgb);

		tvNotes = (TextView) findViewById(R.id.tvNote);
		tvSongName = (TextView) findViewById(R.id.tvSongName);
		tvSong = (TextView) findViewById(R.id.tvSong);

		tvNotes.setText("");
		tvSongName.setText("No Song Selected");
		if (notShownTutorial) {
			t.add(new ViewTarget(R.id.be, this));
			t.add(new ViewTarget(R.id.beb, this));
			t.add(new ViewTarget(R.id.be, this));
			t.add(new ViewTarget(R.id.beb, this));
			t.add(new ViewTarget(R.id.be, this));
			t.add(new ViewTarget(R.id.bb, this));
			t.add(new ViewTarget(R.id.bd, this));
			t.add(new ViewTarget(R.id.bc, this));
			t.add(new ViewTarget(R.id.ba, this));
			
			t.add(new ViewTarget(R.id.bHigh, this));
			t.add(new ViewTarget(R.id.bLow, this));
			
			t.add(new ActionItemTarget(this, R.id.picker));
			t.add(new ViewTarget(R.id.ba, this)); // false event trp
			showcase = new ShowcaseView.Builder(this).setTarget(Target.NONE)
					.setOnClickListener(this).setContentTitle("Tutorial")
					.setContentText("Hello everyone. Please click the next button to began the tutorial.").build();
			showcase.setButtonText("Next");
		}
	}

	int totalPlayedNode = 0;
	int localNotePointer = 0;
	int offsetPointer = 0;
	String[] currentNotes;
	String songnotes;
	int loadPointer = 0;
	private boolean notShownTutorial = true;
	private int  bgColor;

	@Override
	public void onClick(View v) {
		int offset = 0;
		if (song_id == 0) {
			if (notShownTutorial) {
				offset = 12;
				showTutorial();
			} else {
				if (bHigh.isPressed()) {
					offset = 12;
				} else if (bLow.isPressed()) {
					offset = -12;
				}
			}
			play = true;
		} else {
			currentNotes = songnotes.split(" ");
			if (currentNotes[localNotePointer].equals(((Button) v).getText()
					.toString())) {
				play = true;
				if(((Button)v).isPressed())
				{
					(( Button)v).setBackgroundResource(R.drawable.button_white_clicked);
				}
				(( Button)v).setBackgroundResource(bgColor);
				totalPlayedNode++;
				offset = fullSongOffset.get(offsetPointer);
				offsetPointer = (offsetPointer + 1) % fullSongOffset.size();
				localNotePointer++;
				if (localNotePointer != currentNotes.length) {
					manipulateNoteTextView(localNotePointer);
				}
				// Log.d("info", "load "+loadPointer+"");
				if (localNotePointer == currentNotes.length) {
					songnotes = "";
					loadPointer = (loadPointer + 1) % load.size();
					if (loadPointer == 0) {
						totalPlayedNode = 0;
					}
					for (int i = totalPlayedNode; i < (load.get(loadPointer) + totalPlayedNode); i++) {
						songnotes += fullSongNotes.get(i) + " ";
					}
					localNotePointer = 0;
					currentNotes = songnotes.split(" ");
					
					manipulateNoteTextView(0);
					// tvSong.setText(songWords[currentNotesAndWordPointer]);
				}
			} else {
				play = false;
			}
		}
		if (play) {
			switch (v.getId()) {
			case R.id.bab:
				sp.play(sound.get(12 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.ba:
				sp.play(sound.get(13 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bbb:
				sp.play(sound.get(14 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bb:
				sp.play(sound.get(15 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bc:
				sp.play(sound.get(16 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bdb:
				sp.play(sound.get(17 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bd:
				sp.play(sound.get(18 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.beb:
				sp.play(sound.get(19 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.be:
				sp.play(sound.get(20 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bf:
				sp.play(sound.get(21 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bgb:
				sp.play(sound.get(22 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;
			case R.id.bg:
				sp.play(sound.get(23 + offset), leftVolume, rightVolume,
						priority, loop, rate);
				break;

			default:
				break;
			}
		}
		offset = 0;
	}
	
	private void showTutorial() {
		//Toast.makeText(context, "tc +" +tutorialCounter, Toast.LENGTH_LONG).show();
		if(tutorialCounter == 9)
		{
			showcase.setContentTitle("Go to one octave up");
			showcase.setContentText("Play while keeping this button pressed. It will play the note one octave higher.");
		}else if(tutorialCounter == 10)
		{
			showcase.setContentTitle("Go to one octave down");
			showcase.setContentText("Play while keeping this button pressed.It will play the note one octave lower.");
		}else if(tutorialCounter == 11)
		{
			showcase.setContentTitle("Select song from list");
			showcase.setContentText("Use the list to select a song and play.");
		}
		else
		{
			showcase.setContentTitle("Play the highlighted button");
			showcase.setContentText("Control your tempo! Play nomally. Don't go too fast or too slow.");
		} 
		showcase.setShowcase(t.get(tutorialCounter), true);
		tutorialCounter++;
		if (tutorialCounter == t.size()) {
			notShownTutorial = false;
			Editor editor = sharedpreferences.edit();
			editor.putBoolean(Tutorial, notShownTutorial);
			editor.commit();
			showcase.hide();
		}
		// Toast.makeText(context, "" + tutorialCounter, Toast.LENGTH_SHORT)
		// .show();
	}

	private void manipulateNoteTextView(int pos) {
		String[] all = currentNotes;
		String firstPart = "";
		String lastPart = "";
		String keyPart = "";
		for (int i = 0; i < pos; i++) {
			firstPart += all[i] + " ";
		}
		for (int i = pos + 1; i < all.length; i++) {
			lastPart += all[i] + " ";
		}
		keyPart = all[pos];
		String text = firstPart + "<strong><i>" + keyPart + "</i></strong> "
				+ lastPart;
		tvNotes.setText(Html.fromHtml(text));
		
		keyPart = keyPart.trim();
		if(keyPart.length()==1)
		{
			
			bgColor = R.drawable.button_white_normal;
			keyToButtonMap.get(keyPart).setBackgroundColor(Color.parseColor("#5096EB"));
		}else
		{
			bgColor = R.drawable.button_black_normal;
			keyToButtonMap.get(keyPart).setBackgroundColor(Color.parseColor("#5096EB"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		bgColor = R.drawable.button_white_normal;
		ba.setBackgroundResource(bgColor);
		bb.setBackgroundResource(bgColor);
		bc.setBackgroundResource(bgColor);
		bd.setBackgroundResource(bgColor);
		be.setBackgroundResource(bgColor);
		bf.setBackgroundResource(bgColor);
		bg.setBackgroundResource(bgColor);
		bgColor = R.drawable.button_black_normal;
		bab.setBackgroundResource(bgColor);
		bbb.setBackgroundResource(bgColor);
		bdb.setBackgroundResource(bgColor);
		beb.setBackgroundResource(bgColor);
		bgb.setBackgroundResource(bgColor);
		
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.picker:
			if (notShownTutorial) {
				notShownTutorial = false;
				Editor editor = sharedpreferences.edit();
				editor.putBoolean(Tutorial, notShownTutorial);
				editor.commit();
				showcase.hide();
			}
			pickSong(PICK_TO_PLAY);
			return true;
		case R.id.reset:
			resetCurrentSong();
			return true;
			// case R.id.insert:
			// Intent i = new Intent(PianoMain.this, CreateNewSong.class);
			// startActivity(i);
			// return true;
			// case R.id.edit:
			// pickSong(PICK_TO_EDIT);
			// return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void resetCurrentSong() {
		new SongLoader().execute(song_id);
	}

	private void pickSong(int requestCode) {
		Intent i = new Intent(PianoMain.this, SongPicker.class);
		startActivityForResult(i, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PICK_TO_PLAY) {
				song_id = data.getIntExtra("songNumber", 0);
				if (song_id == 0) {
					songName = "Free Play";
					songNotes = null;
					songWords = null;
					songOffset = null;

					tvSong.setText("");
					tvNotes.setText("");
					tvSongName.setText(songName);
					bHigh.setEnabled(true);
					bLow.setEnabled(true);
				} else {
					bHigh.setEnabled(false);
					bLow.setEnabled(false);
					new SongLoader().execute(song_id);
				}
			} else if (requestCode == PICK_TO_EDIT) {
				song_id = data.getIntExtra("songNumber", 0);
				if (song_id == 0) {
					Toast.makeText(context, "Free Play is not a song to edit",
							Toast.LENGTH_LONG).show();
				} else {
					Intent i = new Intent(PianoMain.this, InsertSongInfo.class);
					Bundle bag = new Bundle();
					bag.putInt("SongIdToEdit", song_id);
					i.putExtras(bag);
					startActivity(i);
				}
			}
		}
	}

	// private void showAdd() {
	// adView = (AdView) this.findViewById(R.id.adMob);
	// adRequest = new AdRequest.Builder().addTestDevice(
	// AdRequest.DEVICE_ID_EMULATOR).build();
	// adView.loadAd(adRequest);
	// }

	private class Soundloader extends AsyncTask<Integer, Integer, int[]> {
		private ProgressDialog prgDialog;

		public Soundloader() {
			prgDialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			prgDialog.setMessage("Please be patient......");
			prgDialog.setMax(36);
			prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prgDialog.setTitle("Loading Sounds");
			prgDialog.incrementProgressBy(1);
			prgDialog.setCanceledOnTouchOutside(false);
			prgDialog.show();
		}

		@Override
		protected int[] doInBackground(Integer... soundRes) {
			int[] results = new int[36];
			for (int i = 0; i < 36; i++) {
				results[i] = sp.load(context, soundRes[i], 1);
				prgDialog.setProgress(i + 1);
			}
			return results;
		}

		@Override
		protected void onPostExecute(int[] result) {
			for (int i = 0; i < result.length; i++) {
				sound.put(i, result[i]);
			}
			prgDialog.dismiss();
		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (NoSuchMethodException e) {
					Log.e(TAG, "onMenuOpened", e);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	private class SongLoader extends AsyncTask<Integer, Void, Void> {
		boolean hasValue = false;

		@Override
		protected void onPreExecute() {
			fullSongNotes.clear();
			fullSongWords.clear();
			fullSongOffset.clear();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			int id = params[0];
			if (id == 0) {
				hasValue = true;
				songName = "Free Play";
			} else {
				PianoDB db = new PianoDB(context);
				// load song name and artist
				songName = db.getSongNameAndArtistById(id);
				// load song data
				Cursor c = db.getSongDataById(id);
				if (c.moveToFirst()) {
					hasValue = true;
					int i = 0;
					songNotes = new String[c.getCount()];
					songWords = new String[c.getCount()];
					songOffset = new int[c.getCount()];
					do {
						songNotes[i] = c.getString(c.getColumnIndex("note"));
						songWords[i] = c.getString(c.getColumnIndex("word"));
						songOffset[i] = c.getInt(c.getColumnIndex("offset"));
						i++;
					} while (c.moveToNext());
					for (int k = 0; k < songNotes.length; k++) {
						String[] x = songNotes[k].split(" ");
						for (int l = 0; l < x.length; l++) {
							fullSongNotes.add(x[l]);
							fullSongOffset.add(songOffset[k]);
						}
					}
					for (int k = 0; k < fullSongNotes.size(); k++) {
						Log.d("info", fullSongNotes.get(k) + ", "
								+ fullSongOffset.get(k));
					}
					// load loades
					load = db.getSongLoadbyID(id);
					for (int l = 0; l < load.size(); l++) {
						Log.d("load", "" + load.get(l));
					}
				} else {
					hasValue = false;
					songName = "Free Play";
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (songName.equals("Free Play")) {

				tvSongName.setText(songName);
				tvSong.setText("");
				tvNotes.setText("");
				songNotes = null;
				songWords = null;
				songOffset = null;
				song_id = 0;
				if (!hasValue) {
					Toast.makeText(
							context,
							"The selected song has no data. Free Play is selected.",
							Toast.LENGTH_LONG).show();

				}
			} else {
				tvSongName.setText(songName);
				songnotes = "";
				for (int i = 0; i < load.get(0); i++) {
					songnotes += fullSongNotes.get(i) + " ";
				}
				totalPlayedNode = 0;
				localNotePointer = 0;
				offsetPointer = 0;
				loadPointer = 0;
				currentNotes = songnotes.split(" ");
				localNotePointer = 0;
				tvNotes.setText(songnotes);
				manipulateNoteTextView(0);
			}
		}

	}
}
