package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;

public class BoardManagerImpl implements BoardManager {

    private Context mContext;

    private List<BoardCategory> mCategoryList;

    private static class BoardManagerHolder {
        static BoardManager sInstance = new BoardManagerImpl();
    }

    public static BoardManager getInstance() {
        return BoardManagerHolder.sInstance;
    }

    @Override
    public void initialize(Context context) {
        mContext = context.getApplicationContext();
        mCategoryList = new ArrayList<>();
        loadBookmarkBoards();
        loadPreloadBoards();
    }

    private void loadPreloadBoards() {
        BoardCategory category = new BoardCategory("综合讨论");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("7", "议事厅", 7, 7));
        category.add(new Board("323", "国服以外讨论", 323, 323));
        category.add(new Board("10", "银色黎明", 10, 10));
        category.add(new Board("230", "艾泽拉斯风纪委员会", 230, 230));
        category.add(new Board("387", "潘大力亚之迷雾", 387, 387));
        category.add(new Board("430", "德拉诺之王", 430, 430));
        category.add(new Board("305", "305权贵区", 305));
        category.add(new Board("11", "诺森德埋骨地", 11));

        category = new BoardCategory("大漩涡系列");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("-7", "大漩涡", 354, 354));
        category.add(new Board("-343809", "汽车俱乐部", 343809, 343809));
        category.add(new Board("-81981", "生命之杯", 81981, 81981));
        category.add(new Board("-576177", "影音讨论区", 576177));
        category.add(new Board("-43", "军事历史", 43));
        category.add(new Board("414", "游戏综合讨论", 414, 414));
        category.add(new Board("415", "主机游戏综合讨论", 415));
        category.add(new Board("427", "怪物猎人", 427, 427));
        category.add(new Board("431", "风暴英雄", 431, 431));
        category.add(new Board("436", "消费电子 IT新闻", 436));
        category.add(new Board("498", "二手交易", 498));
        category.add(new Board("340", "无聊图", 340));
        category.add(new Board("456", "冲水区", 456));
        category.add(new Board("-187579", "大漩涡历史博物馆", 187579));
        category.add(new Board("485", "篮球", 485, 485));
        category.add(new Board("491", "议会", 491));


        category = new BoardCategory("职业讨论区");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("390", "五晨寺", 390, 390));
        category.add(new Board("320", "黑锋要塞", 320, 320));
        category.add(new Board("181", "铁血沙场", 181, 181));
        category.add(new Board("182", "魔法圣堂", 182, 182));
        category.add(new Board("183", "信仰神殿", 183, 183));
        category.add(new Board("185", "风暴祭坛", 185, 185));
        category.add(new Board("186", "翡翠梦境", 186, 186));
        category.add(new Board("187", "猎手大厅", 187, 187));
        category.add(new Board("184", "圣光之力", 184, 184));
        category.add(new Board("188", "恶魔深渊", 188, 188));
        category.add(new Board("189", "暗影裂口", 189, 189));
        category.add(new Board("477", "伊利达雷", 477, 477));


        category = new BoardCategory("冒险心得");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("310", "精英议会", 310, 310));
        category.add(new Board("190", "任务讨论", 190, 190));
        category.add(new Board("213", "战争档案", 213, 213));
        category.add(new Board("218", "副本专区", 218, 218));
        category.add(new Board("258", "战场讨论", 258, 258));
        category.add(new Board("272", "竞技场", 272, 272));
        category.add(new Board("191", "地精商会", 191, 191));
        category.add(new Board("200", "插件研究", 200, 200));
        category.add(new Board("240", "BigFoot", 240, 240));
        category.add(new Board("460", "大脚综合", 460));
        category.add(new Board("461", "大脚水区", 461));
        category.add(new Board("458", "大脚LOL", 458));
        category.add(new Board("274", "插件发布", 274, 274));
        category.add(new Board("315", "战斗统计", 315, 315));
        category.add(new Board("333", "DKP系统", 333, 333));
        category.add(new Board("327", "成就讨论", 327, 327));
        category.add(new Board("388", "幻化讨论", 388, 388));
        category.add(new Board("411", "宠物讨论", 411, 411));
        category.add(new Board("463", "要塞讨论", 463));
        category.add(new Board("255", "公会管理", 255));
        category.add(new Board("306", "人员招募", 306));

        category = new BoardCategory("麦迪文之塔");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("264", "卡拉赞剧院", 264, 264));
        category.add(new Board("8", "大图书馆", 8, 8));
        category.add(new Board("102", "作家协会", 102, 102));
        category.add(new Board("124", "壁画洞窟", 124));
        category.add(new Board("254", "镶金玫瑰", 254, 254));
        category.add(new Board("355", "龟岩兄弟会", 355, 355));
        category.add(new Board("116", "奇迹之泉", 116, 116));

        category = new BoardCategory("系统软硬件讨论");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("193", "帐号安全", 193, 193));
        category.add(new Board("334", "PC软硬件", 334, 334));
        category.add(new Board("201", "系统问题", 201, 201));
        category.add(new Board("335", "网站开发", 335, 335));

        category = new BoardCategory("其他游戏");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("414", "游戏综合讨论", 414, 414));
        category.add(new Board("428", "手机游戏", 428, 428));
        category.add(new Board("431", "风暴英雄", 431, 431));
        category.add(new Board("-452227", "口袋妖怪", 452227, 452227));
        category.add(new Board("426", "智龙迷城", 426, 426));
        category.add(new Board("-51095", "部落冲突", 51095, 51095));
        category.add(new Board("492", "部落冲突:皇室战争", 492, 492));
        category.add(new Board("-362960", "最终幻想14", 362960, 362960));
        category.add(new Board("-6194253", "战争雷霆", 6194253, 6194253));
        category.add(new Board("427", "怪物猎人", 427, 427));
        category.add(new Board("-47218", "地下城与勇士", 47218));
        category.add(new Board("425", "行星边际2", 425, 425));
        category.add(new Board("422", "炉石传说", 422, 422));
        category.add(new Board("-65653", "剑灵", 65653, 65653));
        category.add(new Board("412", "巫师之怒", 412, 412));
        category.add(new Board("-235147", "激战2", 235147, 235147));
        category.add(new Board("442", "逆战", 442, 442));
        category.add(new Board("-46468", "洛拉斯的战争世界", 46468, 46468));
        category.add(new Board("483", "洛拉斯的战争世界:插件", 46468, 46468));
        category.add(new Board("432", "战机世界", 432, 432));
        category.add(new Board("441", "战舰世界", 441));
        category.add(new Board("321", "DotA", 321, 321));
        category.add(new Board("375", "DotA联赛饰品", 375));
        category.add(new Board("-2371813", "EVE", 2371813, 2371813));
        category.add(new Board("-7861121", "剑叁 ", 7861121, 7861121));
        category.add(new Board("448", "剑叁同人 ", 448));
        category.add(new Board("-793427", "斗战神", 793427));
        category.add(new Board("332", "战锤40K", 332, 332));
        category.add(new Board("416", "火炬之光2", 416));
        category.add(new Board("406", "星际争霸2", 406));
        category.add(new Board("420", "MT Online", 420, 420));
        category.add(new Board("424", "圣斗士星矢", 424));
        category.add(new Board("-1513130", "鲜血兄弟会", 1513130));
        category.add(new Board("433", "神雕侠侣", 433));
        category.add(new Board("434", "神鬼幻想", 434));
        category.add(new Board("435", "上古卷轴Online", 435, 435));
        category.add(new Board("443", "FIFA Online 3", 443));
        category.add(new Board("444", "刀塔传奇", 444, 444));
        category.add(new Board("445", "迷你西游", 445, 445));
        category.add(new Board("447", "锁链战记", 447));
        category.add(new Board("-532408", "沃土", 532408, 532408));
        category.add(new Board("353", "纽沃斯英雄传", 353));
        category.add(new Board("452", "天涯明月刀", 452, 452));
        category.add(new Board("453", "魔力宝贝", 453));
        category.add(new Board("454", "神之浩劫", 454));
        category.add(new Board("455", "鬼武者 魂", 455));
        category.add(new Board("480", "百万亚瑟王", 480));
        category.add(new Board("481", "Minecraft", 481));
        category.add(new Board("482", "CS", 482));
        category.add(new Board("484", "热血江湖传", 484));
        category.add(new Board("486", "辐射", 486));
        category.add(new Board("487", "刀剑魔药2", 487));
        category.add(new Board("488", "村长打天下", 488));
        category.add(new Board("493", "刀塔战纪", 493));
        category.add(new Board("494", "魔龙之魂", 494));
        category.add(new Board("495", "光荣三国志系列", 495));
        category.add(new Board("496", "九十九姬", 496));

        category = new BoardCategory("暗黑破坏神");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("318", "暗黑破坏神3", 318, 318));
        category.add(new Board("403", "购买/安装/共享", 403, 403));
        category.add(new Board("393", "背景故事与文艺作品", 393, 393));
        category.add(new Board("400", "职业讨论区", 400));
        category.add(new Board("395", "野蛮人", 395, 395));
        category.add(new Board("396", "猎魔人", 396, 396));
        category.add(new Board("397", "武僧", 397, 397));
        category.add(new Board("398", "巫医", 398, 398));
        category.add(new Board("399", "魔法师", 399, 399));
        category.add(new Board("446", "圣教军", 446));

        category = new BoardCategory("暴雪游戏");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("422", "炉石传说", 422, 422));
        category.add(new Board("429", "战术讨论", 429));
        category.add(new Board("450", "文章存档", 450));
        category.add(new Board("431", "风暴英雄", 431, 431));
        category.add(new Board("457", "视频资料", 457));
        category.add(new Board("459", "守望先锋", 459, 459));
        category.add(new Board("490", "魔兽争霸", 490));

        category = new BoardCategory("英雄联盟");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("-152678", "英雄联盟", 152678, 152678));
        category.add(new Board("418", "游戏视频", 418));
        category.add(new Board("479", "赛事讨论", 152678, 152678));

        category = new BoardCategory("个人版面");
        category.setCategoryIndex(mCategoryList.size());
        mCategoryList.add(category);
        category.add(new Board("-447601", "二次元国家地理", 447601, 447601));
        category.add(new Board("-84", "模玩之魂", 84, 84));
        category.add(new Board("-8725919", "小窗视界", 8725919, 8725919));
        category.add(new Board("-965240", "树洞", 965240));
        category.add(new Board("-131429", "红茶馆——小说馆", 131429));
        category.add(new Board("-608808", "血腥厨房", 608808));
        category.add(new Board("-469608", "影~视~秀", 469608));
        category.add(new Board("-55912", "音乐讨论", 55912));
        category.add(new Board("-522474", "综合体育讨论区", 522474));
        category.add(new Board("-1068355", "晴风村", 1068355));
        category.add(new Board("-168888", "育儿版", 168888));
        category.add(new Board("-54214", "时尚板", 54214));
        category.add(new Board("-353371", "宠物养成", 353371));
        category.add(new Board("-538800", "乙女向二次元", 538800));
        category.add(new Board("-7678526", "麻将科学院", 7678526));
        category.add(new Board("-202020", "程序员职业交流", 202020));
        category.add(new Board("-444012", "我们的骑迹", 444012));
        category.add(new Board("-349066", "开心茶园", 349066));
        category.add(new Board("-314508", "世界尽头的百货公司", 314508));
        category.add(new Board("-2671", "耳机区", 2671));
        category.add(new Board("-970841", "东方教主陈乔恩", 970841));
        category.add(new Board("-3355501", "基腐版", 3355501));
        category.add(new Board("-403298", "怨灵图纸收藏室", 403298));
        category.add(new Board("-3432136", "飘落的诗章", 3432136));
        category.add(new Board("-187628", "家居 装修", 187628));
        category.add(new Board("-8627585", "牛头人酋长乐队", 8627585));
        category.add(new Board("-17100", "全民健身中心", 395));
    }

    private void loadBookmarkBoards() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        BoardCategory category = new BoardCategory("我的收藏");
        String bookmarkStr = sp.getString(PreferenceKey.BOOKMARK_BOARD, null);
        if (bookmarkStr != null) {
            List<Board> boards = JSON.parseArray(bookmarkStr, Board.class);
            if (!boards.isEmpty()) {
                category.getBoardList().addAll(boards);
            }
        }
        mCategoryList.add(category);
    }

    @Override
    public List<BoardCategory> getCategoryList() {
        return mCategoryList;
    }

    @Override
    public int getCategorySize() {
        return mCategoryList.size();
    }

    @Override
    public BoardCategory getCategory(int index) {
        return mCategoryList.get(index);
    }

    @Override
    public void addBookmark(String fid, String name) {
        Board board = getBoard(fid);
        if (board != null) {
            mCategoryList.get(0).add(board);
        } else {
            mCategoryList.get(0).add(new Board(fid, name));
        }
        saveBookmark();
    }

    @Override
    public void removeBookmark(String fid) {
        mCategoryList.get(0).remove(fid);
        saveBookmark();
    }

    @Override
    public void removeBookmark(int index) {
        mCategoryList.get(0).remove(index);
        saveBookmark();
    }

    private void saveBookmark() {
        BoardCategory category = mCategoryList.get(0);
        SharedPreferences sp = mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString(PreferenceKey.BOOKMARK_BOARD, JSON.toJSONString(category.getBoardList())).apply();
    }

    @Override
    public void removeAllBookmarks() {
        mCategoryList.get(0).removeAll();
        mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE).edit().putString(PreferenceKey.BOOKMARK_BOARD, null).apply();
    }

    @Override
    public String getBoardName(String fid) {
        Board board = getBoard(fid);
        return board != null ? board.getName() : null;
    }

    @Override
    public boolean isBookmarkBoard(String fid) {
        for (Board board : mCategoryList.get(0).getBoardList()) {
            if (board.getUrl().equals(fid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void swapBookmark(int from, int to) {
        List<Board> boards = mCategoryList.get(0).getBoardList();
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(boards, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(boards, i, i - 1);
            }
        }
        saveBookmark();
    }

    private Board getBoard(String fid) {
        for (BoardCategory category : mCategoryList) {
            for (Board board : category.getBoardList()) {
                if (board.getUrl().equals(fid)) {
                    return board;
                }
            }
        }
        return null;
    }
}
