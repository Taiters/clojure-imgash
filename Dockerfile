FROM ubuntu

RUN sudo apt-get update
RUN sudo apt-get -y install nginx
RUN sudo apt-get install -y software-properties-common
RUN sudo add-apt-repository ppa:webupd8team/java
RUN sudo apt-get update
RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
RUN sudo apt-get install -y oracle-java8-installer

RUN mkdir /tmp/images

COPY static /www/data/html
COPY nginx.conf /etc/nginx/nginx.conf
COPY target/uberjar/clojure-imgash-0.1.0-SNAPSHOT-standalone.jar /usr/local/bin/clojure-imgash.jar

CMD sudo service nginx restart && java -jar /usr/local/bin/clojure-imgash.jar
