package com.example.mn;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Service {
	
	 public static String cleanTableHtml(String tableHtml) {
	        Document doc = Jsoup.parse(tableHtml);

	        Elements tdElements = doc.select("td");

	        for (Element td : tdElements) {
	            // Get inner HTML of td
	            String innerHtml = td.html().trim();

	            // Remove spaces and replace <br> with comma to normalize
	            String normalized = innerHtml
	                .replaceAll("(?i)<br\\s*/?>", ",")  // replace <br> or <br/> with comma
	                .replaceAll("[\\s]", "");            // remove all spaces

	            // Now check if normalized contains only digits and commas, e.g. "2,8,8,"
	            // Allow trailing comma, so remove trailing comma if any
	            normalized = normalized.replaceAll(",$", "");

	            // Regex: only digits and commas, and no letters or other chars
	            if (normalized.matches("[0-9,]+")) {
	                // This td contains only numbers separated by <br>, remove it
	                td.remove();
	            }
	        }

	        return doc.body().html();
	    }

}
