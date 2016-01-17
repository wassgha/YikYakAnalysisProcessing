import processing.core.*;
import processing.data.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;

public class Interface
{
  private PApplet p;
  private Yaks yaks;
  private String page, oldpage, title;
  private Yak[] yaksWithWord;
  private int howManyYaksWithWord;
  private int curhour, oldhour;
  private int[] wordfrequencies;
  private String[] wordstrings;
  private int wordsToDisplay, counter, wordsActuallyDisplayed;
  private int maxTextSize, minTextSize;
  private int maxTrials;
  private int margin_bottom=20, padding=20;
  private String longestword;
  private textBox[] textBoxes;
  private int scroll_y=0, chart_y=100;
  private String definition;
  private PImage arrows, arrow_back, bubble;
  public Interface(PApplet p, Yaks yaks) {
    this.p=p;
    this.yaks=yaks;
    this.page="home";
    this.oldpage="home";
    this.curhour=2;
    this.oldhour=-1;
    this.maxTextSize=90;
    this.minTextSize=15;
    this.maxTrials=500;
    this.textBoxes=new textBox[50];
  }
  public boolean home() {
    return page=="home";
  }
  public void showTimeline(int x, int y, int w, int h) {
    p.noStroke();
    p.rectMode(p.CORNER);
    p.textSize(14);
    p.fill(p.color(255, 255, 255));
    p.rect(x-30, y-30, w+60, h+60);
    h-=30;
    w-=70;
    p.fill(p.color(236, 240, 241, 190));
    p.rect(x, y, 24*(w/24), h);
    int barwidth=w/24;
    for (int i=0; i<24; i++) {
      p.fill(p.color(127, 140, 141));
      p.stroke(p.color(220));
      p.textAlign(p.CENTER);
      p.line(x + i*barwidth, y, x + i*barwidth, y+h-1);
      p.noStroke();
      if (i%3==0) {
        p.text(yaks.formatHour(i), x + i*barwidth + barwidth/2, y+h+15);
      }
      if (i%2==0) {
        p.fill(p.color(127, 140, 141, 40));
        p.rect(x+ i*barwidth +1, y, barwidth, h);
      }
      //NUMBER OF YAKS
      p.fill(p.color(200));
      p.noStroke();
      float numYaks=h-h*((float)yaks.getNumOfYaksPerHour(i)/yaks.getMaxYaksPerHour());
      p.rect(x+ i*barwidth +1, y + numYaks, barwidth/3, h -numYaks );
      //NUMBER OF LIKES
      p.fill(p.color(52, 73, 94));
      p.noStroke();
      float numoflikesperhourpositive = yaks.getNumOfLikesPerHour(i)<0?0:yaks.getNumOfLikesPerHour(i);
      float numLikes=h-h*(numoflikesperhourpositive/yaks.getMaxLikesPerHour());
      p.rect(x+ i*barwidth + barwidth/3 +1, y + numLikes, barwidth/3, h -numLikes );
      //NUMBER OF COMMENTS
      p.fill(p.color(127, 140, 141));
      p.noStroke();
      float numComments=h-h*((float)yaks.getNumOfCommentsPerHour(i)/yaks.getMaxCommentsPerHour());
      p.rect(x+ i*barwidth +2*barwidth/3 +1, y + numComments, barwidth/3, h -numComments );
      if ((i==((p.mouseX-x)/barwidth) && p.mouseY>=y && p.mouseY<=y+h+30)||curhour==i) {
        p.fill(p.color(127, 140, 141, 100));
        p.rect(x+ i*barwidth +1, y, barwidth, h);
        if (p.mousePressed) {
          curhour = i;
        }
      }
    }
    p.textAlign(p.LEFT);
    p.fill(p.color(200));
    p.text("YAKS", x + w/2-150, y-15);
    p.rect(x + w/2-150 - 20, y-25, 10, 10);
    p.fill(p.color(52, 73, 94));
    p.text("LIKES",  x + w/2-50, y-15);
    p.rect(x + w/2-50 - 20, y-25, 10, 10);
    p.fill(p.color(127, 140, 141));
    p.text("COMMENTS",  x + w/2+50, y-15);
    p.rect(x + w/2+50 - 20, y-25, 10, 10);
    p.textAlign(p.CENTER);
    p.fill(p.color(44, 62, 80));
    p.textSize(25);
    p.text(yaks.formatHourNum(curhour), x + w + 50, y+(h+30)/2);
  }

  private boolean noColoredPixelsInRect(PApplet p, int x, int y, int w, int h ) {
    for (int i =x-w/2-3; i<x+w/2+3; i++) {
      for (int j= y-h/2-3; j<y+h/2+3; j++) {
        int c=p.get(i, j);
        if (p.red(c)!=255 && p.blue(c)!=255 && p.green(c)!=255) {
          return false;
        }
      }
    }
    return true;
  }

  private void prepareWordCloud(PApplet p, int x, int y, int w, int h) {
    p.background(p.color(255, 255, 255));
    oldhour=curhour;
    // Get the IntDict for words and their frequencies for the selected hour
    IntDict wordfreq = yaks.wordFrequencyByHour(p, curhour);

    // Split the IntDict to two arrays : words and their respective frequencies
    wordfrequencies = wordfreq.valueArray();
    wordstrings = wordfreq.keyArray();
    // Find the longest word that has the highest frequency
    longestword="";
    for (int i=0; i<wordstrings.length; i++) {
      if (longestword.length()<wordstrings[i].length() && wordfrequencies[i]==wordfrequencies[0])
        longestword=wordstrings[i];
    }

    // Limit the words to display to 50 words
    wordsToDisplay=wordfrequencies.length>30?30:wordfrequencies.length-1;
    counter = 0;
    wordsActuallyDisplayed=0;
  }


  public void drawNextWord(int x, int y, int w, int h) {
    if (oldhour!=curhour)
      prepareWordCloud(p, x, y, w, h);
    // Prevent the words from changing position randomly everytime the screen is refreshed
    //p.randomSeed(randomSeed);
    p.textAlign(p.CENTER);
    // substract the width of the longest word on the maximum text size so that words don't get out of the drawing box
    p.textSize(maxTextSize);
    x = x + (int)(p.textWidth(longestword));
    y = y + (int)(p.textAscent());
    w = w - (int)(2*p.textWidth(longestword));
    h = h - (int)(2*p.textAscent());
    if (counter<wordsToDisplay) {

      // Choose a random color, resize the text proportionally to the frequency
      // and draw the text at a random position
      p.rectMode(p.CENTER);
      p.textSize(p.map(wordfrequencies[counter], wordfrequencies[wordsToDisplay], wordfrequencies[0], minTextSize, maxTextSize));
      int textx= x+w/2;
      int texty= y+h/2;
      int textwidth= (int)p.textWidth(wordstrings[counter]);
      int textheight= (int)p.textAscent();
      int trials=0;
      while (trials<maxTrials && !noColoredPixelsInRect (p, textx, texty- (int)p.textDescent(), textwidth, textheight)) {
        //p.stroke(p.color(255,0,0));
        p.fill(p.color(255, 255, 255, 255));
        // Used for testing, draws rectangle around the tested position of the word
        //p.rect(textx, texty-(int)p.textDescent(), textwidth, textheight);
        //p.redraw();
        //p.delay(1000);
        textx= (int)p.random(-w/2, w/2)+x+w/2;
        texty= (int)p.random(-h/2, h/2)+y+h/2;
        trials++;
      }
      p.fill(p.color(255, 255, 255, 255));
      // Used for testing, draws rectangle around the final position of the word
      //p.stroke(p.color(0,255,0));
      //p.rect(textx, texty-(int)p.textDescent(), textwidth, textheight);
      //p.redraw();
      //p.delay(1000);
      if (trials<maxTrials) {
        p.fill(p.color(p.random(0, 150), p.random(0, 150), p.random(0, 150), p.map(wordfrequencies[counter], wordfrequencies[wordsToDisplay], wordfrequencies[0], 80, 255)));
        p.text(wordstrings[counter], 
        textx, 
        texty);
        textBoxes[wordsActuallyDisplayed]=new textBox(textx-textwidth/2, texty-2*(int)p.textDescent()-textheight/2, textwidth, textheight+(int)p.textDescent(), counter);
        wordsActuallyDisplayed++;
      }
      //p.redraw();
      counter++;
    }
  }

  public void makeClickable() {
    boolean hover=false;
    for (int i =0; i<wordsActuallyDisplayed; i++) {
      if (p.mouseX>textBoxes[i].getX() && p.mouseX<textBoxes[i].getX()+textBoxes[i].getWidth() && p.mouseY>textBoxes[i].getY() && p.mouseY<textBoxes[i].getY()+textBoxes[i].getHeight()) {
        hover = true;
        if (p.mousePressed) {
          page=wordstrings[i];
          //p.println(page);
        }
      }
    }
    if (hover) {
      p.cursor(p.HAND);
    } else {
      p.cursor(p.ARROW);
    }
  }

  private void prepareWordPage() {
    scroll_y=0;
    p.cursor(p.ARROW);
    title = page.substring(0, 1).toUpperCase() + page.substring(1);
    yaksWithWord = yaks.yaksWithWord(p, page);
    howManyYaksWithWord=yaks.howManyYaksWithWord(p, page);
    XML definitionXML = p.loadXML("http://www.dictionaryapi.com/api/v1/references/learners/xml/" + page + "?key=a4710bd5-d508-4e96-b61e-4053e9ce093d");
    try {
      definition = definitionXML.getChildren("entry")[0].getChildren("def")[0].getChild("dt").getContent();
      definition = "Def. " + (definition.length()>60?definition.substring(0, 60) + "..":definition);
    } 
    catch (Exception e) {
      definition ="";
    }
    arrows = p.loadImage("arrows.png");
    arrow_back = p.loadImage("arrow_back.png");
    bubble = p.loadImage("bubble.png");
  }

  public void showWordPage(float mouseWheel) {
    if (oldpage!=page) {
      prepareWordPage();
      oldpage=page;
    }
    p.background(p.color(24, 163, 252));
    p.fill(p.color(255, 255, 255, 180));
    p.textAlign(p.LEFT);
    boolean hover = false;

    // Create the Simple Date Format to show the labels
    SimpleDateFormat sdf = new SimpleDateFormat("dd/M");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

    // determining how many days (the range) the json file has
    long millisDiff = (yaks.getLastDate().getTime() - yaks.getFirstDate().getTime());
    long dayDiff =  TimeUnit.MILLISECONDS.toDays(millisDiff);
    int[] data=new int[(int)dayDiff+1];
    int chartWidth=p.width;
    int chartHeight=p.height/2;
    Calendar calendar = new GregorianCalendar(yaks.getFirstDate().getYear() + 1900, yaks.getFirstDate().getMonth(), yaks.getFirstDate().getDate());  
    for (int i=0; i<=dayDiff; i++) {
      if (i%3==0) {
        p.textSize(9);
        p.textAlign(p.CENTER);
        p.text(sdf.format(calendar.getTime()), 50 + i*((chartWidth-100)/dayDiff), chart_y+chartHeight-75 + scroll_y);
      }
      data[i]=yaks.frequencyWordByDate(p, page, calendar.getTime().getDate(), calendar.getTime().getMonth(), calendar.getTime().getYear());
      //p.println(i, calendar.getTime().getDate(), calendar.getTime().getMonth(), calendar.getTime().getYear(), data[i]);
      calendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    // Calculate maximum and minimum
    int max_val=yaks.maxOfArray(data);
    int min_val=yaks.minOfArray(data);


    // Find the index of maximum and minimum
    int max=yaks.findInArray(data, max_val);
    int min=yaks.findInArray(data, min_val);


    float scale_x=(chartWidth-100)/((float)(data.length-1)); //width of the canvas divided by number of data points gives the horizontal scale
    float scale_y=(chartHeight-100)/(max_val-min_val); //height of the canvas divided by vertical range gives the vertical scale

    int number_labels=max_val+1>(p.ceil((float)(chartHeight-100)/100))?p.ceil((float)(chartHeight-100)/100):max_val+1;
    int freq_per_label=p.round((max_val-min_val)/number_labels);

    //Draw labels
    p.textSize(14);
    int label=(int)(max_val);
    p.stroke(p.color(255, 255, 255, 120));
    for (int i=0; i<=number_labels; i++) { 
      p.text(label, 25, chart_y+i*((chartHeight-100)/number_labels) + scroll_y);    
      p.line(50, chart_y+i*((chartHeight-100)/number_labels) + scroll_y, chartWidth-51, chart_y+i*((chartHeight-100)/number_labels) + scroll_y);
      label-=freq_per_label;
    }

    // Draw the line curve
    p.stroke(255);
    p.strokeWeight(4);
    int totalTimesMentioned=0;
    for (int i=0; i<data.length-1; i++) { 
      p.stroke(255);
      p.strokeWeight(4);
      p.fill(p.color(24, 163, 252));
      totalTimesMentioned +=data[i];
      p.line(i*scale_x+50, (chartHeight)-(data[i]-min_val)*scale_y + scroll_y, (i+1)*scale_x+50, (chartHeight)-(data[i+1]-min_val)*scale_y + scroll_y);
      if (p.mouseX>i*scale_x+50-5 && p.mouseX<i*scale_x+50+5 && p.mouseY>chart_y+ scroll_y && p.mouseY<chart_y+ scroll_y + chartHeight-50) {
        p.stroke(p.color(255, 255, 255, 100));
        p.strokeWeight(10);
        p.fill(255);
        p.ellipse(i*scale_x+50, (chartHeight)-(data[i]-min_val)*scale_y + scroll_y, 12, 12);
        p.image(bubble, i*scale_x+25, (chartHeight)-(data[i]-min_val)*scale_y + scroll_y - 40);
        p.text(data[i], i*scale_x+50, (chartHeight)-(data[i]-min_val)*scale_y + scroll_y - 25);
      } else {
        p.ellipse(i*scale_x+50, (chartHeight)-(data[i]-min_val)*scale_y + scroll_y, 10, 10);
      }
    }
    //p.println("Mentioned " + totalTimesMentioned + " times");
    p.strokeWeight(1);      


    for (int i=0; i<howManyYaksWithWord-1; i++) {
      p.textAlign(p.LEFT);
      p.fill(255);
      p.noStroke();
      p.textSize(20);
      p.rect(50, chartHeight + 50 + (150+margin_bottom)*i +scroll_y, p.width-100, 150, 4);
      p.fill(0);
      p.text(yaksWithWord[i+1].getMessage(), 50+padding, 50 + padding + chartHeight + (150+margin_bottom)*i +scroll_y, p.width-100-2*padding - 50, 150-2*padding);
      p.fill(p.color(26, 188, 156));
      p.textSize(18);
      p.text(sdf2.format(yaksWithWord[i+1].getTime()), 50+padding, 165 + padding + chartHeight + (150+margin_bottom)*i +scroll_y);
      int datewidth = (int)p.textWidth(sdf2.format(yaksWithWord[i+1].getTime()));
      p.fill(180);
      p.text(yaksWithWord[i+1].getNumOfComments() + " replies", datewidth + 100 + 50+padding, 165 + padding + chartHeight + (150+margin_bottom)*i +scroll_y);
      p.textAlign(p.CENTER);
      p.image(arrows, p.width-120, padding + chartHeight + (150+margin_bottom)*i +scroll_y + 60, 45, 95);
      p.text(yaksWithWord[i+1].getLikes(), p.width-100, 50 + padding + chartHeight + (150+margin_bottom)*i +scroll_y + 65);
    }

    p.fill(p.color(24, 163, 252, 150));
    p.noStroke();
    p.rect(0, 0, p.width, 70);
    p.textSize(35);
    p.fill(255);
    p.textAlign(p.LEFT);
    p.text(title, 100, 50);
    int titlewidth = (int)p.textWidth(title);
    p.textSize(17);
    p.text(definition, 100+titlewidth+20, 30, p.width-150-titlewidth-20, 50);
    p.image(arrow_back, 50, 20);
    //Draw total times mentioned
    p.fill(p.color(0, 130, 222));
    p.noStroke();
    p.rect(p.width-150, 10, 100, 50, 4);
    p.textAlign(p.CENTER);
    p.textSize(25);
    p.fill(255);
    p.text("" + totalTimesMentioned, p.width-150+50, 44);
    p.textSize(9);
    p.fill(p.color(111, 201, 255));
    p.text("mentioned", p.width-150+50, 22);
    p.text("times", p.width-150+50, 53);
    // Go to the word cloud view when the back arrow is clicked
    if (p.mouseX>50 && p.mouseX<100 && p.mouseY>20 && p.mouseY<70) {
      hover=true;
      if (p.mousePressed) {
        page="home";
        oldhour=-1;
      }
    }
    // Scroll the page when mouseWheel changes
    scroll_y+=-20*mouseWheel;
    scroll_y=scroll_y>0?0:scroll_y;
    if (hover) {
      p.cursor(p.HAND);
    } else {
      p.cursor(p.ARROW);
    }
  }
}

