package librarysystem.resources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public final class Resources {

    public static final String ENGLISH_LANGUAGE_FILE = "english";
    public static final String RESOURCES_FOLDER = "resources";
    public static final String IMAGES_FOLDER = "images";
    public static final String RESOURCES_FILE = "resources_selected_language.txt";
    private static final HashMap<String, Object> parsedXML = new HashMap<>();
    private static String lang = "";

    public static String getLanguage() {
        if (lang != null && lang.length() > 0) {
            return lang;
        }
        // read from file
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCES_FILE))) {
            lang = reader.readLine().trim().toLowerCase();
        } catch (IOException e) {
            // left blank in case file doesn't exist.
        }
        if (lang != null && lang.length() > 0) {
            return lang;
        }
        return Resources.ENGLISH_LANGUAGE_FILE;
    }

    public static void setLanguage(String langs) {
        lang = langs.toLowerCase();

        // save to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCES_FILE, false))) {
            writer.write(lang);
        } catch (IOException e) {
           // left blank in case file doesn't exist.
        }
        loadFile();
    }

    public static void loadFile() {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        try (BufferedReader reader = new BufferedReader(new FileReader("langs/" + (Objects.equals(lang, "") ? ENGLISH_LANGUAGE_FILE : lang) + ".xml"))) {
            StringBuilder xmlStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xmlStringBuilder.append(line);
            }

            ByteArrayInputStream input = new ByteArrayInputStream(
                    xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8));

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(input);
            NodeList elements = doc.getElementsByTagName("string");

            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                parsedXML.put(element.getAttribute("name"), element.getTextContent());
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getString(String stringId) {
        if (parsedXML.isEmpty()) {
            throw new NullPointerException("You need to call method: setLanguage first");
        }
        return (String) parsedXML.get(stringId);
    }

    public static Integer getInteger(String stringId) {
        if (parsedXML.isEmpty()) {
            throw new NullPointerException("You need to call method: setLanguage first");
        }
        return (Integer) parsedXML.get(stringId);
    }

}