#!/bin/bash

IPADDR=$(ip a s | sed -ne '/127.0.0.1/!{s/^[ \t]*inet[ \t]*\([0-9.]\+\)\/.*$/\1/p}')
echo $IPADDR

/usr/bin/java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr=$IPADDR -jar /opt/infinispan-embedded-tutorial/infinispan-embedded-tutorial.jar

