package bit.clarksj4.germanlanguagetrainer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends Activity
{
    // Keys for intent bundle extras
    public static final String NOUN_VARIABLES_BUNDLE = "noun_variables_bundle";
    public static final String NOUN_NAMES = "noun_names";
    public static final String NOUN_ARTICLES = "noun_articles";
    public static final String NOUN_IMAGE_IDS = "noun_image_ids";
    public static final String N_NOUNS = "n_nouns";

    private TypedArray nounImageIds;
    private TypedArray nounNames;
    private TypedArray nounArticles;
    private TypedArray nounPronunciationIds;
    private int[] selectionOrder;
    private ArrayList<Integer> incorrectGuesses;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get image ids from resources
        nounImageIds = getResources().obtainTypedArray(R.array.noun_images);
        nounNames = getResources().obtainTypedArray(R.array.noun_names);
        nounArticles = getResources().obtainTypedArray(R.array.noun_articles);
        nounPronunciationIds = getResources().obtainTypedArray(R.array.noun_pronunciations);

        // Create array filled with all numbers up to the number of images; then shuffle it.
        selectionOrder = new int[nounImageIds.length()];
        for (int i = 0; i < selectionOrder.length; i++)
        {
            selectionOrder[i] = i;
        }
        shuffle(selectionOrder);

        // Index of image to show starts at 0
        index = 0;

        incorrectGuesses = new ArrayList<Integer>();

        // Set click handler for each article button
        ImageButton maleArticleButton = (ImageButton)findViewById(R.id.maleArticleImageButton);
        maleArticleButton.setOnClickListener(new ArticleButtonClickHandler());

        ImageButton neutralArticleButton = (ImageButton)findViewById(R.id.neutralArticleImageButton);
        neutralArticleButton.setOnClickListener(new ArticleButtonClickHandler());

        ImageButton femaleArticleButton = (ImageButton)findViewById(R.id.femaleArticleImageButton);
        femaleArticleButton.setOnClickListener(new ArticleButtonClickHandler());

        // Update views for the current round
        update();
    }

    /**
     * Update views that need to change for the current iteration
     */
    private void update()
    {
        // Get image id from typed array
        int imageId = getNounImageId(index);
        String nounName = getNounName(index);

        // Get nounImageView and set image to the image at 'index' in the typed array
        ImageView nounImageView = (ImageView)findViewById(R.id.nounImageView);
        nounImageView.setImageResource(imageId);

        // Set nounText to nounName
        TextView nounNameTextView = (TextView)findViewById(R.id.nounNameTextView);
        nounNameTextView.setText(nounName);

        // Get outOf string from resources and format to show amount of nouns guessed
        String outOf = (String)getResources().getString(R.string.out_of);
        String amountCompleted = String.format(outOf, index + 1, selectionOrder.length);

        // Give amount of nouns guessed string to quiz index textView
        TextView quizIndex = (TextView)findViewById(R.id.quizIndexTextView);
        quizIndex.setText(amountCompleted);

        // Play noun pronunciation sound
        int nounPronunciationId = getNounPronunciationId(index);
        MediaPlayer.create(this, nounPronunciationId).start();
    }

    /**
     * Gets the ID of the image for the noun at index in selection order
     */
    private int getNounImageId(int index) { return nounImageIds.getResourceId(selectionOrder[index], -1); }

    /**
     * Gets the ID of the sound for the noun at index in selection order
     */
    private int getNounPronunciationId(int index){ return nounPronunciationIds.getResourceId(selectionOrder[index], -1); }

    /**
     * Gets the noun article at index in selection order
     */
    private String getNounArticle(int index) { return nounArticles.getString(selectionOrder[index]); }

    /**
     * Gets the noun name at index in selection order
     */
    private String getNounName(int index)
    {
        return nounNames.getString(selectionOrder[index]);
    }

    private int getArticlePronunciationId(String article)
    {
        switch(article)
        {
            case "die":
                return R.raw.die;
            case "das":
                return R.raw.das;
            case "der":
                return R.raw.der;
            default:
                return -1;
        }
    }

    /**
     * Fisher–Yates shuffle
     * @param array An array of integers to be shuffled
     */
    private static void shuffle(int[] array)
    {
        Random random = new Random();

        // Swap each element in array with another random element in array
        for (int index = array.length - 1; index > 0; index--)
        {
            int randomIndex = random.nextInt(index + 1);

            // Swap
            int temp = array[index];
            array[index] = array[randomIndex];
            array[randomIndex] = temp;
        }
    }

    /**
     * OnClickHandler for gender article image button presses. Checks if the article associated with
     * the clicked button is the correct one for the current index and responds accordingly
     */
    private class ArticleButtonClickHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            // Convert view to image button and disable so that it can't be pressed again
            ImageButton pressedButton = (ImageButton)v;
            pressedButton.setEnabled(false);

            // Get chosen article from pressed button tag, get correct article from resources
            String chosenArticle = (String)pressedButton.getTag();
            String correctArticle = getNounArticle(index);

            // Is the chosen article the correct one
            if (chosenArticle.equals(correctArticle))
            {
                // Find and disable all article buttons to prevent further interaction
                RelativeLayout articleButtonsLayout = (RelativeLayout)findViewById(R.id.articleButtonsRelativeLayout);
                for(int i = 0; i < articleButtonsLayout.getChildCount(); i++)
                {
                    articleButtonsLayout.getChildAt(i).setEnabled(false);
                }

                // Set button to display tick
                pressedButton.setImageResource(R.drawable.tick);

                // Find nounName textView and get the noun name from it
                TextView nounNameTextView = (TextView)findViewById(R.id.nounNameTextView);
                CharSequence nounName = nounNameTextView.getText();

                // Prepend the correct article to the nounName and give it back to textView
                CharSequence nounAndArticle = correctArticle + " " + nounName;
                nounNameTextView.setText(nounAndArticle);

                // Get id of noun and article pronunciation sound resources
                int articlePronunciationId = getArticlePronunciationId(correctArticle);
                int nounPronunciationId = getNounPronunciationId(index);

                // Create mediaplayer to play sound resources
                MediaPlayer player = MediaPlayer.create(QuizActivity.this, articlePronunciationId);

                // Add handler to play sounds one after the other -> handler will also proceed to next round
                player.setOnCompletionListener(new PlayOnSoundCompleteHandler(nounPronunciationId));
                player.start();
            }

            // Incorrect article given
            else
            {
                // Check if index has already been registered as being guessed incorrectly
                if(!incorrectGuesses.contains(index))
                {
                    incorrectGuesses.add(index);
                }
            }
        }
    }

    private class EndRoundUpdater implements Runnable
    {
        @Override
        public void run()
        {
            // Get each article from resources
            String maleArticle = (String)getResources().getString(R.string.male_article);
            String neutralArticle = (String)getResources().getString(R.string.neutral_article);
            String femaleArticle = (String)getResources().getString(R.string.female_article);

            // Get article buttons layout
            RelativeLayout articleButtonsLayout = (RelativeLayout)findViewById(R.id.articleButtonsRelativeLayout);
            for(int i = 0; i < articleButtonsLayout.getChildCount(); i++)
            {
                // Get image button and associated article
                ImageButton articleImageButton = (ImageButton)articleButtonsLayout.getChildAt(i);
                String article = (String)articleImageButton.getTag();

                // Reset image to the one associated with the article
                if (article.equals(maleArticle))
                {
                    articleImageButton.setImageResource(R.drawable.der);
                }

                else if (article.equals(neutralArticle))
                {
                    articleImageButton.setImageResource(R.drawable.das);
                }

                else if (article.equals(femaleArticle))
                {
                    articleImageButton.setImageResource(R.drawable.die);
                }

                // Re-enable button
                articleImageButton.setEnabled(true);
            }

            // Increment quiz round index and update relevant views (only if game isn't over
            QuizActivity.this.index++;
            if (QuizActivity.this.index < QuizActivity.this.selectionOrder.length)
            {
                QuizActivity.this.update();
            }

            else // Quiz is over
            {
                // Number of incorrectly guessed nouns
                int nIncorrect = incorrectGuesses.size();

                // Arrays store information on the incorrectly guessed nouns
                String[] nounNames = new String[nIncorrect];
                String[] nounArticles = new String[nIncorrect];
                int[] nounImageIds = new int[nIncorrect];

                // Assemble information on incorrectly guessed nouns
                for (int i = 0 ; i < nIncorrect; i++)
                {
                    // Get the i'th incorrectly guessed noun's index in typed arrays
                    int index = incorrectGuesses.get(i);

                    nounNames[i] = getNounName(index);
                    nounArticles[i] = getNounArticle(index);
                    nounImageIds[i] = getNounImageId(index);
                }

                // Create a new bundle to store information on incorrectly guessed nouns
                Bundle bundle = new Bundle();
                bundle.putStringArray(NOUN_NAMES, nounNames);
                bundle.putStringArray(NOUN_ARTICLES, nounArticles);
                bundle.putIntArray(NOUN_IMAGE_IDS, nounImageIds);
                bundle.putInt(N_NOUNS, QuizActivity.this.nounNames.length());

                // Create intent, give it information on incorrectly guessed nouns
                Intent intent = new Intent(QuizActivity.this, ReviewActivity.class);
                intent.putExtra(NOUN_VARIABLES_BUNDLE, bundle);

                // Release resources associated with TypedArrays
                QuizActivity.this.nounArticles.recycle();
                QuizActivity.this.nounImageIds.recycle();
                QuizActivity.this.nounNames.recycle();

                // Release resources associated with this activity and start new activity
                finish();
                startActivity(intent);
            }
        }
    }

    private class PlayOnSoundCompleteHandler implements MediaPlayer.OnCompletionListener
    {
        private int nextSoundId;

        public PlayOnSoundCompleteHandler(int nextSoundId)
        {
            this.nextSoundId = nextSoundId;
        }

        @Override
        public void onCompletion(MediaPlayer mp)
        {
            MediaPlayer.create(QuizActivity.this, nextSoundId).start();

            // Get handler to create a delay then proceed to next round
            Handler handler = new Handler();
            handler.postDelayed(new EndRoundUpdater(), 1500);
        }
    }
}
