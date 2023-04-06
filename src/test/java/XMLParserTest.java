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
                " <symbol sym=\"USD\">\n" +
                " <account id=\"123456\">100</account>\n" +
                " <account id=\"123457\">101</account>\n" +
                " </symbol>\n" +
                " <symbol sym=\"CYN\">\n" +
                " <account id=\"123456\">100</account>\n" +
                " </symbol>\n" +
                " <account id=\"123456\" balance=\"1000\"/>\n" +
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

    @Test
    public void testParseQueryFunction() throws IOException, SAXException, ParserConfigurationException {
        String xml = "<transactions id=\"777\">\n" +
                " <query id=\"1\"/> \n" +
                "</transactions>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
    }

    @Test
    public void testParseQueryFunctionInvalidId() throws IOException, SAXException, ParserConfigurationException {
        String xml = "<transactions id=\"132\">\n" +
                " <query id=\"55\"/> \n" +
                "</transactions>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
    }

    @Test
    public void testParseCancelFunction() throws IOException, SAXException, ParserConfigurationException {
        String xml = "<transactions id=\"777\">\n" +
                " <cancel id=\"1\"/> \n" +
                "</transactions>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
        System.out.println(xmlParser.buildXMLReply());
    }

    @Test
    public void testBuildXMLReply() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<transactions id=\"132\">\n" +
                " <query id=\"1\"/> \n" +
                " <cancel id=\"1\"/>\n" +
                "</transactions>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
        System.out.println(xmlParser.buildXMLReply());
    }

    @Test
    public void testParseCreateFunction() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<create>\n" +
                "<account id=\"777\" balance=\"100\"/>\n" +
                "</create>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
        System.out.println(xmlParser.buildXMLReply());
    }

    @Test
    public void testParseCreateSymFunction() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<create>\n" +
                "<symbol sym=\"BIT\"> \n" +
                "<account id=\"777\">10</account>\n" +
                "</symbol>" +
                "</create>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
        System.out.println(xmlParser.buildXMLReply());
    }

    @Test
    public void testParseCreateWithSymFunction() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<create>\n" +
                "<account id=\"888\" balance=\"100\"/>\n" +
                "<account id=\"999\" balance=\"100\"/>\n" +
                "<symbol sym=\"BIT\"> \n" +
                "<account id=\"777\">10</account>\n" +
                "</symbol>" +
                "<symbol sym=\"BTC\"> \n" +
                "<account id=\"888\">100</account>\n" +
                "</symbol>" +
                "</create>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
        System.out.println(xmlParser.buildXMLReply());
    }

    @Test
    public void testParseOpenFunction() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<transactions id=\"777\">\n" +
                "<order sym=\"BIT\" amount=\"-5\" limit=\"1\"/> \n" +
                "</transactions>";
        XMLParser xmlParser = new XMLParser(xml);
        xmlParser.parseXML();
        System.out.println(xmlParser.buildXMLReply());
    }
}