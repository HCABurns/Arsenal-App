# Import the required packages.
from firebase_admin import credentials,db,initialize_app
import dbHelper

import base64
import requests

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

import io
from PIL import Image

# Define the URL to be searched.
url = "https://store.epicgames.com/en-US/free-games"

valid = True
# Scrape the page for the relevant information.
while True:
    try:
        print("Start")
        # Load the webpage amd wait until the element is present.
        driver = webdriver.Chrome()
        driver.maximize_window()
        driver.get(url)
        driver.implicitly_wait(50)
        #elem = WebDriverWait(driver, 30).until(EC.presence_of_element_located((By.ID, "onetrust-accept-btn-handler")))

        print("Find elements")
        elems = driver.find_elements(By.CLASS_NAME, "css-1myhtyb")
        
        print("General Info" , len(elems))
        # Find the relevant information and store.
        games = []
        for game in elems:
            gg = game.text.split("\n")
            if len(gg) == 2:
                gg = [" "] + gg
            games += [gg]

        chunks = []
        for i in range(0, len(games[0]), 3):
            chunks.append(games[0][i:i + 3])
        games = chunks
        print(games)
        
        print("Images")
        # Find all image tags
        elems = driver.find_element(By.ID, "app-main-content")
        #images = elems.find_elements(By.TAG_NAME, "img")

        # Extract the image URLs
        image_urls = []
        #images = driver.find_elements(By.CSS_SELECTOR, "div.css-n446gb img")
        images = driver.find_elements(By.CSS_SELECTOR, "div.css-1myhtyb img")
        for game in images:
            src = game.get_attribute("data-image") or game.get_attribute("src")
            if src:
                image_urls.append(src)
        print(image_urls)

        print("Image conversion")
        for i, url in enumerate(image_urls):
            print(url)

            # Download image
            response = requests.get(url)
            image = Image.open(io.BytesIO(response.content))

            # Re-encode as non-progressive JPEG
            buffer = io.BytesIO()
            image = image.convert("RGB")  # just in case it's PNG or has alpha
            image.save(buffer, format="JPEG", progressive=False)
            
            # Convert to base64 string
            b64_bytes = base64.b64encode(buffer.getvalue())
            b64_string = b64_bytes.decode("utf-8")

            games[i].append(b64_string)
        #for i,url in enumerate(image_urls[:len(games)]):
        #    print(url)
        #    games[i].append(str(base64.b64encode(requests.get(url).content))[2:-5])

        # Currently print the games together.
        # todo: Update the database with the information.
        # todo: Convert the URL to base64 with requests.
        for line in games:
            print(line[0:-1])
            print()
            
        print("\nSuccessful Retrieval!")
        break

    except Exception as e:
        print(f"Error occured: {e}")
        valid = False
        break

while valid:
    try:
        # Fetch the service account key JSON file contents
        cred = credentials.Certificate(dbHelper.file_location)

        # Initialize the app with a service account, granting admin privileges
        initialize_app(cred, {'databaseURL': dbHelper.url})

        # Get the database reference.
        ref = db.reference()

        # Titles.
        titles = "availability, name, time, cover_base64".split(", ")

        # List to JSON.
        json = {}
        for i in range(len(games)):
            print(games[i][1], len(games[i]))
            json[i] = {titles[j]:games[i][j] for j in range(len(titles))}

        # Update the X games to the newest information.
        ref.update({"epic_games":json})
        print("Successfully Updated!")
        break
        
    except Exception as e:
        print(f"Error occured: {e}")
        break

# Close the driver.
driver.quit()
