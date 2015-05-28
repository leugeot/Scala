package cz.kinoscala.scala.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.kinoscala.scala.R;
import cz.kinoscala.scala.fragment.ContactsFragment;
import cz.kinoscala.scala.fragment.MovieDetailFragment;
import cz.kinoscala.scala.fragment.NavigationDrawerFragment;
import cz.kinoscala.scala.fragment.NotificationListFragment;
import cz.kinoscala.scala.fragment.UpcomingMoviesFragment;
import cz.kinoscala.scala.notification.MovieNotificationReceiver;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        UpcomingMoviesFragment.OnFragmentInteractionListener,
        NotificationListFragment.OnFragmentInteractionListener,
        MovieDetailFragment.OnFragmentInteractionListener,
        ContactsFragment.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

//        notificationTest();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        onSectionAttached(position);
        FragmentManager fragmentManager = getSupportFragmentManager();

        // select correct fragment
        Fragment selectedFragment = null;
        String tag = null;

        switch (position) {
            case 0:
                tag = getString(R.string.upcoming_movies_title_section);
                selectedFragment = fragmentManager.findFragmentByTag(tag);

                if (selectedFragment == null) {
                    selectedFragment = new UpcomingMoviesFragment();
                }
                break;

            case 1:
                tag = getString(R.string.notifications_title_section);
                selectedFragment = fragmentManager.findFragmentByTag(tag);

                if (selectedFragment == null) {
                    selectedFragment = new NotificationListFragment();
                }
                break;

            case 2:
                tag = getString(R.string.contact_title_section);
                selectedFragment = fragmentManager.findFragmentByTag(tag);

                if (selectedFragment == null) {
                    selectedFragment = new ContactsFragment();
                }
                break;
        }

        if (selectedFragment != null) {
            // set selected fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.container, selectedFragment, tag)
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.upcoming_movies_title_section);
                break;
            case 1:
                mTitle = getString(R.string.notifications_title_section);
                break;
            case 2:
                mTitle = getString(R.string.contact_title_section);
                break;
        }
        getSupportActionBar().setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.upcoming_movies, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void notificationTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.DAY_OF_MONTH, 25);

        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, 44);
        calendar.set(Calendar.SECOND, 50);
        calendar.set(Calendar.AM_PM,Calendar.PM);

        System.out.println("time of film: \n" +
                "MONTH " + calendar.get(Calendar.MONTH) + "\n" +
                "YEAR " + calendar.get(Calendar.YEAR) + "\n" +
                "DAY_OF_MONTH " + calendar.get(Calendar.DAY_OF_MONTH) + "\n" +
                "HOUR_OF_DAY " + calendar.get(Calendar.HOUR_OF_DAY) + "\n" +
                "MINUTE " + calendar.get(Calendar.MINUTE) + "\n" +
                "SECOND " + calendar.get(Calendar.SECOND) + "\n" +
                "AM_PM " + calendar.get(Calendar.AM_PM) + "\n");

        Calendar c = Calendar.getInstance();
        System.out.println("time of android: \n" +
                "MONTH " + c.get(Calendar.MONTH) + "\n" +
                "YEAR " + c.get(Calendar.YEAR) + "\n" +
                "DAY_OF_MONTH " + c.get(Calendar.DAY_OF_MONTH) + "\n" +
                "HOUR_OF_DAY " + c.get(Calendar.HOUR_OF_DAY) + "\n" +
                "MINUTE " + c.get(Calendar.MINUTE) + "\n" +
                "SECOND " + c.get(Calendar.SECOND) + "\n" +
                "AM_PM " + c.get(Calendar.AM_PM) + "\n");

        Intent myIntent = new Intent(MainActivity.this, MovieNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}
