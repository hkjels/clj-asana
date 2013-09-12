(ns clj-asana.core
  (:require [clj-http.client :as client]))

(def asana-url "https://app.asana.com/api")
(def api-version "1.0")
(def api-url (format "%s/%s" asana-url api-version))

(defn get-basic-auth
  "Get basic auth creds
  :returns: the basic auth string
  " 
  [])

(defn handle-exception
  " Handle exceptions

  :param r: request object
  :param api_target: API URI path for requests
  :param data: payload
  :returns: 1 if exception was 429 (rate limit exceeded), otherwise, -1
  "
  [])

(defn handle-rate-limit 
  " Sleep for length of retry time

  :param r: request object
  "
  [])

(defn -asana
  "Peform a GET request

  :param api_target: API URI path for request" 
  [api-target]
  (client/get (format "%s/%s" (api-url) api-target)))

(defn -asana-post
  "Peform a POST request

  :param api_target: API URI path for request
  :param data: POST payload" 
  [])

(defn -asana-put
  "Peform a PUT request

  :param api_target: API URI path for request
  :param data: PUT payload" 
  [])

(defn user-info
  "Obtain user info on yourself or other users.

  :param user_id: target user or self (default)
  "
  [user-id]
  (-asana (format "users/%s" user-id)))

(defn list-users
  "List users

  :param workspace: list users in given workspace
  :param filters: Optional [] of filters you want to apply to listing
  "
  [])

(defn list-tasks 
  "List tasks

  :param workspace: workspace id
  :param assignee: assignee
  "
  [workspace assignee]
  (-asana (format "tasks?workspace=%d&assignee=%s" workspace assignee)))

(defn get-task
  "Get a task

  :param task_id: id# of task"
  [task-id]
  (-asana (format "tasks/%d" task-id)))

(defn get-subtasks
  "Get subtasks associated with a given task

  :param task_id: id# of task"
  [task-id]
  (-asana (format "tasks/%d/subtasks" task-id)))

(defn list-projects
  "List projects in a workspace

  :param workspace: workspace whos projects you want to list"
  ([] (-asana "projects"))
  ([workspace] "workspaces/%d/projects" workspace))

(defn get-projects
  "Get project

  :param project_id: id# of project
  "
  [project-id]
  (-asana (format "projects/%d" project-id)))

(defn get-project-tasks
  "Get project tasks

  :param project_id: id# of project
  "
  [project-id]
  (-asana (format "projects/%d/tasks" project-id)))

(defn list-stories
  "List stories for task

  :param task_id: id# of task
  "
  [task-id]
  (-asana (format "tasks/%d/stories" task-id)))

(defn get-story
  "Get story

  :param story_id: id# of story
  "
  [story-id]
  (-asana (format "tasks/%d/stories" story-id)))

(defn list-workspaces
  """List workspaces"""
  []
  (-asana "workspaces"))

(defn create-task
  "Create a new task

  :param name: Name of task
  :param workspace: Workspace for task
  :param assignee: Optional assignee for task
  :param assignee_status: status
  :param completed: Whether this task is completed (defaults to False)
  :param due_on: Optional due date for task
  :param followers: Optional followers for task
  :param notes: Optional notes to add to task
  "
  [])

(defn update-task
  "Update an existing task

  :param task: task to update
  :param name: Update task name
  :param assignee: Update assignee
  :param assignee_status: Update status
  :param completed: Update whether the task is completed
  :param due_on: Update due date
  :param notes: Update notes
  "
  [])

(defn add-parent 
  "Set the parent for an existing task.

  :param task_id: id# of a task
  :param parent_id: id# of a parent task
  "
  [])

(defn create-subtask
  "Creates a task and sets it's parent.
  There is one noticeable distinction between
  creating task and assigning it a parent and
  creating a subtask. Latter doesn't get reflected
  in the project task list. Only in the parent task description.
  So using this method you can avoid polluting task list with subtasks.

  :param parent_id: id# of a task that subtask will be assigned to
  :param name: subtask name
  :param assignee: Optional user id# of subtask assignee
  :param notes: Optional subtask description
  :param followers: Optional followers for subtask"
  [])

(defn create-project
  "Create a new project

  :param name: Name of project
  :param workspace: Workspace for task
  :param notes: Optional notes to add
  :param archived: Whether or not project is archived (defaults to False)
  "
  [])

(defn update-project
  "Update project

  :param project_id: id# of project
  :param name: Update name
  :param notes: Update notes
  :param archived: Update archive status
  "
  [])

(defn update-workspace
  "Update workspace

  :param workspace_id: id# of workspace
  :param name: Update name
  "
  [workspace-id new-name]
  (-asana-put (format "workspaces/%s" workspace-id) {"name" new-name}))

(defn add-project-task
  "Add project task

  :param task_id: id# of task
  :param project_id: id# of project
  "
  [task-id project-id]
  (-asana-post (format "tasks/%d/addProject" task-id) {"project" project-id})) 

(defn rm-project-task
  "Remove a project from task

  :param task_id: id# of task
  :param project_id: id# of project
  "
  [task-id project-id]
  (-asana-post (format "tasks/%d/removeProject" task-id) {"project" project-id}))

(defn add-story
  "Add a story to task

  :param task_id: id# of task
  :param text: story contents
  "
  [task-id text]
  (-asana-post (format "tasks/%d/stories" task-id)))

(defn add-tag-task
  "Tag a task

  :param task_id: id# of task
  :param tag_id: id# of tag to add
  "
  [task-id tag-id]
  (-asana-post (format "tasks/%d/addTag" task-id) {"tag" tag-id}))

(defn get-tags
  "Get available tags for workspace

  :param workspace: id# of workspace
  "
  [workspace]
  (-asana (format "workspaces/%s/tags" workspace)))

(defn get-tag-tasks
  "Get tasks for a tag

  :param tag_id: id# of task
  "
  [tag-id] 
  (-asana (format "tags/%d/tasks" tag-id)))

(defn create-tag
  "Create tag

  :param tag_name: name of the tag to be created
  :param workspace: id# of workspace in which tag is to be created
  "
  [tag workspace]
  (-asana-post "tags" {"name" tag, "workspace", workspace}))

(def basic-auth (get-basic-auth))
