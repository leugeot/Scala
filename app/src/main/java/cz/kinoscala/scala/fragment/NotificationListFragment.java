package cz.kinoscala.scala.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.kinoscala.scala.MovieNotification;
import cz.kinoscala.scala.MovieNotificationManager;
import cz.kinoscala.scala.NotificationAdapter;
import cz.kinoscala.scala.R;
import cz.kinoscala.scala.database.DBManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NotificationListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private ListView notificationListView;
    private NotificationAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification_list, container, false);
        notificationListView = (ListView) v.findViewById(R.id.notification_list);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void updateNotificationsListView(List<MovieNotification> notifications) {
        if (notifications != null) {
            adapter = new NotificationAdapter(getActivity(), 0, notifications);
            notificationListView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        DBManager db = new DBManager(getActivity().getApplicationContext());
        try {
            db.open();
            List<MovieNotification> notifications = db.getNotifications();

            Date actualDate = new Date();

            for (MovieNotification notification : notifications) {
                if (notification.getMovie().getDate().compareTo(actualDate) > 0){
                    db.removeNotification(notification.getMovie().getId());
                }
            }

            db.close();

            updateNotificationsListView(notifications);
            Log.i("updatingnotifications", "set from db");
        } catch (SQLException e) {
            Log.e("updatingnotifications", "ERROR WHILE LOADING NOTIFICATIONS FROM DB");
            // SHOW SOME KIND OF ERROR
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void removeNotification(View view) {
        MovieNotification notification = (MovieNotification) view.getTag();
        adapter.remove(notification);

        MovieNotificationManager mnm = new MovieNotificationManager(getActivity().getApplicationContext(),
                (AlarmManager) getActivity().getSystemService(FragmentActivity.ALARM_SERVICE));

        mnm.removeNotification(notification.getMovie().getId(), notification.getID());
    }
}
