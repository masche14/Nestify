import base64
import requests
import json
import os
import sys

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

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {api_key}"
}

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
                        "  '카테고리': '가구',\n"
                        "  '제품명': '침대',\n"
                        "  '색상': '흰색, 베이지',\n"
                        "  '특징': '간결한 디자인의 침대, 부드러운 패턴이 있는 이불'\n"
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

response = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=payload)

print(response.json())

content = response.json()['choices'][0]['message']['content']

json_start = content.find('```json') + len('```json\n')
json_end = content.rfind('```')
json_str = content[json_start:json_end].strip()

# 추출한 JSON 문자열을 실제 파이썬 객체로 변환
json_data = json.loads(json_str)

# 결과를 JSON 형식으로 출력
print(json.dumps(json_data))