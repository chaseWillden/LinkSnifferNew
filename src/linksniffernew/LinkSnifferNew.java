/*
 * Author: Chase Willden
 * 
 * Error Numbers:
 * 132: Ping Error
 * 133: Login Error
 * 136: Dlap getresource error
 */

package linksniffernew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

/**
 *
 * @author willdech
 */
public class LinkSnifferNew {

    /**
        * The first letter 'd' indicates that this is a result from a Dlap call.<div><br></div><div>Holds CourseActivityXML</div>
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
        * @generated
        * @ordered
        */

       private List dCourseActivityDOM = new ArrayList();
       private List errorLog = new ArrayList();
       private Document dCourseItemList;
       private Session session;
       private List brokenLinks = new ArrayList();
       private String baseUrl;

       /**
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
        * @generated
        */
       public LinkSnifferNew(){
           try {
               this.session = new Session("Link Sniffer", "http://gls.agilix.com/dlap.ashx");
           } catch (   TransformerConfigurationException | ParserConfigurationException ex) {
               Logger.getLogger(LinkSnifferNew.class.getName()).log(Level.SEVERE, null, ex);
           }
       }

       /**
        * A function to ping the url.<div><br></div><div>Returns true if a 200 message is returned.</div>
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param address
     * @return 
        * @generated
        * @ordered
        */
     
       public boolean pingUrl(String address){
            try {
                System.out.println("Address pinged: " + address);
                if (address.contains("///")){
                    return false;
                }
                return Jsoup.connect(address).execute().statusCode() == 200;
            } catch (IOException ex) {
                addError("132", "Unable to pings the address: " + address);
            }
            return false;
        }
       
       /**
        * Adds Errors to the Error log.
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param errorNum
     * @param errorMsg
        * @generated
        * @ordered
        */
       
       public void addError(String errorNum, String errorMsg){
           this.errorLog.add(errorNum + ": " + errorMsg);
       }
       
       /**
        * Sets the base url
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param url
        * @generated
        * @ordered
        */
       
       public void setUrl(String url){
           this.baseUrl = url;
       }

       /**
        * Login to Brainhoney's API through "http://gls.agilix.com/dlap.ashx".
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param username
     * @param password
     * @param prefix
     * @return 
        * @generated
        * @ordered
        */

       public boolean login(String username, String password, String prefix) {
            // Login
           org.jsoup.nodes.Document result = null;
           if (username.isEmpty() || password.isEmpty() || prefix.isEmpty()){
               return false;
           }
           try {
               result = session.Login(prefix, username, password);
           } catch (   ParserConfigurationException | TransformerException | IOException | SAXException ex) {
               Logger.getLogger(LinkSnifferNew.class.getName()).log(Level.SEVERE, null, ex);
           }
            if (!Session.IsSuccess(result))
            {
                addError("133", "Unable to login: " + Session.GetMessage(result));
                return false;
            }

            return true;	
       }

       /**
        * Using Brainhoney's DLAP call, it gets the "html" content of the activity.
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param entityid
     * @param path
     * @return 
        * @generated
        * @ordered
        */

       public String dlapGetResource(String entityid, String path) {
           if (entityid.isEmpty() || path.isEmpty()){
               return null;
           }
           Map<String, String> params = new HashMap<>();
           params.put("entityid", entityid);
           params.put("path", path);
           System.out.println(params.toString());
           try {
               Document r = session.Get("getresource", params);
               if (r == null){
                   return null;
               }
               return r.toString();
           } catch (   TransformerException | IOException | ParserConfigurationException | SAXException ex) {
               addError("136", "Resource couldn't be downloaded");
           }
           return null;
       }

       /**
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
        * @generated
        * @ordered
        */

       public void logout() {
           try {
               this.session.Logout();
           } catch (   TransformerException | IOException | ParserConfigurationException | SAXException ex) {
               Logger.getLogger(LinkSnifferNew.class.getName()).log(Level.SEVERE, null, ex);
           }
       }

       /**
        * Returns a list of links in the activity
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param elements
     * @return 
        * @generated
        * @ordered
        */
       
       public List getLinks(Elements elements){
           List links = new ArrayList();
           for (Element ele : elements){
               if (!ele.attr("href").isEmpty()){
                   links.add(ele.attr("href"));
               }
               if (!ele.attr("src").isEmpty()){
                   links.add(ele.attr("src"));                 
               }
           }
           return links;
       }
       
       /**
        * Returns a list of the broken links within the course
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param courseContent
     * @param activity
        * @generated
        * @ordered
        */

       public void processBrokenLinks(String courseContent, String activity) {
           Document parsed = Jsoup.parse(courseContent);
           Elements hrefs = parsed.getElementsByAttribute("href");
           Elements srcs = parsed.getElementsByAttribute("src");
           List hrefList = getLinks(hrefs);
           List srcList = getLinks(srcs);    
           if (!hrefList.isEmpty()){
               int hrefsize = hrefList.size();
               for (int i = 0; i < hrefsize; i++){
                   String url = formatUrl(hrefList.get(i).toString(), true);
                   if (!pingUrl(url)){
                       this.brokenLinks.add("url=" + url + "&activity=" + activity);
                   }
               }
           }
           if (!srcList.isEmpty()){
               int srcsize = srcList.size();
               for (int i = 0; i < srcsize; i++){
                   String url = formatUrl(srcList.get(i).toString(), true);
                   if (!pingUrl(url)){
                       this.brokenLinks.add("url=" + url + "&activity=" + activity);
                   }
               }
           }
           linkCount++;
       }
       
       protected int linkCount = 0;

       /**
        * get list of all items within a course using the Brainhoney Dlap call "getitemlist" and setting it into a global variable which can be accessed by getItemInfo
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param courseId
        * @generated
        * @ordered
        */

       public void dlapGetItemList(String courseId) {
               // TODO : to implement	
           Map<String, String> getitemlist = new HashMap<>();
           getitemlist.put("entityid", courseId);
           session.setIsHtml(true);
           try {
               this.dCourseItemList = session.Get("getitemlist", getitemlist);
           } catch (   TransformerException | IOException | ParserConfigurationException | SAXException ex) {
               Logger.getLogger(LinkSnifferNew.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       
       /**
        * Get the list of course items set by dlapGetItemList()
     * @return 
        * @generated
        * @ordered
        */
       
       public Document getCourseItemList(){
           return this.dCourseItemList;
       }

       /**
        * Returns the dItemInfo
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @return 
        * @generated
        * @ordered
        */

       public List getCourseActivityDOM() {
           return this.dCourseActivityDOM;	
       }

       /**
        * Formats the URL to http or https then returns the new formatted url
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param address
     * @param https
     * @return 
        * @generated
        * @ordered
        */

       public String formatUrl(String address, boolean https) {
               if (address.contains("[~]") || address.contains("javascript:")){
                   // Not external DB
                   return "https://google.com";
               }
               else if ((address.contains("https://") && https) || (address.contains("http://") && !https)){
                   return address;
               }
               else if (address.contains("https://") && !https){
                   return "http://" + address.split("https://")[1];
               }
               else if (address.contains("http://") && https){
                   return "https://" + address.split("http://")[1];
               }
               else if (address.contains("//") && https){
                   return "https:" + address;
               }
               else if (https){
                   return "https://" + address;
               }
               else if (address.substring(0, 2).contains("/")){
                   // Need to fix
                   return "https://bing.com";
               }
               else{
                   return address;
               }	
       }

       /**
        * Returns a string of all the broken links
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @return 
        * @generated
        * @ordered
        */

       public String displayBrokenLinks() {
            // TODO : to implement
           String display = "==== Broken Links:\n";
           if (this.brokenLinks.isEmpty()){
               return "==== No broken links";
           }
           int size = this.brokenLinks.size();
           for (int i = 0; i < size; i++){
               String linkInfo = this.brokenLinks.get(i).toString();
               display += linkInfo;
           }
           return display;	
       }  
       
       public void run(){
           Document cil = getCourseItemList();
           Elements items = cil.getElementsByTag("item");
           for (Element item : items){               
               Elements typeTag = item.getElementsByTag("type");
               if (!typeTag.isEmpty()){
                    String itemType = typeTag.get(0).text();
                    if (itemType.contains("Resource")){
                        String entityid = item.attr("resourceentityid").split(",")[0];
                        String path = item.getElementsByTag("href").text();
                        String id = item.attr("id");
                        String content = dlapGetResource(entityid, path);
                        if (content != null){
                            processBrokenLinks(content, id);
                            System.out.println("External Links Checked: " + linkCount);
                        }  
                    }
               }       
           }           
           String display = displayBrokenLinks();
           if (display.isEmpty()){
               System.out.println("Your Course contains no broken links");
           }
           else{
               System.out.println(display);
           }
       }
}
