import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.faces.bean.*;

@ManagedBean(name="tcmb")
@RequestScoped
public class TCMBReader {

	private static TCMBReader instance = null;

	public static TCMBReader getInstance() {
		if (instance == null) {
			instance = new TCMBReader();
		}
		return instance;
	}

	public ArrayList<Currency> getCurrencies() {
		ArrayList<Currency> items = new ArrayList<Currency>();
		try {
			//Ilk once DocumentBuilder nesnesini yaratiyoruz
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			//Indirecegimiz xml belgesinin URL'si
			//Burada yerel bir adreste kullanabilirsiniz
			URL u = new URL("http://www.tcmb.gov.tr/kurlar/today.xml");

			//Belgeyi URL'den yukleyip, builder yardimiyla parse ediyoruz
			//Butun belge bellege alinmis oldu
			Document doc = builder.parse(u.openStream());

			//Belge icerisindeki Currency etiketli elemanlari aliyoruz
			NodeList nodes = doc.getElementsByTagName("Currency");
			//Bu elemanlari dolasiyoruz
			for (int i = 0; i < nodes.getLength(); i++) {
				//Her bir eleman icin bir Currency nesnesi yaratiyoruz
				Currency currency = new Currency();
				Element element = (Element) nodes.item(i);
				//Currency etiketi icerisinde tanimli ozellikleri aliyoruz (property)
				currency.setCurrencyCode(element.getAttribute("CurrencyCode"));
				currency.setKod(element.getAttribute("Kod"));
				//Currency etiketinin alt etiketlerini aliyoruz
				currency.setCurrencyName(getElementValue(element,"CurrencyName"));
				currency.setIsim(getElementValue(element, "Isim"));
				currency.setBanknoteBuying(getFloat(getElementValue(element,"BanknoteBuying")));
				currency.setBanknoteSelling(getFloat(getElementValue(element,"BanknoteSelling")));
				currency.setCrossrateEURO(getFloat(getElementValue(element,"CrossrateEURO")));
				currency.setCrossrateOther(getFloat(getElementValue(element,"CrossrateOther")));
				currency.setCrossrateUSD(getFloat(getElementValue(element,"CrossrateUSD")));
				currency.setForexBuying(getFloat(getElementValue(element,"ForexBuying")));
				currency.setForexSelling(getFloat(getElementValue(element,"ForexSelling")));
				currency.setUnit(getFloat(getElementValue(element, "Unit")));
				items.add(currency);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		} catch (Exception ex) {
		}
		return "";
	}

	protected float getFloat(String value) {
		if (value != null && !value.equals("")) {
			return Float.parseFloat(value);
		}
		return 0;
	}

	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent
				.getElementsByTagName(label).item(0));
	}
}
