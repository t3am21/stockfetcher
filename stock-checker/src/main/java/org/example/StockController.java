package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class StockController {

	@GetMapping("/getStockPrice")
	public String getStockPrice(@RequestParam(required = false) String symbol, Model model) {
		if (symbol == null || symbol.trim().isEmpty()) {
			return "stock"; // Return the HTML template without data
		}

		try {
			String[] result = performAction(symbol);
			model.addAttribute("title", result[0]);
			model.addAttribute("stockPrice", result[1]);
		} catch (IOException e) {
			model.addAttribute("error", "Failed to connect to the website: " + e.getMessage());
		}

		return "stock"; // This refers to stock.html in the templates directory
	}

	public String[] performAction(String stockSymbol) throws IOException {
		// Connect to the website and get the document
		Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + stockSymbol + ".NS").get();
		String title = doc.title();

		// Select the element containing the stock price
		Element ele = doc.selectFirst("fin-streamer[data-field=\"regularMarketPrice\"]");
		if (ele != null) {
			String stockPrice = ele.text();
			return new String[]{title, stockPrice};
		} else {
			return new String[]{"", "Unable to fetch the stock price. Please check the stock symbol."};
		}
	}
}
