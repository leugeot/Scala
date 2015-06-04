package cz.kinoscala.scala;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by petr on 20.4.2015.
 */
public class Movie implements Serializable {
    private long id;
    private String name;
    private String originalName;
    private Date date;
    private int year;
    private int runtime;
    private String countries;
    private String languages;
    private String subtitles;
    private List<String> directors;
    private List<String> actors;
    private String plot;
    private int price;
    private int csfdRating;
    private String csfdID;
    private double imdbRating;
    private String imdbID;
    private String fest;
    private String festUrl;
    private String imageUrl;
    private String youtubeUrl;
    private String website;
    private String url;
    private String reservationUrl;
    private String currency;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCsfdRating() {
        return csfdRating;
    }

    public void setCsfdRating(int csfdRating) {
        this.csfdRating = csfdRating;
    }

    public String getCsfdID() {
        return csfdID;
    }

    public void setCsfdID(String csfdID) {
        this.csfdID = csfdID;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String officialWebsite) {
        this.website = officialWebsite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReservationUrl() {
        return reservationUrl;
    }

    public void setReservationUrl(String reservationUrl) {
        this.reservationUrl = reservationUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Movie(){}

    public Movie(long id, String name, Date date, int price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.price = price;
    }
    public Movie(long id, String name, Date date, int price, String img) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.price = price;
        this.imageUrl = img;
    }
}
