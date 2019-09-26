package site.qutayba.wheelsy.models;

import android.text.format.DateUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Trip implements IModel, Serializable {

    private String id;
    private String name;
    private String description;
    private float distance;
    private Date date;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getFormattedDistance() {
        return new DecimalFormat("#").format(distance);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormattedDate() {
        long time = date != null ? date.getTime() : System.currentTimeMillis();
        long now = System.currentTimeMillis();

        CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
        return ago.toString();
    }
}
