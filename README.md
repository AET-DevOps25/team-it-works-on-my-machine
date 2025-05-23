# Run Instructions:

### 1. Setup GenAI .env file at `genai/.env`
```bash
OPENAI_API_KEY=your-api-key
OPENAI_API_BASE=https://api.deepseek.com/v1  # Optional, if using DeepSeek or a proxy
```

### 2. Setup GH-Connector properties file

The file must be placed at `gh-connector/src/main/java/resources/application-secret.properties`.
Use this guide to generate these values: https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app

```bash
oauth.client-id=<your-oauth-client-id>
oauth.client-secret=<your-oauth-client-secret>
```


### 3. Run everything via docker

`docker compose build && docker compose up`

Alternativaly install eveything manually according to the instructions in the subprojects

1. GH-Connector README: [README](https://github.com/AET-DevOps25/team-it-works-on-my-machine/blob/main/gh-connector/README.md)
2. GenAI README: [README](https://github.com/AET-DevOps25/team-it-works-on-my-machine/blob/main/genai/README.md)
3. Client README: [README](https://github.com/AET-DevOps25/team-it-works-on-my-machine/blob/main/client/README.md)

