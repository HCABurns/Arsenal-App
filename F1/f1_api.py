# Web imports
from flask import Flask, jsonify, request, render_template, flash

# Firebase imports
from firebase_admin import credentials, db, initialize_app

# Extra imports
import os
import json
import base64

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
        races = ref.get()
        races_with_ids = []
        for id, race in enumerate(races):
            race_with_id = race
            race_with_id["id"] = id
            races_with_ids.append(race_with_id)
        return races_with_ids
    except Exception as e:
        print(f"Error occurred during Firebase initialization: {e}")
        return {}

def verify_firebase_token():
    auth_header = request.headers.get('Authorization', '')
    if not auth_header.startswith('Bearer '):
        return None, jsonify({'error': 'Unauthorized: Missing token'}), 401

    id_token = auth_header.split('Bearer ')[1]
    try:
        decoded_token = auth.verify_id_token(id_token)
        return decoded_token['uid'], None, None
    except Exception as e:
        print(f"Token verification failed: {e}")
        return None, jsonify({'error': 'Unauthorized: Invalid token'}), 401
    

races = initialize_firebase()
app = Flask(__name__)
app.config['MAX_CONTENT_PATH'] = 16 * 1000000  # 16MB limit
app.secret_key = os.environ["SECRET_KEY"]


@app.route('/')
def landing_page():
    # Alter to be a usage for the api.
    return render_template("index.html")


@app.route('/api', methods=['GET'])
def get_races():
    uid, error_response, status = verify_firebase_token()
    if not uid:
        return error_response, status
    
    races_with_ids = []
    for id, race in enumerate(races):
        race_with_id = race
        race_with_id["id"] = id
        races_with_ids.append(race_with_id)
    return {"races":races_with_ids,"count":len(races)}, 200


@app.route('/api/<string:country_name>', methods=['GET'])
def get_race(country_name):
    results = []
    for race_info in races:
        if country_name.lower() == race_info.get('country', '').lower():
            results.append(race_info)
    if not results:
        return jsonify({'error': 'No races found for this country'}), 404
    return {"races":results,"count":len(results)}, 200


@app.errorhandler(404)
def page_not_found(e):
    if request.path.startswith('/api'):
        return jsonify({'error': 'Unknown Request'}), 404
    return jsonify({'error but different': 'Unknown Request'}), 404


if __name__ == "__main__":
    app.run()
