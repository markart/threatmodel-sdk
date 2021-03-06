/*
 * This file is part of the ThreatModeling SDK.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.springett.threatmodeling;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class dynamically generates {@link us.springett.threatmodeling.model.CWE} from
 * cwec_v2.9.xml obtained from https://cwe.mitre.org/data/xml/cwec_v2.9.xml
 */
public class CweEnumGenerator {

    private static final String INDENT = "    ";
    private static final Map<Integer, String> CWE_Mappings = new TreeMap<>();

    public static void main(String[] args) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("public enum CWE {\n\n");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse("src/test/resources/cwec_v2.9.xml");
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        XPathExpression expr1 = xpath.compile("/Weakness_Catalog/Categories/Category");
        XPathExpression expr2 = xpath.compile("/Weakness_Catalog/Weaknesses/Weakness");
        XPathExpression expr3 = xpath.compile("/Weakness_Catalog/Compound_Elements/Compound_Element");

        parseNodes((NodeList)expr1.evaluate(doc, XPathConstants.NODESET));
        parseNodes((NodeList)expr2.evaluate(doc, XPathConstants.NODESET));
        parseNodes((NodeList)expr3.evaluate(doc, XPathConstants.NODESET));

        for (Map.Entry<Integer, String> entry : CWE_Mappings.entrySet()) {
            sb.append(INDENT).append("CWE_" + entry.getKey() + "(" + entry.getKey() + ", \"" + entry.getValue().replaceAll("\\\\", "\\\\\\\\") + "\"),\n");
        }
        sb.delete(sb.length()-2, sb.length()-0); // removes last comma and newline character
        sb.append(";\n\n");

        sb.append(INDENT).append("private int id;\n");
        sb.append(INDENT).append("private String name;\n\n");
        sb.append(INDENT).append("CWE(int id, String name) {\n");
        sb.append(INDENT).append(INDENT).append("this.id = id;\n");
        sb.append(INDENT).append(INDENT).append("this.name = name;\n");
        sb.append(INDENT).append("}\n\n");
        sb.append(INDENT).append("public int getId() {\n").append(INDENT).append(INDENT).append("return id;\n").append(INDENT).append("}\n\n");
        sb.append(INDENT).append("public String getName() {\n").append(INDENT).append(INDENT).append("return name;\n").append(INDENT).append("}\n\n");
        sb.append("}");

        System.out.println(sb.toString());
    }

    private static void parseNodes(NodeList nodeList) {
        for (int i=0; i<nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attributes = node.getAttributes();
            Integer id = Integer.valueOf(attributes.getNamedItem("ID").getNodeValue());
            String desc = attributes.getNamedItem("Name").getNodeValue();
            CWE_Mappings.put(id, desc);
        }
    }

}
