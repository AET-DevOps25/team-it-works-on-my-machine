import json
from uvicorn.importer import import_from_string

def main():
    app = import_from_string("genai.app.main:app")
    openapi = app.openapi()
    version = openapi.get("openapi", "unknown version")

    print(f"writing openapi spec v{version}")
    with open("openapi.json", "w") as f:
        json.dump(openapi, f, indent=2)

    print("spec written to openapi.json")
