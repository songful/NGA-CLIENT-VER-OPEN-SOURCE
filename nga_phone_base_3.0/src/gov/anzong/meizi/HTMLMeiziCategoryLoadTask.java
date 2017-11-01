package gov.anzong.meizi;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sp.phone.utils.ActivityUtils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.StringUtils;

public class HTMLMeiziCategoryLoadTask extends
        AsyncTask<String, Integer, List<MeiziUrlData>> {
    @SuppressWarnings("unused")
    private final static String TAG = HTMLMeiziCategoryLoadTask.class
            .getSimpleName();
    private static String moe52 = "http://www.52moe.net/";
    private static String rosmm = "http://www.rosmm.com";
    @SuppressWarnings("unused")
    private final Context context;
    final private OnMeiziCategoryLoadFinishedListener notifier;
    private boolean isrosmm = false;
    private int nextsid;
    public HTMLMeiziCategoryLoadTask(Context context,
                                     OnMeiziCategoryLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected List<MeiziUrlData> doInBackground(String... params) {
        String url = params[0];
        String htmlString = "";

        if (url.toLowerCase(Locale.US).indexOf("52moe") > 0) {
            htmlString = MeiziHttpUtil.getHtmlFormeizi(url, PhoneConfiguration
                    .getInstance().getDb_Cookie());
            if (!StringUtils.isEmpty(htmlString)) {
                MOE52CategoryDecoder mDecoder = new MOE52CategoryDecoder();
                List<MeiziUrlData> result;
                result = mDecoder.decode(htmlString);
                return result;
            }
        }
        if (url.toLowerCase(Locale.US).indexOf("rosmm") > 0) {
            isrosmm = true;
            if (url.toLowerCase(Locale.US).indexOf("ajax.php") < 0) {
                htmlString = MeiziHttpUtil.getHtmlFormeizi(url,
                        PhoneConfiguration.getInstance().getDb_Cookie(),
                        "http://www.rosmm.com/");
                if (!StringUtils.isEmpty(htmlString)) {
                    String sid = StringUtils.getStringBetween(htmlString, 0,
                            "var endid = \"", "\"").result;
                    try {
                        nextsid = Integer.parseInt(sid);
                    } catch (Exception e) {

                    }
                    ROSMMADMINCategoryDecoder mDecoder = new ROSMMADMINCategoryDecoder();
                    List<MeiziUrlData> result;
                    result = mDecoder.decode(htmlString);
                    return result;
                }
            } else {
                htmlString = MeiziHttpUtil.getHtmlFormeizi(url,
                        PhoneConfiguration.getInstance().getDb_Cookie(),
                        "http://www.rosmm.com/");
                if (!StringUtils.isEmpty(htmlString)) {
                    String sid = StringUtils.getStringBetween(htmlString, 0,
                            "$$$", "\"").result;
                    try {
                        nextsid = Integer.parseInt(sid);
                    } catch (Exception e) {

                    }
                    ROSMMCategoryDecoder mDecoder = new ROSMMCategoryDecoder();
                    List<MeiziUrlData> result;
                    result = mDecoder.decode(htmlString);
                    return result;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MeiziUrlData> result) {
        ActivityUtils.getInstance().dismiss();
        if (null != notifier) {
            if (isrosmm && nextsid >= 0) {
                notifier.datafinishLoad(result, nextsid);
            } else {
                notifier.datafinishLoad(result);
            }
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }

    public static class MOE52CategoryDecoder extends
            MeiziHtmlDecoderBase<List<MeiziUrlData>> {

        @Override
        public List<MeiziUrlData> decode(String html) {
            List<MeiziUrlData> result = new ArrayList<MeiziUrlData>();

            if (!TextUtils.isEmpty(html)) {
                Document document = Jsoup.parse(html);

                Elements meiziElements = document.select("div.enpty-home");
                for (int i = 0; i < meiziElements.size(); i++) {
                    Element meiziE = meiziElements.get(i);
                    MeiziUrlData meiziM = new MeiziUrlData();
                    meiziM.dataId = "";
                    meiziM.smallPicUrl = meiziE.select("img").attr("src");
                    meiziM.largePicUrl = meiziE.select("img").attr("src");
                    // TODO [Ou Runqiang] starCount might be loaded from a js
                    // script
                    // meiziM.starCount =
                    Elements urlHolder = meiziE.select("div.pic").select("a");
                    String topicUrl = urlHolder.attr("href");
                    if (!TextUtils.isEmpty(topicUrl)) {
                        meiziM.TopicUrl = topicUrl;
                    }
                    if (!topicUrl.toLowerCase(Locale.US).equals(
                            "http://www.52moe.net/?p=2473") && !StringUtils.isEmpty(meiziM.smallPicUrl))
                        result.add(meiziM);
                }
            }

            return result;
        }
    }

    public static class ROSMMCategoryDecoder extends
            MeiziHtmlDecoderBase<List<MeiziUrlData>> {

        @Override
        public List<MeiziUrlData> decode(String html) {
            List<MeiziUrlData> result = new ArrayList<MeiziUrlData>();

            if (!TextUtils.isEmpty(html)) {
                Document document = Jsoup.parse(html);

                Elements meiziElements = document.select("li");
                for (int i = 0; i < meiziElements.size(); i++) {

                    Element meiziE = meiziElements.get(i);
                    String img = meiziE.select("img").first().attr("src");
                    String url = meiziE.select("a").first().attr("href");

                    MeiziUrlData meiziM = new MeiziUrlData();
                    meiziM.dataId = "";
                    if (!StringUtils.isEmpty(meiziE.select("img").first()
                            .attr("src"))
                            && !StringUtils.isEmpty(meiziE.select("a").first()
                            .attr("href"))) {

                        meiziM.smallPicUrl = meiziE.select("img").first()
                                .attr("src");
                        meiziM.largePicUrl = meiziE.select("img").first()
                                .attr("src");
                        String topicUrl = rosmm + meiziE.select("a").first()
                                .attr("href");
                        meiziM.TopicUrl = topicUrl;
                        result.add(meiziM);
                    }
                }
            }

            return result;
        }
    }


    public static class ROSMMADMINCategoryDecoder extends
            MeiziHtmlDecoderBase<List<MeiziUrlData>> {

        @Override
        public List<MeiziUrlData> decode(String html) {
            List<MeiziUrlData> result = new ArrayList<MeiziUrlData>();

            if (!TextUtils.isEmpty(html)) {
                Document document = Jsoup.parse(html);

                Elements meiziElements = document.select("ul#sliding").select("li");
                for (int i = 0; i < meiziElements.size(); i++) {

                    Element meiziE = meiziElements.get(i);
                    String img = meiziE.select("img").first().attr("src");
                    String url = meiziE.select("a").first().attr("href");

                    MeiziUrlData meiziM = new MeiziUrlData();
                    meiziM.dataId = "";
                    if (!StringUtils.isEmpty(meiziE.select("img").first()
                            .attr("src"))
                            && !StringUtils.isEmpty(meiziE.select("a").first()
                            .attr("href"))) {

                        meiziM.smallPicUrl = meiziE.select("img").first()
                                .attr("src");
                        meiziM.largePicUrl = meiziE.select("img").first()
                                .attr("src");
                        String topicUrl = rosmm + meiziE.select("a").first()
                                .attr("href");
                        meiziM.TopicUrl = topicUrl;
                        result.add(meiziM);
                    }
                }
            }

            return result;
        }
    }
}
