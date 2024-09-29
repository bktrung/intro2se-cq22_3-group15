# Contributing to intro2se-cq22_3-group15

Thank you for considering contributing to our project! This document outlines the process for contributing to the repository.

## Table of Contents
1. Introduction
2. Branches
3. Workflow
4. Commit Messages
5. Pull Requests
6. Code Reviews
7. Coding Standards
8. Contact

## Introduction
This repository contains the source code and materials for the Introduction to Software Engineering class project. The project is a project management application built with Django for the backend and Kotlin for the frontend.

## Branches
The repository is organized into three main branches:
- **main**: The production-ready branch with the latest stable version of both frontend and backend.
- **frontend**: Contains the code for the frontend, built with Jetpack Compose and Kotlin.
- **backend**: Contains the code for the backend, built with Django and Django REST Framework.

### Branch Naming
- **Feature Branches**: `<backend/frontend>/feature/<feature-name>`
- **Bug Fix Branches**: `<backend/frontend>/bugfix/<bug-description>`
- **Hotfix Branches**: `<backend/frontend>/hotfix/<hotfix-description>`

## Workflow
1. **Clone the Repository**:
   ```bash
   git clone -b <frontend/backend> --single-branch https://github.com/bktrung/intro2se-cq22_3-group15
   cd intro2se-cq22_3-group15

2. **Create a New Branch**:
- For frontend work:
   ```bash
   git checkout -b frontend/feature/<feature-name>

- For backend work:
   ```bash
   git checkout -b backend/feature/<feature-name>

3. **Make Changes**:
- Ensure that changes in the frontend branch only affect the src/frontend directory.
- Ensure that changes in the backend branch only affect the src/backend directory.

4. **Commit Changes**:
   ```bash
   git add .
   git commit -m "Description of the changes"
   git push origin <your-branch-name>

## Commit Messages
- Use clear and descriptive commit messages.
- Follow the format: type(scope): description
- type: feat, fix, docs, style, refactor, test, chore
- scope: The part of the codebase affected (e.g., frontend, backend)
- description: A brief description of the changes

## Pull Requests
- Create a Pull Request:
- Ensure your branch is up-to-date with the target branch (frontend or backend).
- Open a pull request with a clear title and description of the changes.

## Review Process:
- Pull requests will be reviewed by at least one other team member.
- Address any feedback and make necessary changes.

## Merge:
- Once approved, the pull request can be merged into the target branch.

## Code Reviews
- Code reviews are essential for maintaining code quality.
- Be respectful and constructive in your feedback.
- Ensure that the code follows the project’s coding standards.

## Coding Standards
- Follow the coding standards and best practices for Django and Kotlin.
- Ensure that your code is well-documented and tested.

## Contact
- If you have any questions or need further assistance, please contact the project maintainers.

Thank you for contributing!

