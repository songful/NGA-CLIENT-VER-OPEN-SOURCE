package sp.phone.model;

import java.util.ArrayList;

/**
 * 获取版块列表
 * Created by elrond on 2017/9/29.
 */

public class ForumsListModel {
    public int code;
    public String msg;
    public ArrayList<Result> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<Result> getResult() {
        return result;
    }

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public static class Result {
        public String id;
        public String name;
        public ArrayList<Group> groups;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Group> getGroups() {
            return groups;
        }

        public void setGroups(ArrayList<Group> groups) {
            this.groups = groups;
        }
    }

    public static class Group {
        public String id;
        public String name;
        public ArrayList<Forum> forums;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Forum> getForums() {
            return forums;
        }

        public void setForums(ArrayList<Forum> forums) {
            this.forums = forums;
        }
    }

    public static class Forum {
        public int id;
        public String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Forum forum = (Forum) o;

            if (id != forum.id) return false;
            return name != null ? name.equals(forum.name) : forum.name == null;

        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
