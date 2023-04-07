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
    private String response;

    public XMLParser(String xmlString) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        this.doc = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
        this.response = "<results>\n";
    }

    public void parseXML() {
        Element rootElement = doc.getDocumentElement();
        doc.getDocumentElement().normalize();
        String rootEle = rootElement.getNodeName();
        if (rootEle.equals(SystemConstant.CREATE_TAG)) {
            parseCreateXML(rootElement);
        } else if (rootEle.equals(SystemConstant.TRANS_TAG)) {
            parseTransactionsXML(rootElement);
        } else {
            throw new IllegalArgumentException("invalid xml format"); // todo: change to xml indicate error
        }
        response += "</results>\n";
    }
    // todo: append content to response in the parse method
    public void parseCreateXML(Element createElement) {
        try {
            NodeList childNodes = createElement.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);

                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (childNode.getNodeName().equals(SystemConstant.ACCOUNT_TAG) && childNode.getParentNode().getNodeName().equals(SystemConstant.CREATE_TAG)) {
                        Element accountElem = (Element) childNode;
                        String accountId = accountElem.getAttribute(SystemConstant.ID_ATTRIBUTE);
                        String balance = accountElem.getAttribute(SystemConstant.BALANCE_TAG);
                        CreateAccountHandler createAccountHandler = new CreateAccountHandler(accountId, balance);
                        this.response += createAccountHandler.executeAction();
                        this.response += "\n";
                    } else if (childNode.getNodeName().equals(SystemConstant.SYM_TAG)) {
                        Element symbolElem = (Element) childNode;
                        String sym = symbolElem.getAttribute(SystemConstant.SYM_ATTRIBUTE);
                        NodeList symbolAccountList = symbolElem.getElementsByTagName(SystemConstant.ACCOUNT_TAG);

                        for (int j = 0; j < symbolAccountList.getLength(); j++) {
                            Node symbolAccountNode = symbolAccountList.item(j);
                            if (symbolAccountNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element symbolAccountElem = (Element) symbolAccountNode;
                                String symbolAccountId = symbolAccountElem.getAttribute(SystemConstant.ID_ATTRIBUTE);
                                String num = symbolAccountElem.getTextContent();
                                CreateSymbolHandler createSymbolHandler = new CreateSymbolHandler(sym, symbolAccountId, num);
                                this.response += createSymbolHandler.executeAction();
                                this.response += "\n";
                                //System.out.println("Symbol Account ID: " + symbolAccountId + ", NUM: " + num);
                            }
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
            String accountNum = transElement.getAttribute(SystemConstant.ID_ATTRIBUTE);
            //System.out.println("Account ID: " + accountNum);
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
                            OpenHandler openHandler = new OpenHandler(accountNum, sym, amount, limit);
                            this.response += openHandler.executeAction();
                            this.response += "\n";
                            break;
                        case "query":
                            String queryTransId = childElem.getAttribute("id");
                            //System.out.println("Query - Transaction ID: " + queryTransId);
                            if (!MyUtil.isNumeric(queryTransId)) {
                                response += "<error id=\"" + queryTransId + "\">" + "invalid transaction id" + "</error>\n";
                                break;
                            }
                            QueryHandler queryHandler = new QueryHandler(Long.parseLong(queryTransId), accountNum);
                            response += queryHandler.executeAction();
                            break;
                        case "cancel":
                            String cancelTransId = childElem.getAttribute("id");
                            //System.out.println("Cancel - Transaction ID: " + cancelTransId);
                            if (!MyUtil.isNumeric(cancelTransId)) {
                                response += "<error id=\"" + cancelTransId + "\">" + "invalid transaction id" + "</error>\n";
                                break;
                            }
                            CancelHandler cancelHandler = new CancelHandler(Long.parseLong(cancelTransId), accountNum);
                            response += cancelHandler.executeAction();
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

    public String buildXMLReply() {
        // send the response to client
        return response;
    }

}
