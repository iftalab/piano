package com.adorgolap.easypiano;
import com.adorgolap.easypiano.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SmartInsertSongInfo extends Activity{
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sequence_view);
		lv = (ListView)findViewById(R.id.lvSequence);
		String x[] = {"aaa","bbb","ccc"};
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,x));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.smart_insert_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
