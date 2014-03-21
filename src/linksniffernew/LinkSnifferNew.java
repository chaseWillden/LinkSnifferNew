/*
 * Author: Chase Willden
 * 
 * Error Numbers:
 * 132: Ping Error
 * 133: Login Error
 */

package linksniffernew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

       private List dCourseActivityDOM;
       private final List errorLog = new ArrayList();
       private Document dCourseItemList;
       private Session session;
       private List brokenLinks;
       private String baseUrl;

       /**
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
        * @generated
        */
       public LinkSnifferNew(){
               super();
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
           String[] tmp = {errorNum, errorMsg};
           this.errorLog.add(tmp);
       }
       
       /**
        * Adds Errors to the Error log.
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
     * @param url
        * @generated
        * @ordered
        */
       
       public void addError(String url){
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
            this.session = new Session("Link Sniffer", "http://gls.agilix.com/dlap.ashx");
            // Login
            org.jsoup.nodes.Document result = session.Login(prefix, username, password);
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
            Map<String, String> params = new HashMap<>();
            params.put("entityid", entityid);
            params.put("path", path);
            return session.Get("getresource", params);
       }

       /**
        * <!-- begin-user-doc -->
        * <!--  end-user-doc  -->
        * @generated
        * @ordered
        */

       public void logout() {
           this.session.Logout();
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
           List links = null;
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
           for (int i = 0; i < hrefList.size(); i++){
               String url = formatUrl(hrefList.get(i).toString(), true);
               if (!pingUrl(url)){
                   this.brokenLinks.add("url=" + url + "&activity=" + activity);
               }
           }
       }

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
           this.dCourseItemList = session.Get("getitemlist", getitemlist);
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
               if ((address.contains("https://") && https) || (address.contains("http://") && !https)){
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
           String display = "";
           int size = this.brokenLinks.size();
           for (int i = 0; i < size; i++){
               String linkInfo = this.brokenLinks.get(i).toString();
           }
           return "";	
       }
       
}
