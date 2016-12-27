
package com.blockhouse.drawingviewer;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

    private String[] mPDFTitles;
    NavigationView navigationView;
    TextView html;
    MenuItem curMenuItem;
    Menu myMenu;
    private ArrayAdapter<String> mScheduledNavMenu;
    private ImageView pdfViewer;
    Animation mAnimation;

    PdfRenderer mRender;
    ParcelFileDescriptor mPFD;
    PdfRenderer.Page mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //instantiate view objects set by xml files
        pdfViewer = (ImageView) findViewById(R.id.pdfImg);
        ListView mScheduledItems = (ListView) findViewById(R.id.scheduled_items);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_view);

        Date mCal;
        mCal = Calendar.getInstance().getTime();
        SimpleDateFormat mDate = new SimpleDateFormat("MM-dd-yyyy");
        final String curDate = mDate.format(mCal);
        Log.d("tag",curDate);
//        mScheduledNavMenu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPDFTitles);
//        mScheduledItems.setAdapter(mScheduledNavMenu);
        //ID toolbar is defined in app_bar_main
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        myMenu = navigationView.getMenu();
//        myMenu.clear();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //should AsyncRequest be inside listener or outside?  difference?
        final RequestDrawings myRequest = new RequestDrawings();
        myRequest.execute(curDate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final RequestDrawings myRequest = new RequestDrawings();
//                myRequest.execute(curDate);
                DownloadFileFromURL myDownload = new DownloadFileFromURL();
                myDownload.execute("C190400");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String curItem = String.valueOf(item.getTitle());
        curItem = curItem.substring(0,12);
        DownloadFileFromURL myDownload = new DownloadFileFromURL();
        myDownload.execute(curItem);
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addNavMenuItem(String newItem){

        Menu navMenu = navigationView.getMenu();
        navMenu.add(newItem);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private class RequestDrawings extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            String date = strings[0];
            String HTMLresults = new String();
            try {
                HTMLresults = NetworkAdapter.getScheduledItems("FIN",date);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return HTMLresults;
        }

        @Override
        protected void onPostExecute(String s) {
            //html.setText(s);
            Gson myGson = new GsonBuilder().create();
            mPDFTitles = new String[100];
//            mPDFTitles = new ArrayList();
            mPDFTitles = myGson.fromJson(s,mPDFTitles.getClass());
            if (mPDFTitles.length > 0){
                for(int i = 0; i < mPDFTitles.length; i++){
                    curMenuItem = myMenu.getItem(i);
                    curMenuItem.setTitle(mPDFTitles[i]);
                }
            super.onPostExecute(s);
        }}
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
        protected byte[] doInBackground(String... item) {

            InputStream myIS;
            byte[] netBytes;
            try {
                netBytes = NetworkAdapter.getDocument(item[0]);
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
                Log.e("Error: ", e.getMessage());
            }

            try {
                File mFile = new File(Environment.getExternalStorageDirectory().toString() + "/C190400.pdf");
                if (mFile.exists())
                    mPFD = ParcelFileDescriptor.open(mFile, ParcelFileDescriptor.MODE_READ_ONLY);
                mRender = new PdfRenderer(mPFD);
                mPage = mRender.openPage(0);
                Bitmap mBit = Bitmap.createBitmap(getResources().getDisplayMetrics().densityDpi / 72 * mPage.getWidth(),getResources().getDisplayMetrics().densityDpi / 72 * mPage.getHeight(),Bitmap.Config.ARGB_8888);
                //Bitmap mBit = Bitmap.createBitmap(getResources().getDisplayMetrics().densityDpi,getResources().getDisplayMetrics().densityDpi,Bitmap.Config.ARGB_8888);
                mPage.render(mBit,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                pdfViewer.setImageBitmap(mBit);
//                    pdfViewer.setAnimation(mAnimation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
