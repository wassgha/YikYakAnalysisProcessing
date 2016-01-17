import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.*;

public class Comment
{
  public String message;
  public String mID;
  public String postTime;
  public String poster;
  public int likes = 0;
  //initialize pieces of post time
  public int year = 0;
  public int month = 0;
  public int day = 0;
  public int hour = 0;
  public int minute = 0;
  public int second = 0;
  //array to store different pieces of the split post time
  public String[] splitTime = new String[6];

  public Comment(String message, String mID, String postTime, String poster, int likes, PApplet p)
  {
    this.message=message;
    this.mID=mID;
    this.poster=poster;
    this.likes=likes;
    this.postTime=postTime;
    //modify post time into distinct time pieces
    String[] splitTime = p.splitTokens(postTime, "- :");
    year = Integer.parseInt(splitTime[0]);
    month = Integer.parseInt(splitTime[1]);
    day = Integer.parseInt(splitTime[2]);
    hour = Integer.parseInt(splitTime[3]);
    minute = Integer.parseInt(splitTime[4]);
    second = Integer.parseInt(splitTime[5]);
  }

  //get method for comment id
  public String getMID()
  {
    return(mID);
  }
}
