
package com.blockhouse.drawingviewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] mPDFTitles;
    NavigationView navigationView;
    TextView html;
    MenuItem curMenuItem;
    Menu myMenu;
    private ListView mScheduledItems;
    private ArrayAdapter<String> mScheduledNavMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date mCal;
        mCal = Calendar.getInstance().getTime();
        SimpleDateFormat mDate = new SimpleDateFormat("MM-dd-yyyy");
        final String curDate = mDate.format(mCal);
        Log.d("tag",curDate);
        mScheduledNavMenu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPDFTitles);
        mScheduledItems.setAdapter(mScheduledNavMenu);
        //ID toolbar is defined in app_bar_main
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        myMenu = navigationView.getMenu();
//        myMenu.clear();
        html = (TextView) findViewById(R.id.htmlView);

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
//            if mPDFTitles.length{
                for(int i = 0; i < mPDFTitles.length; i++){
                    curMenuItem = myMenu.getItem(i);
                    curMenuItem.setTitle(mPDFTitles[i]);
                }
            super.onPostExecute(s);
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, InputStream> {

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
        protected InputStream doInBackground(String... f_url) {

            InputStream myIS;
            try {
                myIS = NetworkAdapter.getDocument("C190400");
                return myIS;
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
        protected void onPostExecute(InputStream file_url) {
            // dismiss the dialog after the file was downloaded
//            dismissDialog(progress_bar_type);
            int count;
            try {

                // download the file
                InputStream bis = new BufferedInputStream(file_url,
                        8192);

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

        }

    }
}
