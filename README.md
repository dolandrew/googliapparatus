# Googli Apparatus Backend

www.googliapparatus.com

The "Googli Apparatus" is a Phish lyrics search engine (based on the song Golgi Apparatus) 

To use the Googli Apparatus, type any word or phrase in the search bar. Results will be shown of songs with song names or lyrics containing that word or phrase

![Screen Shot 2022-07-28 at 4 18 00 PM copy](https://user-images.githubusercontent.com/28452598/181653142-1dabc69b-7fde-4701-add5-74335f2edd3d.jpg)


Dark Mode: To switch to Dark Mode, click the Moon Icon in the top left corner. 

![Screen Shot 2022-07-28 at 4 18 14 PM](https://user-images.githubusercontent.com/28452598/181653209-4b6029cf-bee3-4298-841f-0c6d8278e0f3.jpg)


## To build and run with Gradle:

```
./gradlew build
./gradlew bootRun
```

## To build and run with Docker:

```
docker build . -t googliapparatus
docker run googliapparatus
```

Note: Postgres database is required

This app is deployed to Heroku at http://googliapparatus.herokuapp.com/
