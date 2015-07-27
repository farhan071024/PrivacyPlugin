package org.jetbrains.PrivacyPlugin;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
// Converts filtered csv file to xml mapping
public class CsvToXml {
    protected DocumentBuilderFactory domFactory = null;
    protected DocumentBuilder domBuilder = null;
    public CsvToXml() {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();
        } catch (FactoryConfigurationError exp) {
            System.err.println(exp.toString());
        } catch (ParserConfigurationException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }

    }
    public int convertFile(String csvFileName, String xmlFileName, String delimiter){
        int rowsCount = -1;
        try {
            org.w3c.dom.Document newDoc =  domBuilder.newDocument();
            org.w3c.dom.Element rootElement =  newDoc.createElement("mapping");
            newDoc.appendChild(rootElement);
            BufferedReader csvReader;
            csvReader = new BufferedReader(new FileReader(csvFileName));
            int fieldCount = 0;
            String[] csvFields = null;
            StringTokenizer stringTokenizer = null;
            String curLine = csvReader.readLine();
            if (curLine != null) {
                stringTokenizer = new StringTokenizer(curLine, delimiter);
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    csvFields = new String[fieldCount];
                    int i = 0;
                    while (stringTokenizer.hasMoreElements())
                        csvFields[i++] = String.valueOf(stringTokenizer.nextElement());
                }
            }
            while ((curLine = csvReader.readLine()) != null) {
                stringTokenizer = new StringTokenizer(curLine, delimiter);
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    org.w3c.dom.Element rowElement =  newDoc.createElement("pharsetoapi");
                    int i = 0;
                    while (stringTokenizer.hasMoreElements()) {
                        try {
                            String curValue = String.valueOf(stringTokenizer.nextElement());
                            org.w3c.dom.Element curElement =  newDoc.createElement(csvFields[i++]);
                            curElement.appendChild(newDoc.createTextNode(curValue));
                            rowElement.appendChild(curElement);
                        }catch (Exception exp) {
                        }
                    }
                    rootElement.appendChild(rowElement);
                    rowsCount++;
                }
            }
            csvReader.close();
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            Source src = new DOMSource(newDoc);
            Result result = new StreamResult(new File(xmlFileName));
            aTransformer.transform(src, result);
            rowsCount++;
        } catch (IOException exp) {
            System.err.println(exp.toString());
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
        return rowsCount;
    }
}

