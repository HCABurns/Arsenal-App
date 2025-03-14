# Required Imports
import re
import requests
import base64
from bs4 import BeautifulSoup as bs

from firebase_admin import credentials,db,initialize_app

# Define URL to be scraped
#url = "https://www.google.com/search?q=arsenal+fixture"
url = "https://www.arsenal.com/fixtures"


# Get data html from the page.
data = requests.get(url).text

# Create a soup object to search using div and class tags.
soup = bs(data, 'html.parser')

# Get the information in the div and classes required:
#div = soup.find_all('div', class_='card fixture-card fixture-card--full-card fixture-card--is-link')
#div = soup.find_all('div', class_='accordions')
div = soup.find('div', class_='accordion__content')
#div = soup.find_all('div', class_='card fixture-card fixture-card--full-card fixture-card--is-link')


#######################
# Upcoming Match Info #
#######################

img_url = "https://www.arsenal.com"

time = re.search(r'<time datetime="[\d\S]*"', str(div))

t = time[0].split('"')[1]
date, time = t.split("T")

print("Date:",date)
print("Time:",time[:-1])

location = re.search(r'<div class="event-info__venue">[(\d\w )]*', str(div))
location = location[0].split(">")[1]
print("Stadium:",location)

competition = re.search(r'<div class="event-info__extra">[(\d\w )]*', str(div))
competition = competition[0].split(">")[1]
print("Competition:",competition)

opponent = re.findall(r'<div class="team-crest__name-value">[(\d\w )]*', str(div))
t1 = opponent[0].split(">")[1]
t2 = opponent[1].split(">")[1]
opponent = [t1,t2][t1=="Arsenal"]
print("Opponent:",opponent)


badges = re.findall(r'class="team-crest__crest" src="[\S]*.\/>', str(div))

arsenal_badge = [badges[0],badges[1]][t2=="Arsenal"].split('"')[-2]
print("Arsenal badge url: ",img_url+arsenal_badge)

opponent_badge = [badges[0],badges[1]][t1=="Arsenal"].split('"')[-2]
print("Opponent badge url: ",img_url+opponent_badge)

arsenal_base64 = base64.b64encode(requests.get(img_url+arsenal_badge).content)
opponent_base64 = base64.b64encode(requests.get(img_url+opponent_badge).content)

########################################
# Get next games that are to be played #
########################################

div = soup.find_all('div', class_='accordions')

opponent = re.findall(r'<div class="team-crest__name-value">[(\d\w )]*', str(div))

location = re.findall(r'<div class="event-info__venue">[\w.\-\' ]*', str(div))
for i in location:
    loc = i.split(">")[1]
    print("Stadium:",loc)

#competition = re.findall(r'<div class="event-info__extra">[(\d\w )]*', str(div))
#competition = competition[0].split(">")[1]
#print("Competition:",competition)



