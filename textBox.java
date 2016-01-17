public class textBox

{

  ///////////////////fields///////////////

  private int x,y,w,h, id;
  
  public textBox(int x, int y, int w, int h, int id) {
    this.x=x;
    this.y=y;
    this.w=w;
    this.h=h;
    this.id=id;
  }

  public int getID() {
    return this.id;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getX() {
    return this.x;
  }  
  
  public int getWidth() {
    return this.w;
  }
  
  public int getHeight() {
    return this.h;
  }
}
