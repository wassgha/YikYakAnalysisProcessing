/**
 *Class that references a Yak. A yak has a message, an mID,
 *a postTime, a poster, a number of likes, and takes PApplet.
 *
 *@author Shira Wein
 */
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Yak

{

  ///////////////////fields///////////////

  public String message;
  public String mID;
  public Date postTime;
  public String poster;
  public int likes = 0;
  public String[] splitTime = new String[6];
  //counter for number of comments
  public int numOfComments = 0;
  //array of Comments
  public Comment[] comments = new Comment[1000];


  /////////////////////constructor///////////////////

  /**
   *A constructor that takes in the five parameters of
   *the yak: message, mID, post time, poster, and likes.
   *Also takes PApplet.
   */
  public Yak(JSONArray comments, String message, String mID, String postTime, String poster, int likes, PApplet p)
  {
    this.message=message;
    this.mID=mID;
    this.poster=poster;
    this.likes=likes;

    // convert the post_time from a String to a Date object
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
       this.postTime = sdf.parse(postTime);
    }  catch (Exception e) { 
      System.out.println("Unable to parse date stamp");
    }
    //p.println(hour, minute, second);
    parseComments(comments, p);
  }

  ////////////////////////methods///////////////////

  //sort through comments for yaks
  public void parseComments(JSONArray comments, PApplet p)
  {
    for (int i = 0; i < comments.size (); i++) {

      //new JSONObject from yak i, while i < file.size()
      JSONObject commentObject = comments.getJSONObject(i);
      //organizing pieces of JSON into the parameters of the yak
      String comment = commentObject.getString("comment");
      String mID = commentObject.getString("mID");
      String postTime = commentObject.getString("post_time");
      String poster = commentObject.getString("poster");
      int lastLike= commentObject.getJSONArray("likes").getInt(commentObject.getJSONArray("likes").size()-1);
      addComment(comment, mID, postTime, poster, lastLike, p);
      //p.println(comment);
    }
  }

  //method to add Comment to array of comments for yak
  public void addComment(String message, String mID, String postTime, String poster, int likes, PApplet p)
  {
    //ensure there aren't any comments with that mID already
    for (int i = 0; i < this.numOfComments; i++)
    {
      if (mID.equals(this.comments[i].getMID()))
      {
        System.out.println("Error. A comment with that mID already exists.");
        return;
      }
    }
    this.comments[this.numOfComments]=new Comment(message, mID, postTime, poster, likes, p);
    this.numOfComments++;
  }
  
  //get method for id
  public String getMID()
  {
    return(mID);
  }
  
  //get method for message
  public String getMessage()
  {
    return(message);
  }

  //get method for hour
  public int getHour()
  {
    return(this.postTime.getHours());
  }
  
  //get method for day
  public int getDay()
  {
    return(this.postTime.getDate());
  }
  
  //get method for month
  public int getMonth()
  {
    return(this.postTime.getMonth());
  }
  
  //get method for hour
  public int getYear()
  {
    return(this.postTime.getYear());
  }
 
  //get method for the date object
  public Date getTime() {
    return this.postTime;
  }


  //get method for poster
  public String getPoster()
  {
    return(poster);
  }

  //get method for likes
  public int getLikes()
  {
    return(likes);
  }
  
  //get method for comment
  public int getNumOfComments()
  {
    return(numOfComments);
  }
  
}
