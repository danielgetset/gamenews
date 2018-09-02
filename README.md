# gamenews

##### Heroku site: https://gamenews-kotlin.herokuapp.com/ 

A simple RSS feed with gaming news data from IGDB 
built with Javalin and Kotlin deployed on Heroku

https://www.igdb.com \
https://javalin.io \
https://kotlinlang.org \
https://www.heroku.com

Instructions: 

Add a file called 'settings' to folder src/main/resources/private with following content
~~~~json
{
    "apiKey" : "your-api-key-from-api.igdb.com",
    "userName" : "your-username-from-api.igdb.com"
} 
~~~~
