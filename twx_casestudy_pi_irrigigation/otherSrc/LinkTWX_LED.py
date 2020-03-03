#socket 服務端和客戶端 服務端監聽 客戶端的請求 連結確認 
import socket 
import threading 
import time
import RPi.GPIO as GPIO
#from Weather_api import getWeather2

# set LED init 
LED = 15

def blink(speed):   
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(LED,GPIO.OUT,initial=GPIO.LOW)
    GPIO.output(LED,1)
    time.sleep(speed)
    GPIO.output(LED,0)
    time.sleep(speed)
    

controlMsg = {}
controlMsg['switchDevicePowerOn'] = False
controlMsg['waterPressure'] = 1000
controlMsg['powerLevel'] = 5
controlMsg['SwitchAutoShutdownOn'] = False
controlMsg['DataChange'] = 0


#接收信息 
def DealIn(sock): 
    global inString 
    print("DealIn :: Start")
    
    while True:  
        try: 
            print("DealIn::loading")
            inString = sock.recv(1024) 
            print("DealIn::recv success")
            if(inString == b'') :
                print("inString== False !!!!!!!!")
                break    
                                
            msgIn = inString.strip().decode("utf-8");
            msgIn = msgIn.split("#")
            if( msgIn[0] == "SwitchDevicePowerOn" ):
                controlMsg['switchDevicePowerOn'] = True
                dataChange = 0
                print("DealIn::SwitchDevicePowerOn:True:="+ str(controlMsg['switchDevicePowerOn']))
                
            elif( msgIn[0] == "setDevicePowerOff" ):
                controlMsg['switchDevicePowerOn'] = False
                dataChange = "dont work"
                print("DealIn::switchDevicePowerOn:False:="+ str(controlMsg['switchDevicePowerOn']))
                
            elif( msgIn[0] == "switchAutoShutdownOnfalse"):
                controlMsg['SwitchAutoShutdownOn'] = False
                print("DealIn::SwitchAutoShutdownOn:False:="+ str(controlMsg['SwitchAutoShutdownOn']))
                
            elif( msgIn[0] == "SetPoint"):
                print("setPoint="+msgIn[1])
                try:
                    num = float(msgIn[1])
                    for i in range(int(num)):
                        blink(2)
                finally:
                    GPIO.cleanup()
                    print('GPIO clean-up is done.')
                
            else:
                print(b'DealIn::form server receive:' + inString)
        except Exception as e: 
            print("recv fail --------------")
            traceback.print_exc()

#發送信息的函數 
def DealOut(sock): 
    print("DealOut :: Start")
    global nick,outString #聲明為全局變量，進行賦值,這樣才可以生效 
#    global controlMsg 
    while True:
        print("1")
        if(controlMsg['switchDevicePowerOn'] == True):
            print("2")
            # getWeather
    #         weatherMsg = getWeather2('25.05', '121.65')
            weatherMsg ={'description': 'few clouds', 'Temp': '0', 'Humidity': '0'}
    #         print(weatherMsg)
            print("3")

            if(controlMsg['DataChange'] == 0):
                controlMsg['waterPressure'] = 500
                print("4")
            else:
                if(controlMsg['DataChange'] == 1):
                    if(controlMsg['switchDevicePowerOn'] == False):
                        controlMsg['switchDevicePowerOn'] = True
                        print("5")
                    else:
                        controlMsg['switchDevicePowerOn'] = False

                elif(controlMsg['DataChange'] == 2):
                    controlMsg['powerLevel'] = 5

                elif(controlMsg['DataChange'] == 3):
                    if(controlMsg['SwitchAutoShutdownOn'] == False):
                        controlMsg['switchDevicePowerOn'] = True
                    else:
                        controlMsg['switchDevicePowerOn'] = False

                elif(controlMsg['DataChange'] == "e"):
                    break
            # be send msg             
            msg={'weather':weatherMsg,'controller':controlMsg}  
            strMsg = str(msg)
            strMsg = strMsg.strip('()')
            outString = str(strMsg)

            sock.send(bytes(outString, encoding="utf8"))#發送
            print("should sending already")
            time.sleep(5)

nick = 'pyDevice1\n'#名字 
# ip = 'localhost'#ip地址 
client = socket.socket(socket.AF_INET,socket.SOCK_STREAM)#創建套接字,默認為ipv4 
client.connect(('192.168.137.1', 2345))
print("connect success!!!!")


client.send(bytes(nick, encoding="utf8"))
print("send success!!!!")
controlMsg['switchDevicePowerOn'] = True
# client.recv(1024)
# print("recv success!!!!")

# print("main::"+inString)


#讀取
thin = threading.Thread(target=DealIn,args=(client,))#調用threading 創建一個接收信息的線程' 
thin.start() 

# 寫出
thout = threading.Thread(target=DealOut,args=(client,))# 創建一個發送信息的線程，聲明是一個元組
thout.start()
