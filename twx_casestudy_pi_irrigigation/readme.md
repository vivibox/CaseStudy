# Casestudy : irrigigation with device(pi)
file:twx_casestudy_pi_irrigigation

## Prerequisite
1. Thingworx VM
2. JRE 1.8
3. Python3
4. Raspberry Pi

## Start

#### Thingworx
1. Import TwxWebLayout.xml
* otherSrc/TwxWebLayout.xml
2. Create application keys in TWX and copy application keys

#### JAVA
3. Change Uri and Appkey
* src/com.thingworx.main/IrrigigationRouter.java
 config.setUri("Paste Your Uri");
 config.setAppKey("Paste Your Appkey");
4. Main (Start Java) 
* package: com.thingworx.main
* IrrigigationRouter.java
```bash
javac src/com.thingworx.main/IrrigigationRouter.java
java IrrigigationRouter
```
#### Python3
5. If you don't hava raspberry pi , you can comment out below code (57-65)
* otherSrc/LinkTWX_LED.py
```
elif( msgIn[0] == "SetPoint"):
                print("setPoint="+msgIn[1])
                try:
                    num = float(msgIn[1])
                    for i in range(int(num)):
                        blink(2)
                finally:
                    GPIO.cleanup()
                    print('GPIO clean-up is done.')
```

6. Copy LinkTWX_LED.py to your device and start
* otherSrc/LinkTWX_LED.py
```
cd yourProjectFile/otherSrc/LinkTWX_LED.py
python3 LinkTWX_LED.py

```

### Other
7. You can use Weather_API.py  
* otherSrc/Weather_API.py
	* go to https://openweathermap.org/ sign up
	* get OpenWeatherMap API key
	* in line 6 ``` appid='Copy Your Appkey' ``` pasted your appid
* otherSrc/LinkTWX_LED.py
	* remove markdown ``` from Weather_api import getWeather2 ```
	* restar  ``` python3 LinkTWX_LED.py ```



## file description
1. Main (start Java) 
* package: com.thingworx.main
* IrrigigationRouter.java

2. Server thread (connect to device)
   When device connect to server(Router) ,it will new a thread to receive and sending data.
* package:com.thing.device_server

3. Client thread (connect to TWX)
* package:com.thingworx.sdk.simplething

4. Property/Router entity setting
* package: com.thingworx.thing


