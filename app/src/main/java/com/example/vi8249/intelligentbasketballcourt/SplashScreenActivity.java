package com.example.vi8249.intelligentbasketballcourt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

/**
 * Created by vi8249 on 2017/5/26.
 */

public class SplashScreenActivity extends Activity {
    protected Activity mSplash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        mSplash = this;
        new SplashAsyncTack().execute();
    }

    private class SplashAsyncTack extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Intent it = new Intent(mSplash, MainActivity.class);
            mSplash.startActivity(it);
            finish();
        }
    }
}
