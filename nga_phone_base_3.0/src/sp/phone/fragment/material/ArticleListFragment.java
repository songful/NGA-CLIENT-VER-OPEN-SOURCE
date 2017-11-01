package sp.phone.fragment.material;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.Set;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.adapter.material.ArticleListAdapter;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ArticleListAction;
import sp.phone.fragment.BaseFragment;
import sp.phone.fragment.PostCommentDialogFragment;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.model.ArticleListTask;
import sp.phone.task.LikeTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

/*
 * MD 帖子详情每一页
 */
public class ArticleListFragment extends BaseFragment {

    private final static String TAG = sp.phone.fragment.ArticleListFragment.class.getSimpleName();

    private RecyclerView mListView;

    private ArticleListAdapter mArticleAdapter;

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback;

    private ArticleListAction mArticleListAction;

    private int mPage;

    private ArticleListTask mTask;

    private ThreadData mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        NLog.d(TAG, "onCreate");
        if (savedInstanceState != null) {
            mPage = savedInstanceState.getInt("page");
        }
        mArticleListAction = getArguments().getParcelable("ArticleListAction");
        if (mArticleListAction != null)
            mArticleListAction.setPageFromUrl(mPage);
        mTask = new ArticleListTask();
        super.onCreate(savedInstanceState);
    }

    public void setPage(int page) {
        mPage = page;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("page", mPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (RecyclerView) view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setItemViewCacheSize(20);
        mArticleAdapter = new ArticleListAdapter(getContext());
        mArticleAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mArticleAdapter.setSelectedItem(position);
                if (mActionModeCallback != null) {
                    ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
                    return true;
                }
                return false;
            }
        });
        mListView.setAdapter(mArticleAdapter);
        activeActionMode();
        loadPage();
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadPage() {
        mArticleListAction.setPageFromUrl(mPage);
        showProgress();
        mTask.loadPage(getContext(), mArticleListAction, new OnThreadPageLoadFinishedListener() {
            @Override
            public void finishLoad(ThreadData data) {
                if (data != null) {
                    setData(data);
                    dismiss();
                }
            }
        });

    }

    private void activeActionMode() {
        mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                if (mArticleListAction.getPid() == 0) {
                    inflater.inflate(R.menu.article_list_context_menu, menu);
                } else {
                    inflater.inflate(R.menu.article_list_context_menu_with_tid, menu);
                }
                int position = mArticleAdapter.getSelectedItem();
                ThreadRowInfo row = new ThreadRowInfo();
                if (position < mArticleAdapter.getItemCount()) {
                    row = (ThreadRowInfo) mArticleAdapter.getItem(position);
                }

                MenuItem mi = menu.findItem(R.id.menu_ban_this_one);
                if (mi != null && row != null) {
                    if (row.get_isInBlackList()) {// 处于屏蔽列表，需要去掉
                        mi.setTitle(R.string.cancel_ban_thisone);
                    } else {
                        mi.setTitle(R.string.ban_thisone);
                    }
                }
                MenuItem voteMenu = menu.findItem(R.id.menu_vote);
                if (voteMenu != null && StringUtils.isEmpty(row.getVote())) {
                    menu.removeItem(R.id.menu_vote);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                mActionMode = mode;
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                onContextItemSelected(item);
                mode.finish();
                mActionMode = null;
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

        };
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int page = mArticleListAction.getPageFromUrl();
        int tid = mArticleListAction.getTid();
        NLog.d(TAG, "onContextItemSelected,tid=" + tid + ",page=" + page);

        if (!getUserVisibleHint()) {
            return false;
        }

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = mArticleAdapter.getSelectedItem();
        if (info != null) {
            position = info.position;
        }
        if (position < 0 || position >= mArticleAdapter.getItemCount()) {
            showToast(R.string.floor_error);
            position = 0;
        }
        StringBuilder postPrefix = new StringBuilder();
        String tidStr = String.valueOf(tid);

        ThreadRowInfo row = (ThreadRowInfo) mArticleAdapter.getItem(position);
        if (row == null) {
            showToast(R.string.unknow_error);
            return true;
        }
        String content = row.getContent();
        final String name = row.getAuthor();
        final String uid = String.valueOf(row.getAuthorid());
        boolean isAnonymous = row.getISANONYMOUS();
        String mention = null;
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_quote_subject:
                final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                content = content.replaceAll(quote_regex, "");
                content = content.replaceAll(replay_regex, "");
                final String postTime = row.getPostdate();

                content = FunctionUtils.checkContent(content);
                content = StringUtils.unEscapeHtml(content);
                if (row.getPid() != 0) {
                    mention = name;
                    postPrefix.append("[quote][pid=");
                    postPrefix.append(row.getPid());
                    postPrefix.append(',').append(tidStr).append(",").append(page);
                    postPrefix.append("]");// Topic
                    postPrefix.append("Reply");
                    if (row.getISANONYMOUS()) {// 是匿名的人
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append("-1");
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid][color=gray](");
                        postPrefix.append(row.getLou());
                        postPrefix.append("楼)[/color] (");
                    } else {
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append(uid);
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid] (");
                    }
                    postPrefix.append(postTime);
                    postPrefix.append("):[/b]\n");
                    postPrefix.append(content);
                    postPrefix.append("[/quote]\n");
                }

                if (!StringUtils.isEmpty(mention))
                    intent.putExtra("mention", mention);
                intent.putExtra("prefix", StringUtils.removeBrTag(postPrefix.toString()));
                intent.putExtra("tid", tidStr);
                intent.putExtra("action", "reply");
                if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
                }
                getParentFragment().startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
                break;

            case R.id.menu_signature:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    FunctionUtils.Create_Signature_Dialog(row, getActivity(),
                            mListView);
                }
                break;

            case R.id.menu_vote:
                FunctionUtils.createVoteDialog(row, getActivity(), mListView, mToast);
                break;

            case R.id.menu_ban_this_one:
                if (isAnonymous) {
                    showToast(R.string.cannot_add_to_blacklist_cause_anony);
                } else {
                    Set<Integer> blacklist = PhoneConfiguration.getInstance().blacklist;
                    String blackListString;
                    if (row.get_isInBlackList()) {// 在屏蔽列表中，需要去除
                        row.set_IsInBlackList(false);
                        blacklist.remove(row.getAuthorid());
                        showToast(R.string.remove_from_blacklist_success);
                    } else {
                        row.set_IsInBlackList(true);
                        blacklist.add(row.getAuthorid());
                        showToast(R.string.add_to_blacklist_success);
                    }
                    PhoneConfiguration.getInstance().blacklist = blacklist;
                    blackListString = blacklist.toString();
                    SharedPreferences share = getActivity().getSharedPreferences(
                            PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = share.edit();
                    editor.putString(PreferenceKey.BLACK_LIST, blackListString);
                    editor.apply();
                    if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().uid)) {
                        NgaClientApp app = (NgaClientApp) getActivity().getApplication();
                        app.upgradeUserdata(blacklist.toString());
                    } else {
                        showToast(R.string.cannot_add_to_blacklist_cause_logout);
                    }
                }
                break;

            case R.id.menu_show_profile:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    intent.putExtra("mode", "username");
                    intent.putExtra("username", row.getAuthor());
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                    startActivity(intent);
                }
                break;

            case R.id.menu_avatar:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    FunctionUtils.Create_Avatar_Dialog(row, getActivity(), mListView);
                }
                break;

            case R.id.menu_edit:
                if (FunctionUtils.isComment(row)) {
                    showToast(R.string.cannot_eidt_comment);
                    break;
                }
                Intent intentModify = new Intent();
                intentModify.putExtra("prefix", StringUtils.unEscapeHtml(StringUtils.removeBrTag(content)));
                intentModify.putExtra("tid", tidStr);
                String pid = String.valueOf(row.getPid());// getPid(map.get("url"));
                intentModify.putExtra("pid", pid);
                intentModify.putExtra("title", StringUtils.unEscapeHtml(row.getSubject()));
                intentModify.putExtra("action", "modify");
                if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivity(intentModify);
                break;

            case R.id.menu_copy:
                FunctionUtils.CopyDialog(row.getFormated_html_data(), getActivity(), mListView);
                break;

            case R.id.menu_show_this_person_only:
                if (null == getActivity().findViewById(R.id.item_detail_container)) {
                    Intent intentThis = new Intent();
                    intentThis.putExtra("tab", "1");
                    intentThis.putExtra("tid", tid);
                    intentThis.putExtra("authorid", row.getAuthorid());
                    intentThis.putExtra("fromreplyactivity", 1);
                    intentThis.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
                    startActivity(intentThis);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                } else {
                    int authorId = row.getAuthorid();
                    sp.phone.fragment.ArticleContainerFragment f = sp.phone.fragment.ArticleContainerFragment
                            .createshowonly(tid, authorId);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    f.setHasOptionsMenu(true);
                    ft.replace(R.id.item_detail_container, f);
                    ft.commit();
                }
                break;

            case R.id.menu_show_whole_thread:
                if (null == getActivity().findViewById(R.id.item_detail_container)) {
                    Intent intentThis = new Intent();
                    intentThis.putExtra("tab", "1");
                    intentThis.putExtra("tid", tid);
                    intentThis.putExtra("fromreplyactivity", 1);
                    intentThis.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
                    startActivity(intentThis);
                } else {
                    sp.phone.fragment.ArticleContainerFragment f = sp.phone.fragment.ArticleContainerFragment
                            .createshowall(tid);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    f.setHasOptionsMenu(true);
                    ft.replace(R.id.item_detail_container, f);
                    ft.commit();
                }
                break;

            case R.id.menu_send_message:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    FunctionUtils.start_send_message(getActivity(), row);
                }
                break;

            case R.id.menu_post_comment:
                final String quote_regex1 = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex1 = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                content = content.replaceAll(quote_regex1, "");
                content = content.replaceAll(replay_regex1, "");
                final String postTime1 = row.getPostdate();
                content = FunctionUtils.checkContent(content);
                content = StringUtils.unEscapeHtml(content);
                if (row.getPid() != 0) {
                    postPrefix.append("[quote][pid=");
                    postPrefix.append(row.getPid());
                    postPrefix.append(',').append(tidStr).append(",").append(mArticleListAction.getPageFromUrl());
                    postPrefix.append("]");// Topic
                    postPrefix.append("Reply");
                    if (row.getISANONYMOUS()) {// 是匿名的人
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append("-1");
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid][color=gray](");
                        postPrefix.append(row.getLou());
                        postPrefix.append("楼)[/color] (");
                    } else {
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append(uid);
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid] (");
                    }
                    postPrefix.append(postTime1);
                    postPrefix.append("):[/b]\n");
                    postPrefix.append(content);
                    postPrefix.append("[/quote]\n");
                }

                Bundle b = new Bundle();
                b.putInt("pid", row.getPid());
                b.putInt("fid", row.getFid());
                b.putInt("tid", mArticleListAction.getTid());

                String prefix = StringUtils.removeBrTag(postPrefix.toString());
                if (!StringUtils.isEmpty(prefix)) {
                    prefix = prefix + "\n";
                }
                showPostCommentDialog(prefix, b);
                break;

            case R.id.menu_report:
                FunctionUtils.handleReport(row, tid, getFragmentManager());
                break;

            case R.id.menu_search_post:
                intent.putExtra("searchpost", 1);
            case R.id.menu_search_subject:
                intent.putExtra("authorid", row.getAuthorid());
                intent.setClass(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
                startActivity(intent);
                break;

            case R.id.menu_share:
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareUrl = Utils.getNGAHost() + "read.php?";
                ThreadRowInfo rowInfo = mData.getRowList().get(position);
                if (rowInfo == null) {
                    showToast(R.string.unknow_error);
                    return true;
                }
                if (rowInfo.getPid() != 0) {
                    shareUrl = shareUrl + "pid=" + rowInfo.getPid() + " (分享自NGA安卓客户端开源版)";
                } else {
                    shareUrl = shareUrl + "tid=" + mArticleListAction.getTid() + " (分享自NGA安卓客户端开源版)";
                }
                String title = mData.getThreadInfo().getSubject();
                if (!StringUtils.isEmpty(title)) {
                    shareUrl = "《" + title + "》 - 艾泽拉斯国家地理论坛，地址：" + shareUrl;
                }
                intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                String text = getContext().getResources().getString(R.string.share);
                getContext().startActivity(Intent.createChooser(intent, text));
                break;

            case R.id.menu_like:
                doLike(tid, row.getPid(), 1);
                break;

            case R.id.menu_dislike:
                doLike(tid, row.getPid(), -1);
                break;

            default:
                break;
        }
        return true;
    }

    private void doLike(int tid, int pid, int value) {
        LikeTask lt = new LikeTask(getActivity(), tid, pid, value);
        lt.execute();
    }

    public void setData(ThreadData data) {
        if (getParentFragment() instanceof OnThreadPageLoadFinishedListener) {
            ((OnThreadPageLoadFinishedListener) getParentFragment()).finishLoad(data);
        } else if (getActivity() instanceof OnThreadPageLoadFinishedListener) {
            ((OnThreadPageLoadFinishedListener) getActivity()).finishLoad(data);
        }
        mData = data;
        mArticleAdapter.setData(data);
        mArticleAdapter.notifyDataSetChanged();

    }

    public void showProgress() {
        if (getUserVisibleHint()) {
            ActivityUtils.getInstance().noticeSaying(getContext());
        }
    }

    public void dismiss() {
        if (getUserVisibleHint()) {
            ActivityUtils.getInstance().dismiss();
        }
    }

    public void showPostCommentDialog(String prefix, Bundle bundle) {
        Intent intent = new Intent();
        final String dialog_tag = "post comment";
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(dialog_tag);
        if (prev != null) {
            ft.remove(prev);
        }
        DialogFragment df = new PostCommentDialogFragment();
        intent.putExtra("prefix", prefix);
        df.setArguments(bundle);
        df.show(ft, dialog_tag);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mActionMode != null) {
            mActionMode.finish();
        }
    }
}
