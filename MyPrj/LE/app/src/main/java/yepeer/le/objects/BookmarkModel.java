package yepeer.le.objects;

import org.litepal.crud.DataSupport;

/**
 * Created by duongnt on 8/7/15.
 */
public class BookmarkModel extends DataSupport {
    private long id;
    private String subtitle;
    private long date;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public BookmarkModel() {

    }
}
