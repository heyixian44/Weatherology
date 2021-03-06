package com.weatherology.services.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import com.weatherology.helpers.Icon;
import com.weatherology.helpers.Icon.*;

/** Retrieves weather forecast from Open Weather Map API
 * @author Musa V. Ahmed
 */
public class OpenWeatherMap {

    /** User agent  */
    private static final String USER_AGENT = "Mozilla/5.0";
	/** API key  */
	private static final String API_KEY = "&APPID=e318f5b8fe8a644524f903407e54141a";
	/** API URI  */
	private static final String URI = "http://api.openweathermap.org/data/2.5/forecast?zip=";
	/** Five digit zip code */
	private int zip;
    /** Floating point representation of temperature */
	protected double temperature;
    /** Floating point representation of humidity */
	protected double humidity;
    /** Floating point value used to determine icon */
	protected double icon;

	/** Constructor for OpenWeatherMap 
	 * pulls forecast data from API and instantiates class
	 * @param zip Five digit zip code
	 */
	public OpenWeatherMap(int zip) {
		this.zip = zip;
		String zipString = Integer.toString(zip);

		try {
			JsonObject jsonObject = new JsonParser().parse(this.readUrl(URI + zipString + API_KEY))
                                                    .getAsJsonObject();

			JsonObject jsonWeatherObject = jsonObject.get("list").getAsJsonArray()
				                     .get(6).getAsJsonObject()
				                     .get("main").getAsJsonObject();

		    this.temperature = jsonWeatherObject.get("temp").getAsDouble();

		    this.humidity = jsonWeatherObject.get("humidity").getAsDouble();

		    this.icon = jsonObject.get("list").getAsJsonArray()
				                  .get(6).getAsJsonObject()
				                  .get("weather").getAsJsonArray()
				                  .get(0).getAsJsonObject()
				                  .get("id").getAsDouble();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /** Returns the correct ICON
     * @return Icon
     */
    public Icon getIcon() {
        if (icon >= 200.0 && icon <= 232.0) return Icon.RAIN; //Icon.TS;
        else if (icon >= 300.0 && icon <= 321.0) return Icon.RAIN; //Icon.DR;
        else if (icon >= 500.0 && icon <= 531.0) return Icon.RAIN;
        else if (icon >= 600.0 && icon <= 622.0) return Icon.SNOW;
        else if (icon >= 701.0 && icon <= 781.0) return Icon.CLEAR_DAY; //Icon.AT;
        else if (icon >= 800.0 && icon <= 804.0) return Icon.CLOUDY;
        else if (icon >= 900.0 && icon <= 906.0) return null; //Icon.EX;
        else if (icon >= 951.0 && icon <= 962.0) return null; //Icon.ADD;
        else return null;
    }

    /** Return temperature in fahrenheit
     * @return temperature in fahrenheit
     */
    public double getTemperatureFahrenheit() {
        return ((this.temperature * (9.0/5.0)) - 459.67);
    }

    /** Return temperature in celsius
     * @return temperature in celsius
     */
    public double getTemperatureCelsius() {
        return (this.temperature - 273.15);
    }

	/** Return humidity as a percentage
	 * @return humidity
	 */
	public double getHumidity() {
		return this.humidity;
	}

	/** Fetches API data from the web
	 * @param urlString URL for API
	 * @return API data as a string
	 * @throws Exception
	 */
	protected static String readUrl(String urlString) throws Exception {
        URL obj = new URL(urlString);
        HttpURLConnection httpConn = (HttpURLConnection) obj.openConnection();

        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = httpConn.getResponseCode();
        if (responseCode == 200) {
             BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

             String responseLine;
             StringBuffer response = new StringBuffer();

             while ((responseLine = responseReader.readLine()) != null) {
                 response.append(responseLine + "\n");
             }

             responseReader.close();

             return response.toString();
        }
        return null;
	}
}
