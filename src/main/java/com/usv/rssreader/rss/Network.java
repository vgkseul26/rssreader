package com.usv.rssreader.rss;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Network {

    public List<RSSNote> getRSSList(String urlStr) {
        Log.d("myLogs", "getRSS");
        List<RSSNote> rssList = new ArrayList<>();
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            if ( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = urlConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(is);
                Element element = document.getDocumentElement();
                NodeList nodeList = element.getElementsByTagName("item");
                if (nodeList.getLength() > 0) {
                    for (int i = 0; i < 20/*nodeList.getLength()*/; i++) {
                        Element entry = (Element) nodeList.item(i);
                        Element titleE = (Element) entry.getElementsByTagName("title").item(0);
                        Element descriptionE = (Element) entry.getElementsByTagName("description").item(0);
                        Element linkE = (Element) entry.getElementsByTagName("link").item(0);
                        String title = titleE.getFirstChild().getNodeValue();
                        String description = descriptionE.getFirstChild().getNodeValue();
                        String link = linkE.getFirstChild().getNodeValue();
                        RSSNote rssItem = new RSSNote(title, description, link);
                        rssList.add(rssItem);
                    }
                }
            }
        } catch (IOException |ParserConfigurationException |SAXException ex) {
            Log.d("myLogs", ex.toString());
        }

        Log.d("myLogs", "end getting RSS");
        return rssList;
    }
}

