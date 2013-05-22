This application needs to commapi library installed:

export CVSROOT=:pserver:anonymous@qbang.org:/var/cvs/cvsroot
cvs login
CVS password: (blank)
cvs checkout -r commapi-0-0-1 rxtx-devel
cd rxtx-devel
./configure
sudo make install

CouchDB compact command should be run as a cron:

curl -H "Content-Type: application/json" -X POST http://localhost:5984/my_db/_compact

A properties file needs to be created to run this application:

Create: /etc/plos-message-led/message-led.properties

#The address of the serial port:
port = port-name
#The address of the couch server:
couchHost = http://host:port
#The name of the couch db
couchDB = database-name

To install the application:
mvn clean package

To get a list of ports on the current box:

java -cp target/message-led-1.1-jar-with-dependencies.jar org.plos.messageled.GetPorts

To execute the application:

java -cp target/message-led-1.1-jar-with-dependencies.jar org.plos.messageled.Main

or:

java -jar target/message-led-1.1-jar-with-dependencies.jar

