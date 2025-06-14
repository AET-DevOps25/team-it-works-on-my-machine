import requests

if __name__ == "__main__":
    url = "http://localhost:3001/analyze-yamls"

    # prepare the payload with two YAML files
    payload = {
        "yamls": [
            {
                "filename": "build.yml",
                "content": """
    name: Build
    on: push
    jobs:
      build:
        runs-on: ubuntu-latest
        steps:
          - uses: actions/checkout@v2
          - run: echo "Building..."
    """
            },
            {
                "filename": "test.yml",
                "content": """
    name: Test
    on: pull_request
    jobs:
      test:
        runs-on: ubuntu-latest
        steps:
          - uses: actions/setup-node@v2
          - run: npm install
          - run: npm test
    """
            }
        ]
    }

    response = requests.post(url, json=payload)

    if response.status_code == 200:
        print("✅ Successfully Generated:")
        print(response.json())
    else:
        print("❌ Something goes wrong:", response.status_code)
        print(response.text)
