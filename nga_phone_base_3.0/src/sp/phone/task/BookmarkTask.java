package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.Utils;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.StringUtils;

public class BookmarkTask extends AsyncTask<String, Integer, String> {
    private final String url = Utils.getNGAHost() + "nuke.php?__lib=topic_favor&lite=js&noprefix&__act=topic_favor&action=add&tid=";
    private Context context;

    public BookmarkTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String tid = params[0];
        HttpPostClient c = new HttpPostClient(url + tid);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);
        String body = "__lib=topic_favor&__act=topic_favor&lite=js&noprefix&action=add&tid=" + tid;

        String ret = null;
        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(body);
            if (conn != null)
                input = conn.getInputStream();

            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                ret = html;//getPostResult(html);

            }
        } catch (IOException e) {
        }
        return ret;
    }

    @Override
    protected void onPreExecute() {
        ActivityUtils.getInstance().noticeSaying(context);
    }

    @Override
    protected void onPostExecute(String result) {
        ActivityUtils.getInstance().dismiss();
        if (StringUtils.isEmpty(result))
            return;

        String msg = StringUtils.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
        if (!StringUtils.isEmpty(msg)) {
            Toast.makeText(context, msg.trim(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled(String result) {
        this.onCancelled();
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
    }
}
