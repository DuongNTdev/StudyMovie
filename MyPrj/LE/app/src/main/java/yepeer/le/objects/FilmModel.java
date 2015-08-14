package yepeer.le.objects;

import org.litepal.crud.DataSupport;

/**
 * Created by duongnt on 7/26/15.
 */
public class FilmModel extends DataSupport {
    private long id;
    private String filmPath;
    private String subtitlePath;
    private long time;

    public FilmModel() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilmPath() {
        return filmPath;
    }

    public void setFilmPath(String filmPath) {
        this.filmPath = filmPath;
    }

    public String getSubtitlePath() {
        return subtitlePath;
    }

    public void setSubtitlePath(String subtitlePath) {
        this.subtitlePath = subtitlePath;
    }

}
