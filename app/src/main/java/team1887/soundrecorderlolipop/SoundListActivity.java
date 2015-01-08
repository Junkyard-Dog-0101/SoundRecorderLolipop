package team1887.soundrecorderlolipop;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundListActivity extends Activity {

    private static final String LOG_TAG = "SoundList";
    private static boolean isPlaying;
    private MediaPlayer mPlayer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPlaying = false;
        setContentView(R.layout.activity_sound_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final List<String> soundList = new ArrayList<String>();

        final ListView soundListView = (ListView) findViewById(R.id.list_sound);

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/");
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (!inFile.isDirectory()) {
                String filenameArray[] = inFile.getName().split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                if (extension.equals("3gp"))
                    soundList.add(filenameArray[0]);
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                soundList);

        soundListView.setAdapter(arrayAdapter);
        soundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isPlaying)
                    return;
                isPlaying = true;
                String s = (String) soundListView.getItemAtPosition(position);
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/" + s + ".3gp");
                    mPlayer.prepare();
                    mPlayer.start();
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayer.release();
                            isPlaying = false;
                        }
                    });
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPlaying = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        if (mPlayer != null) {
            try {
                mPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
