import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.*;

public class XMLParserTest {

    @Test
    public void testParseCreateXML() throws IOException, SAXException, ParserConfigurationException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<create>\n" +
                " <account id=\"123457\" balance=\"1000\"/>\n" +
                " <account id=\"123456\" balance=\"1000\"/>\n" +
                " <symbol sym=\"USD\">\n" +
                " <account id=\"123456\">100</account>\n" +
                " <account id=\"123457\">101</account>\n" +
                " </symbol>\n" +
                " <symbol sym=\"CYN\">\n" +
                " <account id=\"123456\">100</account>\n" +
                " </symbol>\n" +
                "</create>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidRoot() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<nop>\n" +
                "</nop>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
    }

    @Test
    public void testParseTransactionXML() throws IOException, SAXException, ParserConfigurationException {
        String xml = "<transactions id=\"123456\">\n" +
                " <order sym=\"USD\" amount=\"100\" limit=\"80\"/> \n" +
                " <query id=\"abc\"/> \n" +
                " <cancel id=\"abc\"/>\n" +
                "</transactions>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
    }

}