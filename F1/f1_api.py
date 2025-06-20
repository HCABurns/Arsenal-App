# Web imports
from flask import Flask, jsonify, request, render_template, flash

# Database imports
from firebase_admin import credentials,db,initialize_app
import dbHelper

# Extra imports.
import base64

# Database connector
while True:
    try:
        # Fetch the service account key JSON file contents
        cred = credentials.Certificate(dbHelper.file_location)

        # Initialize the app with a service account, granting admin privileges
        initialize_app(cred, {'databaseURL': dbHelper.url})

        # Get the database reference.
        ref = db.reference("f1")

        races = ref.get()
        
        #print(*races,sep="\n")
        print("Successfully Retrieved!")
        break
        
    except Exception as e:
        print(f"Error occured: {e}")
        break


app = Flask(__name__)
app.config['MAX_CONTENT_PATH'] = 16 * 1000000 #16MB limit (For MongoDB)
app.secret_key = dbHelper.get_secret_key()

### Will be usage information.
@app.route('/')
def landing_page():
    #Upload the images to the database.
    """
    for i in range(len(doggos)):
        with open(f"static/{doggos[i]['img']}", "rb") as imageFile:
            b64 = base64.b64encode(imageFile.read())
        #im.save(image_bytes, format='JPEG')
            
        client.dogs.dog.insert_one({"img":b64,"name":doggos[i]["name"],"breed":doggos[i]["breed"],"fact":doggos[i]["fact"]})

    for x in client.dogs.dog.find():
        image64 = x["img"].decode()
        #print(image64)
    """
    """
    #Get random record
    pipeline = [{'$sample': {'size': 1}}]

    #db.dogs.dog.aggregate([{ $sample: { size: 1 } }])
    for res in client.dogs.dog.aggregate(pipeline):
        dog_image = res["img"].decode()
        dog_name = res["name"]
        dog_breed = res["breed"]
        dog_Fact = res["fact"]
    """
    return render_template("index.html")#,doggo_name = dog_name,
     #                                   dog_image = dog_image,
     #                                   doggo_breed = dog_breed,
     #                                   doggo_fact = dog_Fact)


# Get api to get ALL the race/ quali information.
@app.route('/api', methods = ['GET'])
def get_races():
    """
    This function will return all the dogs in the list.
    """
    return races

# Get API to get race/quali information based on country
@app.route('/api/country/<string:country_name>', methods=['GET'])
def get_race(country_name):
    """
    Returns all race/quali information for the specified country.
    """
    results = []
    for key in races.keys():
        race_info = races[key]
        if country_name.lower() in race_info.get('country', '').lower():
            results.append(race_info)
    if not results:
        # Return 404 with JSON response
        return jsonify({'error': 'No races found for this country'}), 404
    return jsonify(results)


@app.errorhandler(404)
def page_not_found(e):
    # note that we set the 404 status explicitly
    return jsonify({'error': 'Unknown Request'}), 404
    #return render_template('error.html'), 404

