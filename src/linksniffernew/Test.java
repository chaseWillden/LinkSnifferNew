/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package linksniffernew;

/**
 *
 * @author Chase Willden
 */
public class Test {
       public static void main(String[] args){
           LinkSnifferNew ls = new LinkSnifferNew();
           if (!ls.login(null, null, null)){
               System.out.println("Unable to login");
           }
           else{
               ls.setUrl("https://byui-beta.brainhoney.com/Frame/Component/CoursePlayer?");
               ls.dlapGetItemList("17236636");
               ls.run();
               System.out.println(ls.displayBrokenLinks());
           }           
       }
}
