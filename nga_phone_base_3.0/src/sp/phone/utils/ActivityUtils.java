package sp.phone.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.MessageDetailActivity;
import gov.anzong.androidnga.activity.MessageListActivity;
import sp.phone.bean.BoardHolder;
import sp.phone.common.PhoneConfiguration;

public class ActivityUtils {

    public static final String dialogTag = "saying";
    static final String TAG = ActivityUtils.class.getSimpleName();
    private static final double EARTH_RADIUS = 6378.137;
    static ActivityUtils instance;
    static Object lock = new Object();
    private DialogFragment df = null;
    private static String[] sMaterialSupportList = {"SettingsActivity", "LoginActivity","MessageListActivity","MessageDetailActivity"
            ,"MessagePostActivity","FlexibleTopicListActivity","PostActivity"};

    private static String[] sSupportNewUi = { "SettingsActivity" ,"LoginActivity","MainActivity",
            MessageDetailActivity.class.getSimpleName(),
            ArticleListActivity.class.getSimpleName(), MessageListActivity.class.getSimpleName()};

    public static final int REQUEST_CODE_LOGIN = 1;

    public static final int REQUEST_CODE_SETTING = 2;

    public static final int REQUEST_CODE_TOPIC_POST = 3;

    private ActivityUtils() {
    }

    public static boolean supportMaterialMode(Context context){
        for (int i = 0 ; i < sMaterialSupportList.length; i++){
            if (sMaterialSupportList[i].equals(context.getClass().getSimpleName())){
                return true;
            }
        }
        return false;
    }

    public static boolean supportNewUi(Context context){
        for (int i = 0 ; i < sSupportNewUi.length; i++){
            if (sSupportNewUi[i].equals(context.getClass().getSimpleName())){
                return true;
            }
        }
        return false;
    }

    public static void showToast(Context context,int resId) {
        if (context != null) {
            Toast.makeText(context,resId,Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(Context context,String res) {
        if (context != null) {
            Toast.makeText(context,res,Toast.LENGTH_SHORT).show();
        }
    }

    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;//instance;

    }

    public static void reflushLocation(Context context) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = null;
        if (provider != null) {
            location = locationManager.getLastKnownLocation(provider);
        } 
        /*else{
	    	Toast.makeText(context, R.string.location_service_disabled, Toast.LENGTH_SHORT).show();
	    	return;
	    }*/

        if (location != null) {
            updateLocation(location);
        } else {
            //Toast.makeText(context, R.string.locating, Toast.LENGTH_SHORT).show();
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;

            } else {
                Toast.makeText(context, R.string.location_service_disabled, Toast.LENGTH_SHORT).show();
                return;
            }
            LocationListener listener = new LocationUpdater(locationManager, context);
            locationManager.requestLocationUpdates(provider, 0, 1000, listener);
        }

    }

    public static void updateLocation(Location location) {
        PhoneConfiguration.getInstance().location = location;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static long distanceBetween(Location l1, String lati2, String longi2) {
        return distanceBetween(l1, Double.parseDouble(lati2), Double.parseDouble(longi2));
    }

    public static long distanceBetween(Location l1, double lati2, double longi2) {
        double radLat1 = rad(l1.getLatitude());
        double radLat2 = rad(lati2);
        double a = radLat1 - radLat2;
        double b = rad(l1.getLongitude()) - rad(longi2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;

        return Math.round(s * 1000);
    }

    public static void appendStaticBoard(BoardHolder boards) {
        if (null == boards)
            return;

    }

    static public String getSaying() {
        String str = StringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            str = str.replace(";", "-----");
        }

        return str;

    }

    public static void dismissSaying(FragmentActivity fa) {
        if (null == fa)
            return;
        FragmentManager fm = fa.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(ActivityUtils.dialogTag);
        if (prev != null) {
            ft.remove(prev);

        }

        ft.commit();
    }

    public void setFullScreen(View v) {
    }

    public void noticeSaying(Context context) {

        String str = StringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            notice("", str.replace(";", "-----"), context);
        } else {
            notice("", str, context);
        }
    }

    public void noticeSayingWithProgressBar(Context context) {

        String str = StringUtils.getSaying();
        if (str.indexOf(";") != -1) {
            noticebar("", str.replace(";", "-----"), context);
        } else {
            noticebar("", str, context);
        }
    }

    public void noticeSaying(String str, Context context) {

        if (str.indexOf(";") != -1) {
            notice("", str.replace(";", "-----"), context);
        } else {
            notice("", str, context);
        }
    }

    public void noticeError(String error, Context context) {
        if (context != null) {
            HttpUtil.switchServer();
            notice(context.getString(R.string.error), error, context);
        }
    }

    private void notice(String title, String content, Context c) {

        if (c == null)
            return;
        NLog.d(TAG, "saying dialog");
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("content", content);
        synchronized (lock) {
            try {

                DialogFragment df = new SayingDialogFragment();
                df.setArguments(b);

                FragmentActivity fa = (FragmentActivity) c;
                FragmentManager fm = fa.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(dialogTag);
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.commit();
                df.show(fm, dialogTag);
                this.df = df;
            } catch (Exception e) {
                NLog.e(this.getClass().getSimpleName(), NLog.getStackTraceString(e));

            }

        }

    }

    private void noticebar(String title, String content, Context c) {

        if (c == null)
            return;
        NLog.d(TAG, "saying dialog");
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("content", content);
        synchronized (lock) {
            try {

                DialogFragment df = new SayingDialogFragmentWithProgressBar();
                df.setArguments(b);

                FragmentActivity fa = (FragmentActivity) c;
                FragmentManager fm = fa.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(dialogTag);
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.commit();
                df.show(fm, dialogTag);
                this.df = df;
            } catch (Exception e) {
                NLog.e(this.getClass().getSimpleName(), NLog.getStackTraceString(e));

            }

        }

    }

    public void noticebarsetprogress(int i) {
        NLog.d(TAG, "trying setprocess" + String.valueOf(i));
        if (df != null && df.getActivity() != null) {
            if (df instanceof SayingDialogFragmentWithProgressBar) {
                ((SayingDialogFragmentWithProgressBar) df).setProgress(i);
            }
        }
    }

    public void clear() {
        synchronized (lock) {
            this.df = null;
        }
    }

    public void dismiss() {

        synchronized (lock) {
            NLog.d(TAG, "trying dissmiss dialog");


            if (df != null && df.getActivity() != null) {
                NLog.d(TAG, "dissmiss dialog");

                try {
                    FragmentActivity fa = (FragmentActivity) (df.getActivity());
                    FragmentManager fm = fa.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    Fragment prev = fm.findFragmentByTag(dialogTag);
                    if (prev != null) {
                        ft.remove(prev);

                    }

                    ft.commit();
                } catch (Exception e) {
                    NLog.e(this.getClass().getSimpleName(), NLog.getStackTraceString(e));
                }

                df = null;


            } else {
                df = null;
            }

        }
    }

    public static class SayingDialogFragmentWithProgressBar extends DialogFragment {

        ProgressDialog dialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new ProgressDialog(getActivity());
            //
            Bundle b = getArguments();
            if (b != null) {
                String title = b.getString("title");
                String content = b.getString("content");
                dialog.setTitle(title);
                if (StringUtils.isEmpty(content))
                    content = ActivityUtils.getSaying();
                dialog.setMessage(content);
            }


            dialog.setCanceledOnTouchOutside(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(true);


            // etc...
            this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
            return dialog;
        }

        public void setProgress(int i) {
            if (dialog != null) {
                dialog.setProgress(i);
            }
        }

    }

    public static class SayingDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            //
            Bundle b = getArguments();
            if (b != null) {
                String title = b.getString("title");
                String content = b.getString("content");
                dialog.setTitle(title);
                if (StringUtils.isEmpty(content))
                    content = ActivityUtils.getSaying();
                dialog.setMessage(content);
            }


            dialog.setCanceledOnTouchOutside(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);


            // etc...
            this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
            return dialog;
        }


    }

}
