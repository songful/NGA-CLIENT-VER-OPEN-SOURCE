package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yang Yihang on 2017/7/9.
 */

public class ArticleListAction implements Parcelable{

    int pid;

    int tid;

    int authorId;

    int fromReplyActivity = 0;

    int pageFromUrl;

    public ArticleListAction() {

    }

    protected ArticleListAction(Parcel in) {
        pid = in.readInt();
        tid = in.readInt();
        authorId = in.readInt();
        fromReplyActivity = in.readInt();
        pageFromUrl = in.readInt();
    }

    public static final Creator<ArticleListAction> CREATOR = new Creator<ArticleListAction>() {
        @Override
        public ArticleListAction createFromParcel(Parcel in) {
            return new ArticleListAction(in);
        }

        @Override
        public ArticleListAction[] newArray(int size) {
            return new ArticleListAction[size];
        }
    };

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getFromReplyActivity() {
        return fromReplyActivity;
    }

    public void setFromReplyActivity(int fromReplyActivity) {
        this.fromReplyActivity = fromReplyActivity;
    }

    public int getPageFromUrl() {
        return pageFromUrl;
    }

    public void setPageFromUrl(int pageFromUrl) {
        this.pageFromUrl = pageFromUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pid);
        dest.writeInt(tid);
        dest.writeInt(authorId);
        dest.writeInt(fromReplyActivity);
        dest.writeInt(pageFromUrl);
    }
}
