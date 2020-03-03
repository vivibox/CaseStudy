import json
import requests
import configparser
import logging

def getWeather2(latitude, longitude):
    config = configparser.ConfigParser()
    config.read("config.ini")
    appid='Copy Your Appkey'
    openweatherapiurl = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid="+appid

    try:
        response = requests.get(openweatherapiurl)
        data = response.json()
        logging.info(data)
        weatherJson = {}
        weatherJson['description'] =  data['weather'][0]['description']
        weatherJson['Temp'] = str(int(data['main']['temp']-273.15))
        weatherJson['Humidity'] = str(data['main']['humidity'])
        answer = data['weather'][0]['description'] + ". Temp:" + str(int(data['main']['temp']-273.15))+ " (High "+str(int(data['main']['temp_max']-273.15))+", low " +str(int(data['main']['temp_min']-273.15))+". Humidity: "+str(data['main']['humidity'])+"\n"
        logging.info("[getWeather]:" + answer)
        return weatherJson    
    except:
        return None