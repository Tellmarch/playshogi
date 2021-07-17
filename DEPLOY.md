Short guide on how to deploy the website on a clean install of ubuntu 20.04:
Only follow this guide if you understand the commands - take special care with the iptables commands in the last section

# Install MySQL

    sudo apt update  
    sudo apt-get upgrade  
    sudo apt-get install mysql-server 
    sudo mysql_secure_installation

### Optional: Allow root to connect by password (if not, adjust the mysql commands)

Replace "password" by your sql root password

    sudo mysql  
    
    SELECT user,authentication_string,plugin,host FROM mysql.user;  
    ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'password';  
    FLUSH PRIVILEGES;

# Setup database

1. Copy the mysql/schema.sql file to the server, and import the schema with:

        mysql -u root -p < schema.sql 

2. update user auth (If you change the password, change it in DbConnection.java too)

        ALTER USER 'playshogi'@'%' IDENTIFIED WITH mysql_native_password BY 'playshogiDB1';

3. (Optional, if you have a data dump) Import data

        scp ../dumps/Dump20200726-data.sql  ...:  
        sudo apt install pv  
        pv Dump20200726-data.sql | mysql -u root -p playshogi  

# Install Tomcat

    sudo apt-get install tomcat9  
    sudo apt-get install tomcat9-admin  
    sudo nano /etc/tomcat9/tomcat-users.xml  

Add: !!! CHANGE THE USERNAME AND PASSWORD

    <role rolename="manager-gui"/>  
    <user username="tomcatplayshogi" password="<PASSWORD HERE>" roles="manager-gui"/>

# Install JDBC connector

    sudo apt-get install libmariadb-java
    sudo dpkg -L libmariadb-java

    sudo cp  /usr/share/java/mariadb-java-client-2.5.3.jar /var/lib/tomcat9/lib
    sudo systemctl restart tomcat9.service 

# Deploy website

1. Open http://host:8080/manager/html
2. Select WAR file to upload -> Choose ROOT.WAR, built with mvn package.
3. Deploy

# Optional: Listen on port 80 (by default tomcat listens to 8080)

1. sudo su
2. Find out interface: ip addr show , example enp0s20
3. Add iptables rules

        iptables -A PREROUTING -t nat -i enp0s20f0 -p tcp --dport 80 -j REDIRECT --to-port 8080  
        iptables -A PREROUTING -t nat -i enp0s20f0 -p tcp --dport 443 -j REDIRECT --to-port 8443  

4. Make it permanent

        apt-get install iptables-persistent

5. Check

        iptables -L -n -t nat
        iptables -L -n
        less /etc/iptables/rules.v4
