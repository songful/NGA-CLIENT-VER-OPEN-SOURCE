package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.interfaces.OnMessageDetialLoadFinishedListener;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MessageUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class JsonMessageDetialLoadTask extends AsyncTask<String, Integer, MessageDetailInfo> {
    private final static String TAG = JsonMessageDetialLoadTask.class.getSimpleName();
    private final Context context;
    final private OnMessageDetialLoadFinishedListener notifier;
    private String error;
    @SuppressWarnings("unused")
    private String table;


    public JsonMessageDetialLoadTask(Context context,
                                     OnMessageDetialLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected MessageDetailInfo doInBackground(String... params) {
        if (params.length == 0)
            return null;
        NLog.d(TAG, "start to load " + params[0]);

        String uri = params[0];
        String page = StringUtils.getStringBetween(uri, 0, "page=", "&").result;
        if (StringUtils.isEmpty(page)) {
            page = "1";
        }
        String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
        if (js == null) {
            if (context != null)
                error = context.getResources().getString(R.string.network_error);
            return null;
        }
        js = js.replaceAll("window.script_muti_get_var_store=", "");
        if (js.indexOf("/*error fill content") > 0)
            js = js.substring(0, js.indexOf("/*error fill content"));
        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
        js = js.replaceAll("/\\*\\$js\\$\\*/", "");
        js = js.replaceAll("\\[img\\]./mon_", "[img]http://img6.nga.178.com/attachments/mon_");

        JSONObject o = null;
        try {
            o = (JSONObject) JSON.parseObject(js).get("data");
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
        }
        if (o == null) {

            try {
                o = (JSONObject) JSON.parseObject(js).get("error");
            } catch (Exception e) {
                NLog.e(TAG, "can not parse :\n" + js);
            }
            if (o == null) {
                error = "请重新登录";
            } else {
                error = o.getString("0");
                if (StringUtils.isEmpty(error))
                    error = "请重新登录";
            }
            return null;
        }
        MessageDetailInfo ret = new MessageUtil(context).parseJsonThreadPage(js, Integer.parseInt(page));
        return ret;
    }

    @Override
    protected void onPostExecute(MessageDetailInfo result) {
        ActivityUtils.getInstance().dismiss();
        if (result == null) {
            if (!StringUtils.isEmpty(error))
                ActivityUtils.getInstance().noticeError
                        (error, context);
        }
        if (null != notifier) {
            notifier.finishLoad(result);
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }


}
