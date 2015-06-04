package cz.kinoscala.scala.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import cz.kinoscala.scala.MovieNotification;
import cz.kinoscala.scala.R;
import cz.kinoscala.scala.fragment.ContactsFragment;
import cz.kinoscala.scala.fragment.MovieDetailFragment;
import cz.kinoscala.scala.fragment.NavigationDrawerFragment;
import cz.kinoscala.scala.fragment.NotificationListFragment;
import cz.kinoscala.scala.fragment.UpcomingMoviesFragment;

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
    private Fragment selectedFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

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
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        onSectionAttached(position);
        FragmentManager fragmentManager = getSupportFragmentManager();

        // select correct fragment
        selectedFragment = null;
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

    public void removeNotificationHandler(View view) {
        ((NotificationListFragment)selectedFragment).removeNotification(view);
    }
}
