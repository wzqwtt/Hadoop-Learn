package com.wzq.hadoop.jaxp;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * {@link https://www.oracle.com/java/technologies/jaxp-doc-object-model.html}
 *
 * @author wzq
 * @create 2023-04-10 13:18
 */
public class DOMEcho {

    static final String outputEncoding = "UTF-8";

    private PrintWriter out;

    private int indent = 0;

    private final String basicIndent = " ";

    DOMEcho(PrintWriter out) {
        this.out = out;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {

        String filename = "core-default.xml";
        URL resource = DOMEcho.class.getClassLoader().getResource(filename);
        System.out.println(resource == null ? "null" : resource.getPath());

        boolean dtdValidate = true;
        boolean xsdValidate = true;

        // 1、实例化工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // 1.1 配置工厂
        dbf.setNamespaceAware(true);
        // dbf.setValidating(dtdValidate || xsdValidate);
        dbf.setIgnoringComments(true);

        // 2、获取解析器并解析文件
        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document doc = db.parse(new File(resource.toURI()));
//
//        // 3、
//        OutputStreamWriter outWriter = new OutputStreamWriter(System.out, outputEncoding);
//        new DOMEcho(new PrintWriter(outWriter)).echo(doc);

    }

    /**
     * 获取节点的信息
     *
     * @param n
     */
    private void printlnCommon(Node n) {
        out.print(" nodeName=\"" + n.getNodeName() + "\"");

        String val = n.getNamespaceURI();
        if (val != null) {
            out.print(" uri=\"" + val + "\"");
        }

        val = n.getPrefix();
        if (val != null) {
            out.print(" pre=\"" + val + "\"");
        }

        val = n.getLocalName();
        if (val != null) {
            out.print(" local=\"" + val + "\"");
        }

        val = n.getNodeValue();
        if (val != null) {
            out.print(" nodeValue=");
            if (val.trim().equals("")) {
                // Whitespace
                out.print("[WS]");
            } else {
                out.print("\"" + n.getNodeValue() + "\"");
            }
        }
        out.println();
    }

    private void outputIndentation() {
        for (int i = 0; i < indent; i++) {
            out.print(basicIndent);
        }
    }

    private void echo(Node n) {
        outputIndentation();

        int type = n.getNodeType();
        switch (type) {
            case Node.ATTRIBUTE_NODE:
                out.print("ATTR:");
                printlnCommon(n);
                break;
            case Node.CDATA_SECTION_NODE:
                out.print("CDATA:");
                printlnCommon(n);
                break;
            case Node.COMMENT_NODE:
                out.print("COMM:");
                printlnCommon(n);
                break;
            case Node.DOCUMENT_FRAGMENT_NODE:
                out.print("DOC_FRAG:");
                printlnCommon(n);
                break;
            case Node.DOCUMENT_NODE:
                out.print("DOC:");
                printlnCommon(n);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                out.print("DOC_TYPE:");
                printlnCommon(n);
                NamedNodeMap nodeMap = ((DocumentType) n).getEntities();
                indent += 2;
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    Entity entity = (Entity) nodeMap.item(i);
                    echo(entity);
                }
                indent -= 2;
                break;
            case Node.ELEMENT_NODE:
                out.print("ELEM:");
                printlnCommon(n);

                NamedNodeMap atts = n.getAttributes();
                indent += 2;
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    echo(att);
                }
                indent -= 2;
                break;
            case Node.ENTITY_NODE:
                out.print("ENT:");
                printlnCommon(n);
                break;
            case Node.ENTITY_REFERENCE_NODE:
                out.print("ENT_REF:");
                printlnCommon(n);
                break;
            case Node.NOTATION_NODE:
                out.print("NOTATION:");
                printlnCommon(n);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                out.print("PROC_INST:");
                printlnCommon(n);
                break;
            case Node.TEXT_NODE:
                out.print("TEXT:");
                printlnCommon(n);
                break;
            default:
                out.print("UNSUPPORTED NODE: " + type);
                printlnCommon(n);
                break;
        }

        indent++;
        for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
            echo(child);
        }
        indent--;
    }

    /**
     * Find the named subnode in a node's sublist.
     * <li>Ignores comments and processing instructions.
     * <li>Ignores TEXT nodes (likely to exist and contain
     * ignorable whitespace, if not validating.
     * <li>Ignores CDATA nodes and EntityRef nodes.
     * <li>Examines element nodes to find one with
     * the specified name.
     * </ul>
     *
     * @param name the tag name for the element to find
     * @param node the element node to start searching from
     * @return the Node found
     */
    public Node findSubNode(String name, Node node) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            System.err.println("Error: Search node not of element type");
            System.exit(22);
        }

        if (!node.hasChildNodes()) {
            return null;
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node subnode = list.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                if (subnode.getNodeName().equals(name)) {
                    return subnode;
                }
            }
        }
        return null;
    }

    /**
     * Return the text that a node contains. This routine:<ul>
     * <li>Ignores comments and processing instructions.
     * <li>Concatenates TEXT nodes, CDATA nodes, and the results of
     * recursively processing EntityRef nodes.
     * <li>Ignores any element nodes in the sublist.
     * (Other possible options are to recurse into element
     * sublists or throw an exception.)
     * </ul>
     *
     * @param node a  DOM node
     * @return a String representing its contents
     */
    public String getText(Node node) {
        StringBuffer result = new StringBuffer();

        if (!node.hasChildNodes()) {
            return "";
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node subnode = list.item(i);
            if (subnode.getNodeType() == Node.TEXT_NODE) {
                result.append(subnode.getNodeValue());
            } else if (subnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                result.append(subnode.getNodeValue());
            } else if (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                // 递归
                result.append(getText(subnode));
            }
        }

        return result.toString();
    }
}
