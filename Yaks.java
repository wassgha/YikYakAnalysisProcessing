/**
 *Class that references Yaks. Yaks have file names and
 *take in PApplet.
 *
 *@author Shira Wein
 */

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import processing.data.*;
import processing.core.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Yaks

{

  ///////////////////fields///////////////

  /**number of yaks*/
  public int numOfYaks = 0;

  /**
   *Array that stores the yak added in the addYak method and
   */
  private Yak[] yaks = new Yak[100000];

  private int[] numOfYaksPerHr = new int[24];
  private int[] numOfCommentsPerHr = new int[24];
  private int[] numOfLikesPerHr = new int[24];
  private int maxYaksPerHr;
  private int maxCommentsPerHr;
  private int maxLikesPerHr;
  private int[] wordfrequencies;
  private String[] wordstrings;
  private int wordsToDisplay;
  private int counter;
  private int wordsActuallyDisplayed;
  private int maxTextSize;
  private int minTextSize;
  private int maxTrials;
  private String longestword;
  private textBox[] textBoxes;
  /** date objects to store the date range of the Yaks **/
  private Date last_date, first_date;

  /** array of english stop words that can be ignored during the analysis */
  public String [] stopWords = new String[1000];

  /** used to prevent word clouds from changing on every screen refresh*/
  long randomSeed;

  //counters for graphs
  private Lemmatization lemm;
  private int curhour=8;
  private int oldhour=25;


  /////////////////////constructor///////////////////

  /**
   *A constructor that takes the file name of the yak
   *and PApplet; sorts them with JSONsort.
   */
  public Yaks(String filename, PApplet p)
  {
    // Create the Simple Date Format to determine the first and the last date while parsing Yaks
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      last_date = sdf.parse("1994-01-31 00:00:00");
    } 
    catch(Exception e) {
      System.out.println("Unable to parse date stamp");
    }
    first_date = new Date();


    stopWords=stopWords(p);
    JSONparse(filename, p);
    lemm = new Lemmatization(p);
    setNumOfYaksPerHour();
    setNumOfLikesPerHour();
    setNumOfCommentsPerHour();
    maxYaksPerHr=maxOfArray(numOfYaksPerHr);
    maxCommentsPerHr=maxOfArray(numOfCommentsPerHr);
    maxLikesPerHr=maxOfArray(numOfLikesPerHr);
    randomSeed=(long)p.random(1000);
    maxTextSize=90;
    minTextSize=20;
    maxTrials=300;
    textBoxes=new textBox[50];
    /*frequencyWordByDate(p, "PSafe", 31, 8, 2015);
    yaksWithWord(p, "fuck");*/
  }


  ////////////////////////methods///////////////////

  //split stop words
  public String [] stopWords(PApplet p)
  {
    return p.loadStrings("stopWords.txt");
  }

  //sort through JSON file
  public void JSONparse(String filename, PApplet p)
  {
    JSONArray file = p.loadJSONArray(filename);

    for (int i = 0; i < file.size (); i++) {

      //new JSONObject from yak i, while i < file.size()
      JSONObject yakObject = file.getJSONObject(i);
      //organizing pieces of JSON into the parameters of the yak
      String message = yakObject.getString("message");
      String mID = yakObject.getString("mID");
      String postTime = yakObject.getString("post_time");
      String poster = yakObject.getString("poster");
      int lastLike= yakObject.getJSONArray("likes").getInt(yakObject.getJSONArray("likes").size()-1);
      JSONArray comments = yakObject.getJSONArray("comments");
      addYak(comments, message, mID, postTime, poster, lastLike, p);
    }
  }

  /**
   *Method to add a yak to the instance variable array
   *yaks. The yak is added to a certain position in the array
   *as stated by int numOfYaks and the yak that is
   *added to the array of yaks. It will not be added if a yak
   *with the sam mID already exists.
   */
  public void addYak(JSONArray comments, String message, String mID, String postTime, String poster, int likes, PApplet p)
  {
    //ensure there aren't any yaks with that mID already
    for (int i = 0; i < this.numOfYaks; i++)
    {
      if (mID.equals(this.yaks[i].getMID()))
      {
        System.out.println("Error. A yak with that mID already exists.");
        return;
      }
    }
    this.yaks[this.numOfYaks]=new Yak(comments, message, mID, postTime, poster, likes, p);
    // Check if the Yak is older than the last date in which case make the last date equal to this Yak's date
    last_date=this.yaks[numOfYaks].getTime().after(last_date)?this.yaks[numOfYaks].getTime():last_date;

    // Check if the Yak is more recent than the first date in which case make the first date equal to this Yak's date
    first_date=this.yaks[numOfYaks].getTime().before(first_date)?this.yaks[numOfYaks].getTime():first_date;
    this.numOfYaks++;
  }
  /**
   * Method to get a JSON Object that contains the words and their
   * frequencies for a certain time range
   * @param date1 the start date
   * @param date2 the end date
   * @return the IntDict of word/frequency for the time range
   */

  public IntDict wordFrequencyByHour(PApplet p, int hour) {

    // Create the IntDict to hold the word frequency
    IntDict result= new IntDict();

    // convert the stop words from an array to a regex string that will be used to remove them from the message
    String stopWordsRegex = "#|\\b" +  p.join(stopWords, "\\b|\\b")+"\\b|#";

    // Create the Simple Date Format to determine if the yak is posted in the selected week
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Loop through the yaks
    for (int i=0; i<numOfYaks; i++) {
      // if the yak is posted in the selected week
      if (yaks[i].getHour()==hour) {
        // remove all special characters and make the message lowercase
        String message = yaks[i].getMessage().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]+", "");
        // remove all stop words from the message and separate the words (as an array)
        String[] words= p.splitTokens(message.replaceAll(stopWordsRegex, ""));
        // Loop through the words and increase their frequency in the intDict
        for (int j=0; j<words.length; j++) {
          String lemmatized = lemm.getLemmat(words[j].toLowerCase());
          if(lemmatized.length()>2)
            result.add(lemmatized, 1);
        }
      }
    }
    // sort the intDict from most frequent to the least frequent
    result.sortValuesReverse();
    return result;
  }

  public int frequencyWordByDate(PApplet p, String word, int day, int month, int year)
  {
    int frequency = 0;
    String[] split;

    for (int i = 0; i < this.getNumOfYaks(); i++)
    {
      if (day==yaks[i].getDay() && month==yaks[i].getMonth() && year==yaks[i].getYear())
      {
        split = p.splitTokens((yaks[i]).getMessage().replaceAll("[^a-zA-Z0-9\\s]+", "").toLowerCase());
        for (int j = 0; j<split.length; j++)
        {
          if (lemm.getLemmat(split[j]).equals(word))
          { 
            frequency = frequency + 1;
          }
        }
      }
    }

    return(frequency);
  }

  public Yak[] yaksWithWord(PApplet p, String word)
  {
    Yak[] yaksWithWord = new Yak[10000];
    int yakCounter = 0;
    String[] split;

    for (int i = 0; i < this.getNumOfYaks(); i++)
    {
      split = p.splitTokens((yaks[i]).getMessage().replaceAll("[^a-zA-Z0-9\\s]+", "").toLowerCase());
      boolean addyak=false;
      for (int j = 0; j<split.length; j++)
      {
        if (lemm.getLemmat(split[j]).equals(word))
        {
           addyak=true; 
        }
      }
      if(addyak) {
          yaksWithWord[yakCounter]=yaks[i];
          yakCounter++;
      }
    }
    return(yaksWithWord);
  }
  
  public int howManyYaksWithWord(PApplet p, String word)
  {
    int yakCounter = 0;
    String[] split;

    for (int i = 0; i < this.getNumOfYaks(); i++)
    {
      split = p.splitTokens((yaks[i]).getMessage().replaceAll("[^a-zA-Z0-9\\s]+", "").toLowerCase());
      boolean addyak=false;
      for (int j = 0; j<split.length; j++)
      {
        if (lemm.getLemmat(split[j]).equals(word))
        { 
          addyak=true;
        }
      }
      if(addyak) {
        yakCounter++;
      }
    }
    return(yakCounter);
  }
  
 
  /**
   * Method to get the first date that appears on the JSON
   * file of Yik Yak data (when data scraping started)
   * @return the first date
   */
   public Date getFirstDate() {
    return this.first_date;
  }

  /**
   * Method to get the last date that appears on the JSON
   * file of Yik Yak data (when data scraping stopped)
   * @return the last date
   */

  public Date getLastDate() {
    return this.last_date;
  }

  /**
   *Method to get the number of yaks.
   *@return the number of yaks
   */
  public int getNumOfYaks()
  {
    return(numOfYaks);
  }

  public int maxOfArray(int[] a)
  {
    int highest = 0;
    for (int i = 0; i<a.length; i++)
    {
      if (a[i] > highest)
      {
        highest=a[i];
      }
    }
    return highest;
  }
  
  public int minOfArray(int[] a)
  {
    int minimum = a[0];
    for (int i = 0; i<a.length; i++)
    {
      if (a[i] < minimum)
      {
        minimum=a[i];
      }
    }
    return minimum;
  }
  
  // Finds the index of a value in an array
  public int findInArray(int [] a, int searchfor){
    int pos=-1;
    for ( int i=0; i<a.length; i++) { 
      if(a[i]==searchfor) // if the values are equal, we return the position
        pos=i;
    }
    return pos;
  }

  private void setNumOfYaksPerHour()
  {
    for (int i = 0; i < this.getNumOfYaks (); i++)
    {
      numOfYaksPerHr[yaks[i].getHour()]++;
    }
  }

  private void setNumOfCommentsPerHour()
  {
    for (int i = 0; i < this.getNumOfYaks (); i++)
    {
      numOfCommentsPerHr[yaks[i].getHour()] +=yaks[i].getNumOfComments();
    }
  }

  private void setNumOfLikesPerHour()
  {
    for (int i = 0; i < this.getNumOfYaks (); i++)
    {
      numOfLikesPerHr[yaks[i].getHour()] +=yaks[i].getLikes();
    }
  }

  public int getNumOfYaksPerHour(int h)
  {
    return numOfYaksPerHr[h];
  }

  public int getNumOfCommentsPerHour(int h)
  {
    return numOfCommentsPerHr[h];
  }

  public int getNumOfLikesPerHour(int h)
  {
     return numOfLikesPerHr[h];
  }
  
  public int getMaxYaksPerHour()
  {
    return maxYaksPerHr;
  }

  public int getMaxLikesPerHour()
  {
    return maxLikesPerHr;
  }

  public int getMaxCommentsPerHour()
  {
    return maxCommentsPerHr;
  }

  public String formatHour(int i) {
    return i==0||i==24?"Midnight":i==12?"Noon":(i<12?i + " AM":i-12 +" PM");
  }
  public String formatHourNum(int i) {
    return i==0||i==24?"12AM":i==12?"12PM":(i<12?i + " AM":i-12 +" PM");
  }
  

}

