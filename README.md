# web-crawler-test

The implementation requires:
1. Java 8 (use of a lambda)
2. Maven
3. An internet connection (build and execution)

Compiling the code and running the tests
'mvn clean install'

Running the application
'mvn clean package'

Once completed, 'cd' into the target folder and run:
'java -jar webcrawler-1.0.0-SNAPSHOT-jar-with-dependencies.jar [Initial Website Url]'
for example
'java -jar webcrawler-1.0.0-SNAPSHOT-jar-with-dependencies.jar http://www.example.com/'

Application entry point class is service.Application

Things to note:

The application is multithreaded.  An ExecutorService with a thread pool of 10 threads is created (this is configurable).  
Each new request for a page to be crawled is processed as a separate thread by the executor service.

A ConcurrentHashMap is used to store the crawled URLs and the links that they contain.  This collection is thread safe and can 
be queried quickly to check to see if the page has already been crawled.

I used the JSoup library to parse the HTML page and retrieve the links / images.  The library is very simple to use and quite effective.
JSoup can also used to connect to the URL and download the content, however it will create a connection on each request, which is expensive.  
I used Apache HTTP client to make the HTTP call as it is easily configured with a connection pool for efficiency. 

The logging level is set to DEBUG, which will log extra messages to the console.  This can be changed in logback.xml for less logging.

Given more time on this exercise I would hae also used a framework to wire the ojects together.  Probably Spring.

One thing that I have assumed in this program is that there is no depth limit of links to crawl and the user is happy to wait until the full site has been crawled.  
A depth limit can be easily added by passing a parameter through to child processes to indicate the current depth.   

