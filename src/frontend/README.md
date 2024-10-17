# Set up for frontend

## Flow

1. **Clone the frontend branch**
   
    ```bash
    git clone -b frontend --single-branch https://github.com/bktrung/intro2se-cq22_3-group15

3. **Create new branch for specific task**
   
    ```bash
    git checkout -b frontend-feature/<feature-name>

5. **Commit Changes**:
   
   ```bash
   git add .
   git commit -m "Description of the changes"
   git push origin <your-branch-name>

7. **Admin Review and Merge**
- Create a Pull Request: Once your changes are pushed, create a pull request to merge your feature branch into the frontend branch.
- Admin Review: The admin will review the pull request.
- Merge: After approval, the admin will merge the changes into the frontend branch.
- Delete Remote Branch: The admin will delete the remote feature branch.

5. **Update Local Repository for new task**
- Pull latest change:
  
    ```bash
    git checkout frontend
    git pull origin frontend
    
- Delete Local Feature Branch:
  
    ```bash
    git fetch --prune
    
- Clean Up Remote-Tracking Branches
  
    ```bash
    git branch -d frontend-feature/<feature-name>

## Additional setup on local for running and testing

**Set up virtual environment**

- Open cmd/terminal in your ...\AppData\Local\Android\Sdk\platform-tools :
  
    ```bash
    adb reverse tcp:8000 tcp:8000


