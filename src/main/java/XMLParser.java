import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class XMLParser {
    private final Document doc;

    public XMLParser(String xmlString) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        this.doc = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
    }

    public void parseXML() {
        Element rootElement = doc.getDocumentElement();
        doc.getDocumentElement().normalize();
        String rootEle = rootElement.getNodeName();
        if (rootEle.equals(XMLConstant.CREATE_TAG)) {
            parseCreateXML(rootElement);
        } else if (rootEle.equals(XMLConstant.TRANS_TAG)) {
            parseTransactionsXML(rootElement);
        } else {
            throw new IllegalArgumentException("invalid xml format");
        }
    }

    public void parseCreateXML(Element createElement) {
        try {
            NodeList accountNodes = createElement.getElementsByTagName(XMLConstant.ACCOUNT_TAG);
            NodeList symbolNodes = createElement.getElementsByTagName(XMLConstant.SYM_TAG);
            for (int i = 0; i < accountNodes.getLength(); i++) {
                Node accountNode = accountNodes.item(i);
                if (accountNode.getNodeType() == Node.ELEMENT_NODE && accountNode.getParentNode().getNodeName().equals(XMLConstant.CREATE_TAG)) {
                    Element accountElem = (Element) accountNode;
                    String accountId = accountElem.getAttribute(XMLConstant.ID_ATTRIBUTE);
                    String balance = accountElem.getAttribute(XMLConstant.BALANCE_TAG);
                    System.out.println("Account ID: " + accountId + ", Balance: " + balance);
                }
            }

            for (int i = 0; i < symbolNodes.getLength(); i++) {
                Node symbolNode = symbolNodes.item(i);
                if (symbolNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element symbolElem = (Element) symbolNode;
                    String sym = symbolElem.getAttribute(XMLConstant.SYM_ATTRIBUTE);
                    System.out.println("Symbol: " + sym);
                    NodeList symbolAccountList = symbolElem.getElementsByTagName(XMLConstant.ACCOUNT_TAG);

                    for (int j = 0; j < symbolAccountList.getLength(); j++) {
                        Node symbolAccountNode = symbolAccountList.item(j);
                        if (symbolAccountNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element symbolAccountElem = (Element) symbolAccountNode;
                            String symbolAccountId = symbolAccountElem.getAttribute(XMLConstant.ID_ATTRIBUTE);
                            String num = symbolAccountElem.getTextContent();
                            System.out.println("Symbol Account ID: " + symbolAccountId + ", NUM: " + num);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseTransactionsXML(Element transElement) {
        try {
            String accountId = transElement.getAttribute(XMLConstant.ID_ATTRIBUTE);
            System.out.println("Account ID: " + accountId);
            NodeList children = transElement.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElem = (Element) node;
                    switch (childElem.getNodeName()) {
                        case "order":
                            String sym = childElem.getAttribute("sym");
                            String amount = childElem.getAttribute("amount");
                            String limit = childElem.getAttribute("limit");
                            System.out.println("Order - Symbol: " + sym + ", Amount: " + amount + ", Limit: " + limit);
                            break;
                        case "query":
                            String queryTransId = childElem.getAttribute("id");
                            System.out.println("Query - Transaction ID: " + queryTransId);
                            break;
                        case "cancel":
                            String cancelTransId = childElem.getAttribute("id");
                            System.out.println("Cancel - Transaction ID: " + cancelTransId);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid transaction type: " + childElem.getNodeName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildCreateXMLReply() {

    }

    public void buildTransXMLReply() {

    }
}
