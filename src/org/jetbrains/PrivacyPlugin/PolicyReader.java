/* parse through privacy policy text file, collects relevant phrases,
compares with xml mapping and generates a list of corresponding apis */
package org.jetbrains.PrivacyPlugin;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PolicyReader {
     List<String>apiList=new ArrayList<String>();
    public void reader() throws IOException {
        FileReader fileReader = new FileReader("D:\\policyText.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String text = "", text2 = "",text3="";
        String key = "collect";
        String line = bufferedReader.readLine();
        while (line != null) {
            text += line;
            line = bufferedReader.readLine();
        }
        Pattern pattern = Pattern.compile(key);
        Matcher regMatcher = pattern.matcher(text);
        while (regMatcher.find()) {
            if (regMatcher.group().length() != 0) {
                text2 = text2 + text.substring(regMatcher.start() + 7, text.indexOf("."));
            }
        }
        String phrases[]={"bandwidth","country","device identifier","geo-location information","gps",
                "internet service provider","ip address","location","location information","mac address",
                "mobile country code","phone number","telephone number","unique identifier"};
        List<String> usedPhrases= new ArrayList<String>();
        for (int i=0;i<phrases.length;i++){
            Pattern pattern1=Pattern.compile(phrases[i]);
            Matcher regMatcher1= pattern1.matcher(text2);
            while(regMatcher1.find()){
                if(regMatcher1.group().length() != 0){
                    usedPhrases.add(phrases[i]);
                }
            }
        }
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder=factory.newDocumentBuilder();
            org.w3c.dom.Document document= builder.parse("D:\\PharseApiXml.xml");
            NodeList nodeList=document.getElementsByTagName("pharsetoapi");
            for(int i=0;i<nodeList.getLength();i++){
                Node ph= nodeList.item(i);
                Element element= (Element) ph;
                NodeList phraseApi= element.getElementsByTagName("phrase");
                NodeList phraseApi1=element.getElementsByTagName("api");
                for(int j=0;j<phraseApi.getLength();j++){
                    Node ph1=phraseApi.item(j);
                    Node ph2=phraseApi1.item(j);
                    for(int k=0;k<usedPhrases.size();k++){
                        if(ph1.getTextContent().equals(usedPhrases.get(k))){
                            apiList.add(ph2.getTextContent());
                        }
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        System.out.println("\n-----------Mapping phrases matched with privacy policy phrases-----------");
        for(int i=0;i<usedPhrases.size();i++){
            System.out.println(usedPhrases.get(i));
        }
        System.out.println("\n-----------List of APIs from policy------------");
          for(int i=0;i<apiList.size();i++){
            System.out.println(apiList.get(i));
        }
    }
    public String getapiList(int i){
        return apiList.get(i);
    }
}


