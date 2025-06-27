# Import the required packages.
from firebase_admin import credentials,db,initialize_app
import dbHelper
import time
import base64
import requests

from bs4 import BeautifulSoup

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

import io
from PIL import Image

# Define URL to be scraped
url = "https://www.premierleague.com/clubs/12/manchester-united/fixtures?co=-1"

# Hashmap to convert months
month_converter = {"January":1,"February":2,"March":3,"April":4,"May":5, "June":6, "July":7,"August":8,
                   "September":9,"October":10,"November":11,"December":12}

valid = True
# Scrape the page for the relevant information.
while True:
    try:


        print("Start")
        # Load the webpage amd wait until the element is present.
        driver = webdriver.Chrome()
        driver.maximize_window()
        driver.get(url)
        # Scroll in loop until page height doesn't change
        last_height = driver.execute_script("return document.body.scrollHeight")

        while True:
            driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(2)  # Wait for new content to load
            new_height = driver.execute_script("return document.body.scrollHeight")
            
            if new_height == last_height:
                break
            last_height = new_height

        #elem = WebDriverWait(driver, 30).until(EC.presence_of_element_located((By.ID, "onetrust-accept-btn-handler")))

        print("Find elements")
        elems = driver.find_elements(By.CLASS_NAME, "fixtures__date-container")

        print("General Info" , len(elems))
        # Find the relevant information and store.
        games = []
        for game in elems:
            info = game.text.split("\n")

            date_info = info[0].split(" ")
            date = date_info[-1]+ "-" +str(month_converter[date_info[2]]) + "-" + date_info[1]
            time = info[2]
            opponent = [info[1],info[3]][info[3] != "Man Utd"] + " ("+ "HA"[info[0]!="Man Utd"] + ")"
            stadium = info[-2].split(",")[0]
            games.append([date,time,opponent,stadium])

        # Parse competition using BeautifulSoup
        soup = BeautifulSoup(driver.page_source, 'html.parser')
        images = soup.find_all('img', class_='fixtures__competition-logo')
        for i,img in enumerate(images[::2]):
            games[i].append(img.get('alt'))

        badge_url="https://resources.premierleague.com/premierleague/badges/"

        # Parse opponent badge in base64
        team_spans = soup.find_all('span', class_='match-fixture__teams')
        i = 0
        print(len(team_spans))
        for span in team_spans:
            images = span.find_all('img', class_='badge-image')
            for img in images:
                src = img.get('src')
                if src != "https://resources.premierleague.com/premierleague/badges/50/t1.png":
                    ext = src.split("/")[-1]
                    games[i].append(str(base64.b64encode(requests.get(badge_url+ext).content))[2:-1])
                    i+=1
                if i == len(games):break

        print("Successful Retreival!")
        break
    except:
        print("Error Occurred Getting Data")

while True:
    try:
        # Fetch the service account key JSON file contents
        cred = credentials.Certificate(dbHelper.file_location)

        # Initialize the app with a service account, granting admin privileges
        initialize_app(cred, {'databaseURL': dbHelper.url})


        # Get the database reference.
        ref = db.reference("games")

        # Titles.
        titles = "date, time, opponent, stadium, competition, badge_base64".split(", ")

        # List to JSON.
        json = {}
        for i in range(len(games)):
            json[i] = {titles[j]:games[i][j] for j in range(len(titles))}

        # Update the X games to the newest information.
        ref.update({"manchester_united":json})
        print("Successfully Updated!")
        break
    except:
        print("An Error Occured with updating the database!")

driver.quit()
