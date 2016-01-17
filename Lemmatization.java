import processing.core.*;
import processing.data.*;

public class Lemmatization
{
 StringDict lemmat;
 public Lemmatization(PApplet p)
 {
     String[] lines = p.loadStrings("lemmatization-en.txt");
     lemmat = new StringDict();
     
     for(int i = 0; i<lines.length; i++)
     {
       String[] split = p.splitTokens(lines[i]);
       lemmat.set(split[1], split[0]);
     }
 }
 
 public String getLemmat(String search)
 {
   return lemmat.get(search) == null?search:lemmat.get(search);
 }

}
