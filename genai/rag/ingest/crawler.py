import time

import requests
from bs4 import BeautifulSoup
import json


def get_urls(doc_path):
    url_prefix = "https://docs.github.com/en/actions/"
    urls = []
    for root, dirs, files in os.walk(doc_path):
        for file in files:
            if not file.endswith("index.md"):
                full_path = os.path.join(root, file)
                relative_path = os.path.relpath(full_path, doc_path)
                web_path = relative_path.replace("\\", "/").replace(".md", "")
                url = url_prefix + web_path
                urls.append(url)
    return urls


def fetch_and_clean_github_docs(url):
    headers = {
        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_4) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Safari/605.1.15",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "en-us",
        "Connection": "keep-alive",
        "Upgrade-Insecure-Requests": "1",
        "Cache-Control": "no-cache",

    }
    res = requests.get(url, headers=headers)
    res.raise_for_status()

    soup = BeautifulSoup(res.text, "html.parser")

    # GitHub Docs main content area
    content_div = soup.find("div", class_="MarkdownContent_markdownBody__v5MYy")
    if not content_div:
        raise ValueError("GitHub Docs No main content area found")

    def is_visible(tag):
        style = tag.get("style", "")
        return "display: none" not in style

    def process_element(elem):
        results = []
        if elem.name in ["h1", "h2", "h3", "h4", "h5", "h6"]:
            text = elem.get_text(strip=True)
            results.append({
                "type": "heading",
                "level": int(elem.name[1]),
                "text": text
            })
        elif elem.name == "p":
            text = elem.get_text(strip=True)
            if text:
                results.append({
                    "type": "paragraph",
                    "text": text
                })
        elif elem.name == "pre":
            code = elem.get_text()
            results.append({
                "type": "code",
                "language": "yaml",
                "content": code.strip()
            })
        elif elem.name in ["ul", "ol"]:
            items = [li.get_text(strip=True) for li in elem.find_all("li")]
            results.append({
                "type": "list",
                "ordered": elem.name == "ol",
                "items": items
            })
        elif elem.name == "div":
            for child in elem.find_all(recursive=False):
                if is_visible(child):
                    results.extend(process_element(child))
        return results

    content = []
    for elem in content_div.find_all(recursive=False):
        if is_visible(elem):
            content.extend(process_element(elem))

    return content


def export_crawled_content(content, output_path):
    with open(output_path, "w",
              encoding="utf-8") as f:
        json.dump(content, f, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    import os

    doc_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "raw",
                            "actions")

    urls = get_urls(doc_path=doc_path)

    print(len(urls))
    all_content = []
    counter = 0
    for url in urls:
        time.sleep(1)
        try:
            content = fetch_and_clean_github_docs(url)

            all_content.append({
                "url": url,
                "content": content
            })
            print(f"Fetched and cleaned content from {url}")
            # export_crawled_content(content, output_path)
            counter += 1
            print(counter)
        except Exception as e:
            print(f"Error fetching {url}: {e}")

    output_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "processed",
                               "actions_docs_raw.json")
    export_crawled_content(all_content, output_path)
