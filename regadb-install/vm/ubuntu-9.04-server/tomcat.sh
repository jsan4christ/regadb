#!/bin/bash

apt-get -y install tomcat6
sed -i 's/#TOMCAT_SECURITY=yes/TOMCAT_SECURITY=no/' /var/default/tomcat6