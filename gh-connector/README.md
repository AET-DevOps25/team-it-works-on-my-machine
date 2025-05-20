Steps to run this:
1: create `src/main/java/resources/application-secret.properties` file and fill with secret key
```
oauth.client-secret=your-client-secret
oauth.client-id=your-client-id
```
2. run with `./gradlew build bootRun`

Build the docker image

```bash
docker build -t gh-connector-image gh-connector
```

Run the docker image

```bash
docker run -p 8589:8589 gh-connector-image
```