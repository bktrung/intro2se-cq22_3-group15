from google.oauth2 import service_account
from google.auth.transport.requests import Request

# Path to your Service Account Key JSON file
SERVICE_ACCOUNT_FILE = "testpushnotification-286ec-firebase-adminsdk-1eda2-acb7f28157.json"

# Create credentials from the service account JSON file
credentials = service_account.Credentials.from_service_account_file(
    SERVICE_ACCOUNT_FILE,
    scopes=["https://www.googleapis.com/auth/cloud-platform"]
)

# Refresh the credentials to get an access token
credentials.refresh(Request())
access_token = credentials.token
print(access_token)