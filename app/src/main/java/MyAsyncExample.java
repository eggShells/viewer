import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.blockhouse.drawingviewer.NetworkAdapter;

import java.io.IOException;

import static com.blockhouse.drawingviewer.R.id.imageView;

/**
 * Created by eslaugh on 12/9/2016.
 */

public class MyAsyncExample extends AsyncTask<String,Void,String> {
    protected void onPreExecute() {
        // Runs on the UI thread before doInBackground
        // Good for toggling visibility of a progress indicator
//        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    protected String doInBackground(String... strings) {
        // Some long-running task like downloading an image.
        String myReturnString = new String();
        try {
            myReturnString = NetworkAdapter.getWebPageContents(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myReturnString;
    }

    protected void onProgressUpdate(Progress... values) {
        // Executes whenever publishProgress is called from doInBackground
        // Used to update the progress indicator
//        progressBar.setProgress(values[0]);
    }

    protected void onPostExecute(Bitmap result) {
        // This method is executed in the UIThread
        // with access to the result of the long running task
//        imageView.setImageBitmap(result);
        // Hide the progress bar
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}
