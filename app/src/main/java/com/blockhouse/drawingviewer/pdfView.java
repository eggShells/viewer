package com.blockhouse.drawingviewer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class pdfView extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener  {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

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
        setContentView(R.layout.activity_pdf_view);
        //instantiate view objects set by xml files
        pdfViewer = (ImageView) findViewById(R.id.pdfBitmap);
        ListView mScheduledItems = (ListView) findViewById(R.id.scheduled_items);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_view);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });

        Date mCal;
        mCal = Calendar.getInstance().getTime();
        SimpleDateFormat mDate = new SimpleDateFormat("MM-dd-yyyy");
        final String curDate = mDate.format(mCal);
        Log.d("tag",curDate);
//        mScheduledNavMenu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPDFTitles);
//        mScheduledItems.setAdapter(mScheduledNavMenu);
        //ID toolbar is defined in app_bar_main
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        myMenu = navigationView.getMenu();
//        myMenu.clear();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //should AsyncRequest be inside listener or outside?  difference?
        final pdfView.RequestDrawings myRequest = new pdfView.RequestDrawings();
        myRequest.execute(curDate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final RequestDrawings myRequest = new RequestDrawings();
//                myRequest.execute(curDate);
                pdfView.DownloadFileFromURL myDownload = new pdfView.DownloadFileFromURL();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
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

            byte[] netBytes;
            try {
                netBytes = NetworkAdapter.getDocument("C190400");
                return netBytes;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
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
//                for(int i = 0; i < mPDFTitles.length; i++){
                for(int i = 0; i < 5; i++){
                    curMenuItem = myMenu.getItem(i);
                    curMenuItem.setTitle(mPDFTitles[i]);
                }
                super.onPostExecute(s);
            }}
    }


    /**
     * A simple pager adapter that represents 5 {@link pdfViewFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return pdfViewFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
