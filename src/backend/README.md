# Set up for backend

## Flow

1. **Clone the backend branch**
   
    ```bash
    git clone -b backend --single-branch https://github.com/bktrung/intro2se-cq22_3-group15

3. **Create new branch for specific task**
   
    ```bash
    git checkout -b backend-feature/<feature-name>

5. **Commit Changes**:
   
   ```bash
   git add .
   git commit -m "Description of the changes"
   git push origin <your-branch-name>

7. **Admin Review and Merge**
- Create a Pull Request: Once your changes are pushed, create a pull request to merge your feature branch into the backend branch.
- Admin Review: The admin will review the pull request.
- Merge: After approval, the admin will merge the changes into the backend branch.
- Delete Remote Branch: The admin will delete the remote feature branch.

5. **Update Local Repository for new task**
- Pull latest change:
  
    ```bash
    git checkout backend
    git pull origin backend
    
- Delete Local Feature Branch:
  
    ```bash
    git fetch --prune
    
- Clean Up Remote-Tracking Branches
  
    ```bash
    git branch -d backend-feature/<feature-name>

## Additional setup on local for running and testing

1. **Set up virtual environment**
   
   ```bash
   python -m venv venv
   venv\Scripts\activate
   pip install -r requirements.txt

2. **Active virtual environment**

   ```bash
   venv\Script\activate

- In case it did not work, check your interpreter by Ctr + Shift + P then choose select interpreter.
- Choose your venv (recommend) instead of default (global).

3. **Recommend using Postman**
- Testing CRUD
- Testing Google login

## Docker

1. **Make sure you have Docker installed on your machine.**

2. **cd into the backend folder and run the following command:**

   ```bash
    docker compose up --build

- If encounter this error: "Error response from daemon: driver failed programming external connectivity on endpoint redis-db (05130a1f5d33c5c31644d42bf654df467a321bdddd418a9d30a0a1a7b406147b): failed to bind port 0.0.0.0:6379/tcp: Error starting userland proxy: listen tcp4 0.0.0.0:6379: bind: address already in use", run the following command (only work for Linux):
    ```bash
    sudo systemctl stop redis
