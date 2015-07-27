/* parse through the source code of application,searches for static method signature generated from xml file,
makes a listof corresponding apis,compares the list of apis to list of apis from privacy policy and find
violations.If violations are found,map back to the phrases need to be included in privacy policy*/
package org.jetbrains.PrivacyPlugin;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GenerateAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("\n------------------------PrivacyPlugin Report--------------------------");
        PsiFile psiFile=e.getData(LangDataKeys.PSI_FILE);
        Editor editor=  e.getData(PlatformDataKeys.EDITOR);
        if((psiFile== null)||(editor==null) ) {
            e.getPresentation().setEnabled(false);
        }
            int offset = editor.getCaretModel().getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);
            PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
            String fields = "";
            fields = fields + psiClass.getText();
        Excel excel = new Excel();
        try {
            excel.readWrite();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        CsvToXml csvToXml= new CsvToXml();
        csvToXml.convertFile("D:\\PhraseApi.csv","D:\\PharseApiXml.xml",",");
        PolicyReader policyReader=new PolicyReader();
        try {
            policyReader.reader();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        List<String>codeApi=new ArrayList<String>();
        try {
            DocumentBuilder builder=factory.newDocumentBuilder();
            org.w3c.dom.Document document= builder.parse("D:\\PharseApiXml.xml");
            NodeList nodeList=document.getElementsByTagName("pharsetoapi");
            for(int i=0;i<nodeList.getLength();i++){
                Node ph= nodeList.item(i);
                Element element= (Element) ph;
                NodeList phraseApi1=element.getElementsByTagName("api");
                for(int j=0;j<phraseApi1.getLength();j++){
                    Node ph2=phraseApi1.item(j);
                    int i1=ph2.getTextContent().indexOf(">");
                    int i2=0;
                    while(i2==0){
                        i1=i1-1;
                        if(ph2.getTextContent().charAt(i1)==' '){
                            i2=1;
                        }
                    }
                    String s=ph2.getTextContent().substring(i1+1,ph2.getTextContent().indexOf(">"));
                    int j1=ph2.getTextContent().indexOf(":");
                    int j2=0;
                    while (j2==0){
                        j1=j1-1;
                        if(ph2.getTextContent().charAt(j1)=='.'){
                            j2=1;
                        }
                    }
                    String p = ph2.getTextContent().substring(j1+1,ph2.getTextContent().indexOf(":"));
                    String stCall=p+"."+s;
                    Pattern pattern1=Pattern.compile(stCall);
                    Matcher regMatcher1= pattern1.matcher(fields);
                    while(regMatcher1.find()){
                        if(regMatcher1.group().length() != 0){
                            codeApi.add(ph2.getTextContent());
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("\n-------List of APIs from source code------");
        for (int i=0;i<codeApi.size();i++){
            System.out.println(codeApi.get(i));
        }
        List<String>apiViolation=new ArrayList<String>();
        List<String>modifiedPhrases= new ArrayList<String>();
        int flag=0;
        for(int i=0;i<codeApi.size();i++){
            for(int j=0;j<policyReader.apiList.size();j++){
                if(policyReader.getapiList(j).equals(codeApi.get(i))){
                    flag=1;
                    break;
                }
                else{
                    flag=0;
                }
            }
            if(flag == 0){
                apiViolation.add(codeApi.get(i));
            }
        }
        if(apiViolation.isEmpty()){
            System.out.println("\nThis app has no privacy policy violations");
        }
        else{
            System.out.println("\nThis app has privacy policy violations at APIS:");
            for(int i=0;i<apiViolation.size();i++){
                System.out.println(apiViolation.get(i));
            }
            DocumentBuilderFactory factory1 =DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder=factory1.newDocumentBuilder();
                org.w3c.dom.Document document1= builder.parse("D:\\PharseApiXml.xml");
                NodeList nodeList1=document1.getElementsByTagName("pharsetoapi");
                System.out.println("\nThis app should include following phrases to avoid privacy policy violations:");
                for(int i=0;i<nodeList1.getLength();i++){
                    Node ph5= nodeList1.item(i);
                    Element element1= (Element) ph5;
                    NodeList phraseApi= element1.getElementsByTagName("phrase");
                    NodeList phraseApi1=element1.getElementsByTagName("api");
                        for(int j=0;j<phraseApi1.getLength();j++){
                            Node ph3=phraseApi.item(j);
                            Node ph4=phraseApi1.item(j);
                            for(int k=0;k<apiViolation.size();k++){
                            if(ph4.getTextContent().equals(apiViolation.get(k))){
                                modifiedPhrases.add(ph3.getTextContent());
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            } catch (SAXException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if(!modifiedPhrases.isEmpty()) {
            for (int i = 0; i < modifiedPhrases.size(); i++) {
                System.out.println(modifiedPhrases.get(i));
                System.out.println("or");
            }
            System.out.println("Privacy policy violation will exist");
        }
    }
}




