package com.adorgolap.easypiano;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class InsertSongInfo extends Activity implements OnClickListener {
	private int sequence = 1, id;
	TextView tvSequaneceNo;
	EditText etSongWord, etSongNotes;
	RadioGroup rg;
	RadioButton rDefault, rHigh, rLow;
	Button bSaveAndNext, bSaveAndExit;
	ArrayList<String> validNotes = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_inserter_layout);
		Bundle bag = getIntent().getExtras();
		id = bag.getInt("SongIdToEdit");
		initialize();
		tryToFetchDataAndPopulateViews(id, sequence);
	}

	private void tryToFetchDataAndPopulateViews(int i, int s) {
		PianoDB db = new PianoDB(getApplicationContext());
		String[] temp = db.getSequenceData(i, s);
		tvSequaneceNo.setText("Sequence " + s);
		if (temp != null) {
			etSongNotes.setText(temp[0]);
			etSongWord.setText(temp[1]);
			if (Integer.parseInt(temp[2]) == 12) {
				rHigh.setChecked(true);
				rLow.setChecked(false);
				rDefault.setChecked(false);
			} else if (Integer.parseInt(temp[2]) == -12) {
				rLow.setChecked(true);
				rHigh.setChecked(false);
				rDefault.setChecked(false);
			} else {
				rDefault.setChecked(true);
				rHigh.setChecked(false);
				rLow.setChecked(false);
				
			}
		}
	}

	private void initialize() {
		String temp[] = { "Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F",
				"Gb", "G" };
		Collections.addAll(validNotes, temp);
		
		etSongWord = (EditText) findViewById(R.id.etSongWord);
		etSongNotes = (EditText) findViewById(R.id.etSongNote);
		tvSequaneceNo = (TextView) findViewById(R.id.tvSequenceNo);
		rg = (RadioGroup) findViewById(R.id.rg);
		rDefault = (RadioButton) findViewById(R.id.rNormal);
		rHigh = (RadioButton) findViewById(R.id.rHigh);
		rLow = (RadioButton) findViewById(R.id.rLow);
		bSaveAndExit = (Button) findViewById(R.id.bSaveAndExit);
		bSaveAndNext = (Button) findViewById(R.id.bSaveAndNext);
		bSaveAndExit.setOnClickListener(this);
		bSaveAndNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bSaveAndExit:
			saveAndExit();
			break;
		case R.id.bSaveAndNext:
			saveAndNext();
			break;

		default:
			break;
		}
	}

	private void saveAndNext() {
		String notes = etSongNotes.getText().toString();
		String word = etSongWord.getText().toString();
		int offset = 0;
		switch (rg.getCheckedRadioButtonId()) {
		case R.id.rHigh:
			offset = 12;
			break;
		case R.id.rNormal:
			offset = 0;
			break;
		case R.id.rLow:
			offset = -12;
			break;
		default:
			offset = 0;
			break;
		}
		if (notes.equals("")) {
			Toast.makeText(this, "Enter some valid note", Toast.LENGTH_LONG)
					.show();
		} else {
			boolean isOk = true;
			String temp[] = notes.split(" ");
			for (String t : temp) {
				if (!validNotes.contains(t)) {
					Toast.makeText(getApplicationContext(),
							"Please enter valid notes for " + t,
							Toast.LENGTH_SHORT).show();
					isOk = false;
					break;
				}
			}
			if (isOk) {
				PianoDB db = new PianoDB(getApplicationContext());
				boolean success = db.insertOrUpdateSongInfo(id, sequence,
						notes, word, offset);
				if (success) {
					sequence++;
					tryToFetchDataAndPopulateViews(id, sequence);
				} else {
					Toast.makeText(getApplicationContext(), "DB Error",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void saveAndExit() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.inserter_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reset_all:
			reset();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void reset() {
		tvSequaneceNo.setText("Sequence " + sequence);
		etSongNotes.setText("");
		etSongWord.setText("");
		rDefault.setChecked(true);
	}
}
