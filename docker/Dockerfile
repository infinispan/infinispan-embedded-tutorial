FROM jboss/base-jdk:8

USER root
RUN yum install -y iproute
ADD launch.sh /usr/bin/launch.sh
RUN chmod +x /usr/bin/launch.sh
RUN mkdir -p /opt/infinispan-embedded-tutorial/
RUN chown jboss:jboss /opt/infinispan-embedded-tutorial/

USER jboss
ADD infinispan-embedded-tutorial-1.0.0-SNAPSHOT-jar-with-dependencies.jar /opt/infinispan-embedded-tutorial/infinispan-embedded-tutorial.jar

ENTRYPOINT /usr/bin/launch.sh

