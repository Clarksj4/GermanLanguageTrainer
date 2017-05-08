package bit.clarksj4.germanlanguagetrainer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class ReviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Get intent to access bundle information
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(QuizActivity.NOUN_VARIABLES_BUNDLE);

        // Get noun incorrect guesses information
        String[] nounNames = bundle.getStringArray(QuizActivity.NOUN_NAMES);
        String[] nounArticles = bundle.getStringArray(QuizActivity.NOUN_ARTICLES);
        int[] nounImageIds = bundle.getIntArray(QuizActivity.NOUN_IMAGE_IDS);

        // If there were some incorrect answers
        if (nounNames != null)
        {
            // Get total number of nouns for bundle; use to calculate the total number correctly guessed
            int nNouns = bundle.getInt(QuizActivity.N_NOUNS, 0);
            int nCorrect = nNouns - nounNames.length;

            // Get 'outOf' string and format with nCorrect and total numbers
            String outOfText = getResources().getString(R.string.out_of);
            String completed = String.format(outOfText, nCorrect, nNouns);

            // Give nCorrect / total to text view
            TextView outOfTextView = (TextView)findViewById(R.id.outOfTextView);
            outOfTextView.setText(completed);

            // Get tableLayout, iterate through incorrect guesses adding a row with image and text for each
            TableLayout reviewTableLayout = (TableLayout)findViewById(R.id.incorrectGuessesTableLayout);
            for (int i = 0; i < nounNames.length; i++)
            {
                // Create new row to add information to
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));

                // Create image view and set image to noun image; add it to row
                ImageView nounImageView = new ImageView(this);
                nounImageView.setImageResource(nounImageIds[i]);
                nounImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                nounImageView.setAdjustViewBounds(true);
                row.addView(nounImageView, new TableRow.LayoutParams(200, 200));

                // Layout params for text view in table row
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;

                // Create text view and set its text to article + noun; add it to row
                TextView nounTextView = new TextView(this);
                nounTextView.setText(nounArticles[i] + " " + nounNames[i]);
                nounTextView.setTextColor(Color.BLACK);
                nounTextView.setPadding(20, 0, 20, 0);
                nounTextView.setTextSize(30f);
                nounTextView.setTypeface(null, Typeface.BOLD);
                nounTextView.setLayoutParams(layoutParams);
                row.addView(nounTextView);

                // Add row to table
                reviewTableLayout.addView(row);
            }
        }
    }
}
