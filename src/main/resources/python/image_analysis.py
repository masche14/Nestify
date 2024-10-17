import base64
import requests
import json
import os
import sys
import time

# OpenAI API Key
api_key = os.environ['OpenAi_Key']

# Function to encode the image
def encode_image(image_path):
    with open(image_path, "rb") as image_file:
        return base64.b64encode(image_file.read()).decode('utf-8')

# Path to your image
image_path = sys.argv[1]

# Getting the base64 string
base64_image = encode_image(image_path)

# Set headers for the OpenAI API
headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {api_key}"
}

# Prepare the payload to send to the OpenAI API
payload = {
    "model": "gpt-4o-mini",
    "messages": [
        {
            "role": "user",
            "content": [
                {
                    "type": "text",
                    "text": (
                        "이 이미지의 인테리어 요소들을 분석하고, 각 요소를 JSON 형식으로 정리해 주세요. "
                        "아래 형식을 따르고, JSON 형식으로 반환해 주세요:\n"
                        "{\n"
                        "  'category': '가구',\n"
                        "  'productName': '침대',\n"
                        "  'color': '흰색, 베이지',\n"
                        "  'features': '간결한 디자인의 침대, 부드러운 패턴이 있는 이불'\n"
                        "}"
                    )
                },
                {
                    "type": "image_url",
                    "image_url": {
                        "url": f"data:image/jpeg;base64,{base64_image}"
                    }
                }
            ]
        }
    ],
    "max_tokens": 300
}

for i in range(3):
    # Send the request to the OpenAI API
    response = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=payload)

    if response.status_code != 200:
        continue

    else:
        # Set response encoding to utf-8
        response.encoding = 'utf-8'
        break

# Extract the content from the response
content = response.json()['choices'][0]['message']['content']

# Find the start and end of the JSON string within the content
json_start = content.find('```json') + len('```json\n')
json_end = content.rfind('```')

if json_end == 0:
    json_str = content[json_start:].strip()
else:
    json_str = content[json_start:json_end].strip()

print(json_str)
