import re
import requests
from firebase_admin import credentials,db,initialize_app
import dbHelper

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

#url = "https://www.formula1.com/en/racing/2025"
url = "https://store.epicgames.com/en-US/free-games"


        
driver = webdriver.Chrome()
driver.maximize_window()

driver.get(url)

driver.implicitly_wait(50)

# Wait until the element is present
elem = WebDriverWait(driver, 30).until(EC.presence_of_element_located((By.ID, "onetrust-accept-btn-handler")))

# Locate the element and get its text
#f = driver.find_element(By.ID, "app-main-content")

f = driver.find_elements(By.CLASS_NAME, "css-n446gb")  

games = []
for game in f:
    games += [game.text.split("\n")]


# Find all image tags
f = driver.find_element(By.ID, "app-main-content")
images = f.find_elements(By.TAG_NAME, "img")


# Extract the image URLs
image_urls = []
for img in images[2:6]:
    src = img.get_attribute("data-image") or img.get_attribute("src")
    if src:
        image_urls.append(src)

# Print or use the URLs
for i,url in enumerate(image_urls):
    games[i].append(url)

for line in games:
    print(line)
    print()

print("\nSuccessful Retrieval!")


driver.quit()
