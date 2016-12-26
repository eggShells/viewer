package com.blockhouse.drawingviewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RenderPDF extends AppCompatActivity {

    //PDFView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_pdf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, byte[]> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected byte[] doInBackground(String... f_url) {

            InputStream myIS;
            byte[] netBytes;
            try {
                netBytes = NetworkAdapter.getDocument("C190400");
                return netBytes;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
//            return myIS;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(byte[] mByteArray) {
            // dismiss the dialog after the file was downloaded
//            dismissDialog(progress_bar_type);
            int count;
            try {

                // download the file
                InputStream bis = new ByteArrayInputStream(mByteArray);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/C190400.pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = bis.read(data)) != -1) {
                    total += count;
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                bis.close();

            } catch (Exception e) {
                //Log.e("Error: ", e.getMessage());
            }

        }

    }

}
