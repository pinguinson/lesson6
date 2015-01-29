package com.pinguinson.lesson7.loading;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class RSSHandler extends DefaultHandler {

    private RSSArticle currentArticle;
    private ArrayList<RSSArticle> articles = new ArrayList<>();

    private int numberOfArticles = 0;

    private static final int ARTICLES_LIMIT = 20;


    StringBuffer chars = new StringBuffer();


    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        chars = new StringBuffer();
        if (qName.equalsIgnoreCase("item") || qName.equalsIgnoreCase("entry")) {
            currentArticle = new RSSArticle();
        }
    }


    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (currentArticle != null) {
            if (localName.equalsIgnoreCase("title")) {
                String text = chars.toString();
                currentArticle.setTitle(text.trim());
            } else if (localName.equalsIgnoreCase("link")) {
                currentArticle.setUrl(chars.toString());
            }
            if (localName.equalsIgnoreCase("item") || localName.equalsIgnoreCase("entry")) {
                articles.add(currentArticle);
                currentArticle = new RSSArticle();
                numberOfArticles++;
                if (numberOfArticles >= ARTICLES_LIMIT) {
                    throw new SAXException();
                }
            }
        }
    }


    public void characters(char ch[], int start, int length) {
        chars.append(new String(ch, start, length));
    }


    public ArrayList<RSSArticle> getArticles(String feedUrl) throws IOException {
        URL url;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            url = new URL(feedUrl);
            reader.setContentHandler(this);
            InputSource source = new InputSource(url.openStream());
            reader.parse(source);
        } catch (SAXException e) {
            Log.e("SAX Handler error", e.getMessage() + "");
        } catch (ParserConfigurationException e) {
            Log.e("Parsing error", e.toString());
        }
        for (RSSArticle article : articles) {
            Log.d("THIS IS ENTRY URL: " + article.getUrl(), "");
        }
        return articles;
    }
}