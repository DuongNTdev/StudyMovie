package yepeer.le.objects;

import yepeer.le.Utils;

/**
 * Created by duongnt on 8/5/15.
 */
public class SubtitleModel {
    private long id;
    private String subtitle;
    private long time;
    public SubtitleModel() {
    }

    public SubtitleModel(long id, String subtitle, long time) {
        this.id = id;
        this.subtitle = subtitle;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SubtitleModel) {
            SubtitleModel orther = (SubtitleModel)o;
            if (Utils.getTimeString(orther.getTime()).equals(Utils.getTimeString(time))) return true;
            if(orther.getTime() - time <= 3000 && orther.getSubtitle().equals(subtitle)) return true;
        }
        return false;
    }
}
