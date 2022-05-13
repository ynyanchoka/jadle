package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Review implements Comparable<Review> {

    private String content;
    private String writtenby;
    private int rating;
    private int id;
    private int restaurantId; //will be used to connect Restaurant to Review (one-to-many)

    private long createdat;
    private String formattedCreatedAt;

    public Review(String content, String writtenby, int rating, int restaurantId) {
        this.content = content;
        this.writtenby = writtenby;
        this.rating = rating;
        this.restaurantId = restaurantId;
        this.createdat = System.currentTimeMillis();
        setFormattedCreatedAt();
    }

    public String getContent() {
        return content;
    }

    public String getWrittenby() {
        return writtenby;
    }

    public int getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }
    public long getCreatedat() {
        return createdat;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWrittenby(String writtenby) {
        this.writtenby = writtenby;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setCreatedat() {
        this.createdat = System.currentTimeMillis();
    }

    public String getFormattedCreatedAt(){
        Date date = new Date(createdat);
        String datePatternToUse = "MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        return sdf.format(date);
    }

    public void setFormattedCreatedAt(){
        Date date = new Date(this.createdat);
        String datePatternToUse = "MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        this.formattedCreatedAt = sdf.format(date);
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return getRating() == review.getRating() && getId() == review.getId() && getRestaurantId() == review.getRestaurantId() && Objects.equals(getContent(), review.getContent()) && Objects.equals(getWrittenby(), review.getWrittenby());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getWrittenby(), getRating(), getId(), getRestaurantId());
    }

    @Override
    public int compareTo(Review reviewObject) {
        if (this.createdat < reviewObject.createdat)
        {
            return -1; //this object was made earlier than the second object.
        }
        else if (this.createdat > reviewObject.createdat){ //this object was made later than the second object
            return 1;
        }
        else {
            return 0; //they were made at the same time, which is very unlikely, but mathematically not impossible.
        }

    }
}
