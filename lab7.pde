import processing.data.JSONObject;
import processing.data.JSONArray;
Yaks yaks;
Interface ui;
PImage logo;
float mouseWheel;

void setup()
{
  size(800, 600);
yaks = new Yaks("yak_2.json", this);
ui = new Interface(this, yaks);

  background(255);
}

void draw() {
  if(ui.home()) {
    ui.drawNextWord(50, 30+150, width-100, height-180-50);
    ui.showTimeline(30, 30, width-60, 100);
    ui.makeClickable();
  } else {
    ui.showWordPage(mouseWheel);
    mouseWheel=0;
  }
}

void mouseWheel(MouseEvent event) {
  mouseWheel = event.getCount();
}