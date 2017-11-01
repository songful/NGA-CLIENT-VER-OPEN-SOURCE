package sp.phone.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;


public class ProgressDialogFragment extends DialogFragment {

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


        //dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        //dialog.setCancelable(true);


        // etc...
        this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
        return dialog;
    }

}
