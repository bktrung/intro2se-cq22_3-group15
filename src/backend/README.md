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
