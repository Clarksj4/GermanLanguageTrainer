package bit.clarksj4.germanlanguagetrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {
    // TODO: add delay times to resources

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Instantiate handler to create delay
        Handler handler = new Handler();
        handler.postDelayed(new AfterDelayHandler(), 1500);
    }

    private class AfterDelayHandler implements Runnable
    {
        public void run()
        {
            // Start quiz activity, releasing the resources associated with this activity
            Intent intent = new Intent(SplashScreenActivity.this, QuizActivity.class);
            finish();
            startActivity(intent);
        }
    }
}
