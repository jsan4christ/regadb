/*
 * Created on May 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.importXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestImportXML {

    /**
     * @param args
     * @throws SAXException 
     * @throws IOException 
     */
    public static void main(String[] args) throws SAXException, IOException {
        ImportFromXML instance = new ImportFromXML();
        
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        
        xmlReader.setContentHandler(instance);
        xmlReader.setErrorHandler(instance);
        
        FileReader r = new FileReader(new File(args[0]));
        xmlReader.parse(new InputSource(r));
    }

}
