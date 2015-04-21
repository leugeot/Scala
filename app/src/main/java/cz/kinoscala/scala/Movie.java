package cz.kinoscala.scala;

import java.util.Date;
import java.util.List;

/**
 * Created by petr on 20.4.2015.
 */
public class Movie {
    private long id;
    private String name;
    private String originalName;
    private Date date;
    private int year;
    private int runtime;
    private List<String> countries;
    private String languages;
    private String subtitles;
    private List<String> directors;
    private List<String> actors;
    private String plot;
    private int price;
    private int csfdRating;
    private String csfdID;
    private int imdbRating;
    private String imdbID;
    private String fest;
    private String festUrl;
    private String imageUrl;
    private String youtubeUrl;
    private String officialWebsite;
    private String url;
    private String reservationUrl;

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public Movie(long id, String name, Date date, int price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.price = price;
    }
}
