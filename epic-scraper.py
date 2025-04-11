# Import the required packages.
from firebase_admin import credentials,db,initialize_app
import dbHelper

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

# Define the URL to be searched.
url = "https://store.epicgames.com/en-US/free-games"

# Scrape the page for the relevant information.
while True:
    try:
        # Load the webpage amd wait until the element is present.
        driver = webdriver.Chrome()
        driver.maximize_window()
        driver.get(url)
        driver.implicitly_wait(50)
        elem = WebDriverWait(driver, 30).until(EC.presence_of_element_located((By.ID, "onetrust-accept-btn-handler")))

        # Locate the element and get its text
        elems = driver.find_elements(By.CLASS_NAME, "css-n446gb")

    
        # Find the relevant information and store.
        games = []
        for game in elems:
            games += [game.text.split("\n")]


        # Find all image tags
        elems = driver.find_element(By.ID, "app-main-content")
        images = elems.find_elements(By.TAG_NAME, "img")


        # Extract the image URLs
        image_urls = []
        images = driver.find_elements(By.CSS_SELECTOR, "div.css-n446gb img")
        for game in images:
            src = game.get_attribute("data-image") or game.get_attribute("src")
            if src:
                image_urls.append(src)
        for i,url in enumerate(image_urls[:len(games)]):
            games[i].append(url)

        # Currently print the games together.
        # todo: Update the database with the information.
        # todo: Convert the URL to base64 with requests.
        for line in games:
            print(line)
            print()
            
        print("\nSuccessful Retrieval!")
        break

    except Exception as e:
        print(f"Error occured: {e}")
        break

# Close the driver.
driver.quit()
