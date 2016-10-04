FROM ubuntu

RUN apt-get update
RUN apt-get -y install nginx
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
RUN apt-get install -y oracle-java8-installer

RUN mkdir /tmp/images

COPY static /www/data/html
COPY nginx.conf /etc/nginx/nginx.conf
COPY target/uberjar/clojure-imgash-0.1.0-SNAPSHOT-standalone.jar /usr/local/bin/clojure-imgash.jar

CMD service nginx restart && java -jar /usr/local/bin/clojure-imgash.jar
