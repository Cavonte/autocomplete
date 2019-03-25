# Suggestions

## General
Spring Boot Application which return suggestion based on the request.

- The application resides on an EC2 instance at the following Dns/Ip:

http://ec2-18-216-194-112.us-east-2.compute.amazonaws.com:8080

- Alternatively a jar file can be created using Maven and ran with this command.

java -jar demo-0.0.1-20190319.045954-1.jar --server.port=8080 (Example)

## 

**/suggestions**

e.g. http://ec2-18-216-194-112.us-east-2.compute.amazonaws.com:8080/suggestions?q=lond&longitude=-71.04949&latitude=42.33343&limit=30

Parameters
- q   (required)  //query e.g. londo, montre, montreal
- latitude (optional) // 33.4487 (Close to Vestavia Hills), 45.5017 (Close to Montreal)
- latitude (optional) // -86.7666 (Close to Vestavia Hills), -73.5555 (Close to Montreal)
- limit (optional) //How many results are sent back. Default is 20

**/suggestionsByCountry**
This call requires the coordinate of the request to pinpoint the country and filter the suggestion accordingly.

e.g. http://ec2-18-216-194-112.us-east-2.compute.amazonaws.com:8080/suggestionsByCountry?q=lond&longitude=-71.04949&latitude=42.33343&limit=5

Parameters
- q   (required)  //query e.g. londo, montre, montreal
- latitude (required) // 33.4487 (Close to Vestavia Hills), 45.5017 (Close to Montreal
- latitude (required) // -86.7666 (Close to Vestavia Hills), -73.5555 (Close to Montreal)
- limit (optional) //How many results are sent back. Default is 20


## Main Flow
1) Request input is parsed and sanitized.
2) Data manager parses the tsv files and list all the cities.
3) Cities are filtered using a word similarity lib and common string operations.
3) Cities are filtered a second time and sorted based on the location provided and depending on the endpoint.
4) Json array with the result is prepared and returned.

## Filters and Sorting
- The results are filtered based on how close the name of the city and the query of the request. (The tolerance can be adjusted)
- 2 comparator are used to sort the results.
  1)Word Similarity
  2)Distance between the coordinate (if provided) and the city.
 

## Neat features
- Changes can be made to the filters with peace of mind because the functional tests will catch any major issues.
- The word similarity library integration allows for better suggestion since it takes typos into account. The same type of
  algorithms are used in autocorrct engines.
- If the coordinates are specified, the closest city can be approximated and subsequently the country. This can then be used to 
  calculate the distances.
- The classes are modular and can work outside of this context.

## Mandatory Credits
- TSV Parser Library - https://github.com/univocity/univocity-parsers
- Efficient File Reader - https://github.com/uniVocity/univocity-parsers/blob/master/src/test/java/com/univocity/parsers/examples/Example.java
- Distance Between 2 set of Coordinates - http://www.codecodex.com/wiki/Calculate_Distance_Between_Two_Points_on_a_Globe#Java
- Word Similarity Library - https://github.com/tdebatty/java-string-similarity
- Number Utils - Apache Commons Lang3
- Built-in JSON Library of Spring
