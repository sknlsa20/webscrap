package com.example.mn;




import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyController {
	
	
	@RequestMapping("/")
	public String display(Model model)
	{
		 try {
	            String url = "https://dpboss.boston/";
	            Document doc = Jsoup.connect(url)
	                    .userAgent("Mozilla/5.0")
	                    .timeout(10000)
	                    .get();

	            // Select all <div class="tkt-val">
	            Elements ticketDivs = doc.select("div.tkt-val");

	            // Create a list to store <h4> texts
	            List<String> h4List = new ArrayList<>();

	            for (Element div : ticketDivs) {
	                Elements h4s = div.select("h4");
	                for (Element h4 : h4s) {
	                    h4List.add(h4.text()); // or h4.outerHtml() for full HTML
	                }
	            }

	            model.addAttribute("h4List", h4List);

	        } catch (Exception e) {
	            e.printStackTrace();
	            model.addAttribute("h4List", new ArrayList<>());
	        }

	        return "main";
	}
	
	
	@RequestMapping("/chart/{game}")
	public String get(@PathVariable("game") String game,Model m)
	{
		
		String cleaned = game.toLowerCase()

			    // Step 2: Remove anything that's not a letter, digit, or space
			    .replaceAll("[^a-z0-9 ]", "")

			    // Step 3: Replace one or more spaces with a hyphen
			    .replaceAll("\\s+", "-")

			    // Step 4: Remove leading/trailing hyphens if any
			    .replaceAll("^-|-$", "");

			// Step 5: Append .php
			String gamename = cleaned + ".php";
		
		

		
		String tableHtml = null;
		
		try {
		    String url = "https://dpboss.boston/panel-chart-record/"+gamename;
		 
		    Document doc = Jsoup.connect(url)
		            .userAgent("Mozilla/5.0")
		            .timeout(10000)
		            .get();

		    Element table = doc.select("table").first();
		    

		 // Remove colspan attributes from <th>
		 for (Element th : table.select("th")) {
		     th.removeAttr("colspan");
		 }

		// For each row in the table
		 for (Element row : table.select("tr")) {
		     // Select all <td> elements in the row
		     Elements cells = row.select("td");

		     for (Element cell : cells) {
		         // Replace <br> with space and trim
		         String cellHtml = cell.html().replaceAll("(?i)<br\\s*/?>", " ").trim();
		         String[] parts = cellHtml.split("\\s+");

		         // Remove if cell has multiple parts and all are digits or non-word characters (symbols)
		         boolean shouldRemove = parts.length > 1 && java.util.Arrays.stream(parts)
		             .allMatch(part -> part.matches("[\\d\\W]+")); // digits or non-word characters (not letters)

		         if (shouldRemove) {
		             cell.remove();
		         }
		     }
		 }
	     
		// Now do the comparison logic
		   
		        Elements cells = table.select("td");

		        int previousValue = 0;
		        int countgreen=0;
		        int countred=0;

		        for (Element cell : cells) {
		            String text = cell.text().replaceAll("\\s+", "").trim();
		            String rawHtml = cell.html().replaceAll("(?i)<br\\s*/?>", " ").trim();
		            String[] parts = rawHtml.split("\\s+");
		            
		           

		            // Skip date cells or all-symbol cells
		            boolean isDate = text.toLowerCase().contains("to") || text.matches(".*\\d{2}[-/\\.]\\d{2}[-/\\.]\\d{4}.*");
		            boolean isSymbolOnly = parts.length > 1 && java.util.Arrays.stream(parts)
		                .allMatch(p -> p.matches("[\\d\\W]+"));

		            if (isDate || isSymbolOnly||!text.matches("\\d+(\\.\\d+)?")) {
		                continue;
		            }

		            try {
		                int currentValue = Integer.parseInt(text);

		                int sn = currentValue % 10;
		                int fn = currentValue / 10;

		                int formsum=0,formsum1=0;
		                if(previousValue%10>=8)
		                {
		                	formsum = previousValue + 2 + 30;
		                }
		                else
		                {
		                	formsum=previousValue + 42;
		                }
		                if(currentValue%10>=8)
		                {
		                	formsum1 = currentValue + 2 + 30;
		                }
		                else
		                {
		                	formsum1=currentValue + 42;
		                }
		                
		               
		                int temp = formsum;
		                int temp1=formsum1;

		                boolean markedFirst = false;
		                boolean markedSecond = false;

		                int i = 0;
		                int rem = 0,rem1=0;
		                
		                StringBuffer results = new StringBuffer("");

		                // === FIRST (for fn) ===
		                while (i < 2) {
		                    rem = formsum % 10;
		                    

		                    // Always print rem values
		                   

		                    if (rem < 5) {
		                    	
		                        if (rem == fn || rem + 5 == fn) {
		                            cell.attr("style", "background-color: green; color: white;");
		                            results.append("<span style='font-size:10px;'>&#10003;</span><br>");
		                            //cell.append("<div>first : <span style='font-size:10px;'>&#10003;</span></div>");
		                            markedFirst = true;
		                            countgreen++;
		                            break;
		                        }
		                        
		                    } else {
		                    	
		                        if (rem == fn || rem - 5 == fn) {
		                            cell.attr("style", "background-color: green; color: white;");
		                            results.append("<span style='font-size:10px;'>&#10003;</span><br>");
		                            //cell.append("<div>first : <span style='font-size:10px;'>&#10003;</span></div>");
		                            markedFirst = true;
		                            countgreen++;
		                            break;
		                        }
		                        
		                    }
		                    formsum /= 10;
		                    i++;
		                }
		               

		                if (!markedFirst) {
		                    cell.attr("style", "background-color: red; color: white;");
		                    results.append("<span style='font-size:10px;'>X</span><br>");
		                    //cell.append("<div>first : <span style='font-size:10px;'>X</span></div>");
		                    countred++;
		                }

		                // === SECOND (for sn) ===
		                formsum = temp;
		                
		                i = 0;
		                while (i < 2) {
		                    rem = formsum % 10;
		                    

		                    // Always print rem values
		                   

		                    if (rem < 5) {
		                    	
		                        if (rem == sn || rem + 5 == sn) {
		                            cell.attr("style", "background-color: green; color: white;");
		                            results.append("<span style='font-size:10px;'>&#10003;</span><br>");
		                            //cell.append("<div>second : <span style='font-size:10px;'>&#10003;</span></div>");
		                            markedSecond = true;
		                            countgreen++;
		                            break;
		                        }
		                        
		                       
		                    } else {
		                    	
		                        if (rem == sn || rem - 5 == sn) {
		                            cell.attr("style", "background-color: green; color: white;");
		                            results.append("<span style='font-size:10px;'>&#10003;</span><br>");
		                            //cell.append("<div>second : <span style='font-size:10px;'>&#10003;</span></div>");
		                            markedSecond = true;
		                            countgreen++;
		                            break;
		                        }
		                    }
		                    formsum /= 10;
		                    i++;
		                }

		                if (!markedSecond) {
		                	 results.append("<span style='font-size:10px;'>X</span><br>");
		                    //cell.append("<div>second : <span style='font-size:10px;'>X</span></div>");
		                }
		               // cell.append("<div>"+results+"</div>");
		                i=0;
		                StringBuffer yellowcontent= new StringBuffer("");
		                while (i < 2) {
		                    
		                    rem1= formsum1 % 10;

		                    // Always print rem values
		                   

		                    if (rem1 < 5) {
		                    	
		                    	yellowcontent.append( + rem1 + "<br>" + (rem1 + 5)+"<br>");
		                    	
		                        
		                    } else {
		                    	
		                    	yellowcontent.append( + rem1 + "<br>" + (rem1 - 5)+"<br>");
		                    }
		                    formsum1 /= 10;
		                    i++;
		                }
		                
		                String currentValueString ="";
		                if(currentValue<10)
		                {
		                	currentValueString="0"+currentValue;
		                	
		                }else
		                {
		                currentValueString=""+currentValue;
		                }
		                
		               
		                String combinedHtml = 
		                	    "<div style='display: flex; justify-content: space-between; align-items: center; width: 100%; font-size: 12px; white-space: nowrap;'>"
		                	        + "<div style='flex: 1;color: yellow;'>" + results + "</div>"
		                	        + "<div style='flex: 1; text-align: center;font-size: 18px;'>" + currentValueString + "</div>"
		                	        + "<div style='flex: 1; color: yellow; text-align: right;'>" + yellowcontent + "</div>"
		                	    + "</div>";
		                	cell.html(""); 
		                	cell.append(combinedHtml);
		               // cell.append("<div style='color: yellow;'>" + yellowcontent+"</div>");
		                previousValue = currentValue;

		            } catch (NumberFormatException e) {
		                e.printStackTrace();
		            }

		        
		    }
		 
		        

		        tableHtml = table.outerHtml(); // ✅ table is now modified with styles
		       
		      
		   

		} catch (Exception e) {
		    e.printStackTrace();
		}
		m.addAttribute("tableHtml", tableHtml);
		
		return "index";
	}
	

}
