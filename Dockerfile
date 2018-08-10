FROM maven
WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
# Adding source, compile and package into a fat jar
ADD src /code/src
ADD resources /code/resources
RUN apt-get update
RUN apt-get install --assume-yes gnuplot
RUN apt-get install git 
RUN mvn install:install-file -Dfile=resources/msseq.jar -DgroupId=com.unileipzig.filter -DartifactId=bloomfilter-check -Dversion=1.0 -Dpackaging=jar
RUN mvn clean package
#CMD ["kotlinc", "/code/src/main/kotlin/bloomfilter/Main.kt", "bloomfilter.jar"]
CMD ["java", "-jar", "/code/target/bloomfilter-1.0-SNAPSHOT-jar-with-dependencies.jar"]
