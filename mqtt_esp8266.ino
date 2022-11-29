#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <Adafruit_AHTX0.h>
#include <Wire.h>  // Library for I2C communication
#include "RTClib.h"

char* token = "";
Adafruit_AHTX0 aht;
RTC_DS1307 RTC;

// declare message broker
const char* ssid = "Thien Huong 2.4Ghz";
const char* password = "nha@2111d";
const char* mqtt_server = "192.168.1.189";

// declare variable for soil sensor
const int sensor_pin = A0;

// declare variable for timer
int isTimer = 0;
int hour = 0;
int minute = 0;
int second = 0;

int tohour = 0;
int tominute = 0;
int tosecond = 0;

// declare variable for water pumb
int isWatering = 0;
const int motor1A = D8;
const int motor1B = D7;

// delcare ope
WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;
long lastMsg2 = 0;
char msg[50];
int value = 0;


// Connect Soil moisture analog sensor pin to A0
float getMoisture() {
  float moisture_percentage;
  moisture_percentage = (100.00 - ((analogRead(sensor_pin) / 1023.00) * 100.00));
  return moisture_percentage;
}

// conver char* to String
String char2String(byte* payload, unsigned int length) {
  return String((char*)payload).substring(0, length);
}

/*
* function setup wifi / connecting to a WiFi network
*/
void setup_wifi() {
  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  randomSeed(micros());
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

/*
* function reconnecting to message broker
*/
void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");

      // Once connected, publish an announcement...
      client.publish("sensor", "sensor data");

      // ... and resubscribe
      client.subscribe("controlRecive");
      client.subscribe("ope");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  StaticJsonDocument<1024> doc;
  StaticJsonDocument<256> motorPumb;
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  DeserializationError error = deserializeJson(doc, payload);
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return;
  }
  Serial.println();

  if (String(topic) == "controlRecive") {
    motorPumb = doc["303"];
    isWatering = motorPumb["isWatering"];
    isTimer = motorPumb["isTimer"];

    hour = motorPumb["hour"];
    minute = motorPumb["minute"];
    second = motorPumb["second"];

    tohour = motorPumb["tohour"];
    tominute = motorPumb["tominute"];
    tosecond = motorPumb["tosecond"];

    Serial.println(String(isWatering));
    if (isWatering == 1) {
      digitalWrite(motor1A, LOW);
      digitalWrite(motor1B, HIGH);
      digitalWrite(BUILTIN_LED, LOW);  // Turn the LED on (Note that LOW is the voltage level
      publishEvent("watering","switch on watering");
    }

    if (isWatering == 0) {
      digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off by making the voltage HIGH
      digitalWrite(motor1A, LOW);
      digitalWrite(motor1B, LOW);
      publishEvent("watering","switch off watering");
    }
  }

  if (String(topic) == "ope") {
    if (doc["cmd"] == "sync") {
      publishControlData();
    }
  }

  // Switch
}

void publishEvent(String type, String content) {
  DynamicJsonDocument event(256);
  DateTime now = RTC.now();
  String time = "";
  String date = "";

  if (now.hour() < 10) {
    time += "0" + String(now.hour());
  } else {
    time += String(now.hour());
  }
  time += ":";
  if (now.minute() < 10) {
    time += "0" + String(now.minute());
  } else {
    time += String(now.minute());
  }
  time += ":";
  if (now.second() < 10) {
    time += "0" + String(now.second());
  } else {
    time += String(now.second());
  }

  date += String(now.year());
  date += "-";
  if (now.month() < 10) {
    date += "0" + String(now.month());
  } else {
    date += String(now.month());
  }
  date += "-";
  if (now.day() < 10) {
    date += "0" + String(now.day());
  } else {
    date += String(now.day());
  }

  event["date"] = date;
  event["time"] = time;
  event["type"] = type;
  event["content"] = content;

  char out[256];
  serializeJson(event, out);
  client.publish("event", out);
}

void publishSensorData() {
  DynamicJsonDocument sensordata(1020);

  DateTime now = RTC.now();
  sensors_event_t humidity, temp;
  aht.getEvent(&humidity, &temp);

  String time = "";
  String date = "";

  if (now.hour() < 10) {
    time += "0" + String(now.hour());
  } else {
    time += String(now.hour());
  }
  time += ":";
  if (now.minute() < 10) {
    time += "0" + String(now.minute());
  } else {
    time += String(now.minute());
  }
  time += ":";
  if (now.second() < 10) {
    time += "0" + String(now.second());
  } else {
    time += String(now.second());
  }

  date += String(now.year());
  date += "-";
  if (now.month() < 10) {
    date += "0" + String(now.month());
  } else {
    date += String(now.month());
  }
  date += "-";
  if (now.day() < 10) {
    date += "0" + String(now.day());
  } else {
    date += String(now.day());
  }

  sensordata["time"] = time;
  sensordata["date"] = date;

  StaticJsonDocument<200> soil;
  soil["value"] = getMoisture();
  soil["type"] = "AM";
  sensordata["305"] = soil;

  StaticJsonDocument<200> airHumidity;
  airHumidity["value"] = humidity.relative_humidity;
  airHumidity["type"] = "AH";
  sensordata["302"] = airHumidity;

  StaticJsonDocument<200> temperature;
  temperature["value"] = temp.temperature;
  temperature["type"] = "TP";
  sensordata["304"] = temperature;

  char out[1020];
  serializeJson(sensordata, out);
  client.publish("sensor", out);
}

void publishControlData() {
  StaticJsonDocument<1024> controldata;

  StaticJsonDocument<256> motorPumb;
  motorPumb["isWatering"] = isWatering;
  motorPumb["isTimer"] = isTimer;
  motorPumb["hour"] = hour;
  motorPumb["minute"] = minute;
  motorPumb["second"] = second;
  motorPumb["tohour"] = tohour;
  motorPumb["tominute"] = tominute;
  motorPumb["tosecond"] = tosecond;
  controldata["303"] = motorPumb;

  char out[1024];
  serializeJson(controldata, out);

  client.publish("controlSend", out);
}

void setup() {
  pinMode(BUILTIN_LED, OUTPUT);  // Initialize the BUILTIN_LED pin as an output
  pinMode(motor1A, OUTPUT);
  pinMode(motor1B, OUTPUT);
  digitalWrite(BUILTIN_LED, HIGH);

  aht.begin();
  Wire.begin();                              // Start the I2C
  RTC.begin();                               // Init RTC
  RTC.adjust(DateTime(__DATE__, __TIME__));  // Time and date is expanded to date and time on your computer at compiletime
  Serial.print('Time and date set');

  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {

  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  long now = millis();
  if (now - lastMsg > 3000) {
    lastMsg = now;
    publishSensorData();
    publishControlData();
  }
  if (now - lastMsg2 > 1000) {
    lastMsg2 = now;
    if (isTimer == 1) {
      DateTime now2 = RTC.now();
      if (now2.hour() == hour && now2.minute() == minute && now2.second() == second) {
        digitalWrite(BUILTIN_LED, LOW);
        isWatering = 1;
        publishEvent("watering","on time watering");
      }
      if (now2.hour() == tohour && now2.minute() == tominute && now2.second() == tosecond) {
        digitalWrite(BUILTIN_LED, HIGH);
        isWatering = 0;
        publishEvent("watering","off time watering");
      }
    }
  }
}