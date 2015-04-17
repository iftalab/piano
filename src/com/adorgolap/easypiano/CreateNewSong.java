package com.adorgolap.easypiano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNewSong extends Activity {
	EditText etSongName, etArtistName;
	Button bNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_inserter_layout_first_info);
		etSongName = (EditText) findViewById(R.id.etSongName);
		etArtistName = (EditText) findViewById(R.id.etSongArtist);
		bNext = (Button) findViewById(R.id.bNext);
		bNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String songName = etSongName.getText().toString();
				String artistName = etArtistName.getText().toString();
				if (songName.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please Enter a Valid Song Name",
							Toast.LENGTH_SHORT).show();
				} else {
					PianoDB db = new PianoDB(getApplicationContext());
					int id = db.insertNewSong(songName, artistName);
					if (id != -1) {
						Intent i = new Intent(CreateNewSong.this,
								InsertSongInfo.class);
						Bundle bag = new Bundle();
						bag.putInt("SongIdToEdit", id);
						i.putExtras(bag);
						startActivity(i);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "DB Error",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
}
