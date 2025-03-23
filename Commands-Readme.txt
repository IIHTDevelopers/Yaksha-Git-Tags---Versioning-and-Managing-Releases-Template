* to execute and run test cases

  mvn clean install exec:java -Dexec.mainClass="mainapp.MyApp" -DskipTests=true

git config --global user.email ""
git config --global user.name ""
---------------------------------------------------------------------------------------

echo "# TaskManager" > README.md
echo "def create_task():\n    return 'Task Created'" > task_manager.py
git add README.md task_manager.py
git commit -m "Initial commit: Added README and basic task manager script"
git tag -a v1.0 -m "v1.0: First stable release of TaskManager"

git show v1.0
echo "def list_tasks():\n    return ['Task 1', 'Task 2']" >> task_manager.py
git add task_manager.py
git commit -m "Added list_tasks feature"
git tag -a v1.1 -m "v1.1: Added list_tasks feature"
git show v1.1

sed -i 's/return "Task Created"/return "Task has been created"/' task_manager.py
git add task_manager.py
git commit -m "Fixed bug in create_task function"
git tag -a v1.1 -f -m "v1.1: Bug fix in create_task function"
git show v1.1

git checkout v1.0
git status
echo "Version 1.0: Initial release with task creation feature" >> README.md
git add README.md
git commit -m "Added version info for v1.0"
git log -1
git checkout main
git reflog
