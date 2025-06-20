# Web imports
from flask import Flask, jsonify, request, render_template, flash

# Firebase imports
from firebase_admin import credentials, db, initialize_app

# Extra imports
import os
import json

def initialize_firebase():
    try:
        # Load Firebase JSON from Render secret file
        firebase_json_path = "/etc/secrets/firebase.json"
        with open(firebase_json_path, "r") as f:
            firebase_config = json.load(f)

        cred = credentials.Certificate(firebase_config)
        initialize_app(cred, {
            'databaseURL': os.environ["DB_URL"]
        })
        ref = db.reference("f1")
        print("Firebase initialized. Successfully retrieved data.")
        return ref.get()
    except Exception as e:
        print(f"Error occurred during Firebase initialization: {e}")
        return {}

races = initialize_firebase()

app = Flask(__name__)
app.config['MAX_CONTENT_PATH'] = 16 * 1000000  # 16MB limit
app.secret_key = os.environ["SECRET_KEY"]

@app.route('/')
def landing_page():
    return render_template("index.html")

@app.route('/api', methods=['GET'])
def get_races():
    return races

@app.route('/api/country/<string:country_name>', methods=['GET'])
def get_race(country_name):
    results = []
    for key in races.keys():
        race_info = races[key]
        if country_name.lower() in race_info.get('country', '').lower():
            results.append(race_info)
    if not results:
        return jsonify({'error': 'No races found for this country'}), 404
    return jsonify(results)

@app.errorhandler(404)
def page_not_found(e):
    return jsonify({'error': 'Unknown Request'}), 404

if __name__ == "__main__":
    app.run()
