import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//Retrieve weather data from API in order to fetch the latest weather conditions
//from the external API and display it through the GUI.
public class WeatherApp {
    //Fetch weather data for a given location
    public static JSONObject getWeatherData(String locationName){
        //Get location Coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        //Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //Build API Request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
        System.out.println(urlString);
        try{
            //Call api and get response
            HttpURLConnection connection = fetchApiResponse(urlString);

            //Check for response status 200 = OK
            if(connection.getResponseCode() != 200){
                System.out.println("Error: could not connect to API");
            }

            //Store JSON data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            //Read and store into the StringBuilder
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }

            //Close Scanner and url connection
            scanner.close();
            connection.disconnect();

            //Parse JSON String into JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //Get weather hourly data
            JSONObject hourly = (JSONObject) resultObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //Get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get humidity
            JSONArray realtiveHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) realtiveHumidity.get(index);

            //get wind speed
            JSONArray windspeedData = (JSONArray)  hourly.get("wind_speed_10m");
            double windspeed= (double) windspeedData.get(index);

            //build the weather JSON data object in order to use it in the frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;



        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Retrieve geographical coordinates for a given location
    public static JSONArray getLocationData(String locationName){
        //Replace whitespaces with +
        locationName = locationName.replace(" ", "+");

        //Build API url with location
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=10&language=en&format=json";

        try{
            //Call API
            HttpURLConnection connection = fetchApiResponse(urlString);

            //Check response status
            if(connection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else {
                //Store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                //Read and Store the resulting JSON data into StringBuilder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                //Close Scanner and url connection
                scanner.close();
                connection.disconnect();

                //Parse JSON String into JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //Get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultObj.get("results");
                return locationData;
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        //No location found
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
      try {
          //Attempt connection
          URL url = new URL(urlString);
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();

          //Set get method
          connection.setRequestMethod("GET");

          //Connect to API
          connection.connect();
          return connection;
      }catch(IOException e){
          e.printStackTrace();

         }
          return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //Iterate through the time list and see which one matches current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        //Format date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        //Format and print the current date and time

        return currentDateTime.format(formatter);


    }

    //Convert weather code to something understandable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";

        if (weathercode == 0L){
            weatherCondition = "Clear";

        } else if (weathercode <= 3L && weathercode > 0L){
            weatherCondition = "Cloudy";

        }else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)){
            weatherCondition="Rain";

        }else if (weathercode >= 71L && weathercode <= 77L){
            weatherCondition="Snow";
        }
        return weatherCondition;
    }
}
