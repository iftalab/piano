package com.adorgolap.easypiano.unused;

public class Unsued {
/*	private void showSongPickerDialog() {
		final AlertDialog songPicker;

		// Strings to Show In Dialog with Radio Button
		final CharSequence[] items = { "Fur Elise by Beethoven",
				"Twinkle Twinkle Little Star", "My Heart Will Go On",
				"Careless whisper", "No Song" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Music To Play");
		builder.setSingleChoiceItems(items, selectedSong,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						selectedSong = item;
					}
				});
		builder.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetCurrentSong();
					}

				});
		songPicker = builder.create();
		songPicker.show();
	}
	
	private void resetCurrentSong() {
		switch (selectedSong) {
		case 1:
			leftThreshold = 1;
			rightThreshold = 10;
			break;
		case 2:
			leftThreshold = 3;
			rightThreshold = 10;
			break;
		default:
			leftThreshold = -1;
			rightThreshold = 12;
			break;
		}

		noteChangerCounter = 0;
		currentNoteIndex = 0;
		ratePointer = 0;
		if (selectedSong == selectedSongName.size()) {
			tvNotes.setText("");
			tvSong.setText("");
			tvSongName.setText("No Song Selected");
		} else {
			tvSongName.setText(selectedSongName.get(selectedSong));
			notes = selectedSongNotes.get(selectedSong);
			words = selectedSongWords.get(selectedSong);
			tvNotes.setText(notes[0]);
			tvSong.setText(words[0]);
			currentNotePartsCount = notes[0].split(" ").length;
		}
	}
	
	*/
}
