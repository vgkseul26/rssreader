package com.usv.rssreader.rss;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Ser on 12.12.2015.
 */
public class RSSNote implements Parcelable{
    private String title;
    private String description;
    private String link;
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public RSSNote createFromParcel(Parcel in) {
            return new RSSNote(in);
        }

        public RSSNote[] newArray(int size) {
            return new RSSNote[size];
        }
    };

    public RSSNote(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    private RSSNote(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.link = in.readString();
    }

    public RSSNote(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RSSNote)) return false;

        RSSNote rssNote = (RSSNote) o;

        if (title != null ? !title.equals(rssNote.title) : rssNote.title != null) return false;
        if (description != null ? !description.equals(rssNote.description) : rssNote.description != null)
            return false;
        return !(link != null ? !link.equals(rssNote.link) : rssNote.link != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(link);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return  title + '\n' +
                description + '\n' +
                link + '\n' ;
    }
}
