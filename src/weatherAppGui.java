import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class weatherAppGui extends JFrame{

    private JSONObject weatherData;

    public weatherAppGui(){
        //Setup GUI and Title
        super("Weather App");

        //Configure GUI to close when the process has been terminated
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Set the size of the GUI
        setSize(450, 650);

        //Load GUI at the center of the screen
        setLocationRelativeTo(null);

        //Make layout manager null to manually position components within GUI
        setLayout(null);

        //Prevent resizing of GUI
        setResizable(false);

        addGuiComponents();

    }
    private void addGuiComponents(){
        //Search field
        JTextField searchTextField = new JTextField();

        //Set location and size
        searchTextField.setBounds(15, 15, 351, 45);

        //Change font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);



        //Weather Image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450,217);

        add(weatherConditionImage);


        //Temperature
        JLabel temperatureText = new JLabel("28 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //Center the text
        temperatureText.setHorizontalAlignment(SwingConstants.HORIZONTAL);

        add(temperatureText);


        //Weather Condition Description
        JLabel weatherConditionText = new JLabel("Cloudy");
        weatherConditionText.setBounds(0, 405, 450, 36);
        weatherConditionText.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionText.setHorizontalAlignment(SwingConstants.HORIZONTAL);

        add(weatherConditionText);


        //Humidity Image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74,66);
        add(humidityImage);


        //Humidity Text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);


        //Windspeed Image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);


        //Windspeed Text
        JLabel windSpeedText = new JLabel("<html><b>Wind Speed</b> 15 Km/h</html>");
        windSpeedText.setBounds(310, 500, 100, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        //Search button
        JButton searchButton = new JButton(loadImage("src/assets/search-i.png"));

        //Change to hand cursor for button
        searchButton. setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //Set location and size
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                // validate input - remove whitespace to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                System.out.println(weatherData);

                // update gui

                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // depending on the condition, we will update the weather image that corresponds with the condition
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionText.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }
    //Create images in GUI components
    private ImageIcon loadImage(String resourcePath){
        try{
            //Read Image from the path
            BufferedImage image =  ImageIO.read(new File(resourcePath));

            //Return image icon so that the component can render it
            return  new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not load resource");
        return null;
    }

}
