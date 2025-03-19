# Required Imports
import re
import requests
from bs4 import BeautifulSoup as bs
import base64
from firebase_admin import credentials,db,initialize_app
import dbHelper 

# Define URL to be scraped
url = "https://www.arsenal.com/fixtures"
img_url = "https://www.arsenal.com"

while True:
    try:
        # Get data html from the page.
        data = requests.get(url).text

        # Create a soup object to search using div and class tags.
        soup = bs(data, 'html.parser')

        # Get the information in the div and classes required:
        div = soup.find('div', class_='accordion__content')


        ##########################################
        # Get next X games that are to be played #
        ##########################################

        # Competition, Opponent Name, Date, Time, Stadium, opponent badge base64
        games_amount = 4
        games = [[] for i in range(games_amount)]

        # Get the information from the web page.
        data = str(soup.find_all('div', class_='accordions'))

        opponent = re.findall(r'<div class="team-crest__name-value">[(\d\w )]*', data)
        #times = re.findall(r'<time datetime="[\d\S]*"', data)<time datetime="[\d\S ]+<?
        times = re.findall(r'<time datetime="[\d\S ]+<?', data)
        location = re.findall(r'<div class="event-info__venue">[\w.\-\' ]*', data)
        badges = re.findall(r'class="team-crest__crest" src="[\S]*.\/>', data)
        competition = re.findall(r'<div class="event-info__extra">[(\d\w )]*', data)

        # Extract the information for each game and store.
        for i in range(games_amount):

            # Add competition
            games[i].append(competition[i].split(">")[1])

            # Add opponent and whether Arsenal are home or away.
            t1 = opponent[i*2].split(">")[1]
            t2 = opponent[i*2+1].split(">")[1]
            opp = [t1,t2][t1=="Arsenal"]
            home = [True,False][t1=="Arsenal"]
            games[i].append(opp + [" (H)", " (A)"][home])

            # Add date of the match.
            date, time = times[i*2].split('"')[1].split("T")
            games[i].append(date)

            # Add time of the match.
            time = times[i*2].split("-")[-1].split("<")[0].strip()+":00"
            games[i].append(time)
            #games[i].append(time[:-1])

            # Add stadium to the array.
            games[i].append(location[i].split(">")[1])

            # Get base64 of the opponent badge.
            games[i].append(str(base64.b64encode(requests.get(img_url+[badges[i*2],badges[i*2+1]][t1=="Arsenal"].split('"')[-2]).content))[2:-2])

        # Exit and update the database.
        break
    except:
        print("An Error Occured: Web Scraping Failed!")
    
while True:
    try:
        # Fetch the service account key JSON file contents
        cred = credentials.Certificate('key.json')

        # Initialize the app with a service account, granting admin privileges
        initialize_app(cred, {'databaseURL': dbHelper.url})


        # Get the database reference.
        ref = db.reference()

        # Titles.
        titles = "competition, opponent, date, time, stadium, badge_base64".split(", ")

        # List to JSON.
        json = {}
        for i in range(games_amount):
            json[i] = {titles[j]:games[i][j] for j in range(len(titles))}

        # Update the X games to the newest information.
        ref.update({"games":json})
        print("Successfully Updated!")
        break
    except:
        print("An Error Occured with updating the database!")
