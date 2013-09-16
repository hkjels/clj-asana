(ns clj-asana.core
  "Clojure Wrapper for Asana API"
  (:require [clj-http.client :as client]))

(def asana-url "https://app.asana.com/api")

(def api-version "1.0")

(def api-url (format "%s/%s" asana-url api-version))

(def api-key "2btZETdu.ELTTmHhqAzw5XCtWmHhnlH0")

(defn- asana-post
  "Peform a POST request

  :param api-target: API URI path for request
  :param data: POST payload"
  [api-target data]
  (let [response (client/post (format "%s/%s" api-url api-target)
                              {:basic-auth [api-key ""]
                               :as :json
                               :content-type :json
                               :form-params {"data" data}
                               :throw-entire-message? true})]
    (if (= 201 (:status response))
      (:body response))))

(defn- asana-put
  "Peform a PUT request

  :param api-target: API URI path for request
  :param data: PUT payload"
  [api-target data]
  (let [response (client/put (format "%s/%s" api-url api-target)
                             {:basic-auth [api-key ""]
                              :as :json
                              :content-type :json
                              :form-params {"data" data}
                              :throw-entire-message? true})]
    (if (= 201 (:status response))
      (:body response))))

(defn- asana-delete
  "Peform a DELETE request

  :param api-target: API URI path for request
  :param data: PUT payload"
  [api-target]
  (let [response (client/delete (format "%s/%s" api-url api-target)
                             {:basic-auth [api-key ""]
                              :as :json
                              :content-type :json
                              :throw-entire-message? true})]
    (if (= 200 (:status response))
      (:body response))))

(defn- asana
  "Peform a GET request

  :param api-target: API URI path for request"
  [api-target]
  (let [response (client/get (format "%s/%s" api-url api-target)
                             {:basic-auth [api-key ""]
                              :as :json
                              :content-type :json
                              :throw-entire-message? true})]
    (if (= 200 (:status response))
      (:body response))))

(defn show-user-info
  "Obtain user info on yourself or other users.

  :param user-id: target user or self (default)
  "
  [& {:keys [user-id]
      :or {user-id "me"}}]
  (asana (format "users/%s" user-id)))

(defn list-users
  "List users

  :param workspace: list users in given workspace
  :param filters: Optional [] of filters you want to apply to listing
  "
  [& {:keys [workspace filters]
      :or {workspace nil
           filters nil}}]

  (if workspace
    (asana (format "workspaces/%s/users" workspace))
    (if filters
      (asana (format "users?opt_fields=%s" (apply str (interpose "," (map (fn [x] (clojure.string/lower-case (clojure.string/trim x))) filters)))))
      (asana "users"))))

(defn create-task
  "Create a new task

  :param name: Name of task
  :param workspace: Workspace for task
  :param assignee: Optional assignee for task
  :param assignee-status: status
  :param completed: Whether this task is completed (defaults to False)
  :param due-on: Optional due date for task
  :param followers: Optional followers for task
  :param notes: Optional notes to add to task
  "
  [new-name workspace & {:keys [assignee assignee-status due-on followers notes]
                         :or {assignee nil
                              assignee-status nil
                              due-on nil
                              followers nil
                              notes nil}}]
  (asana-post "tasks"
              (conj {"name" new-name
                    "workspace" workspace}
                    (if assignee {"assignee" assignee})
                    (if assignee-status {"assignee_status" assignee-status})
                    (if due-on { "due_on" due-on})
                    (if notes { "notes" notes})
                    (if followers (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))))

(defn show-task
  "Shows a task

  :param task-id: id# of task"
  [task-id]
  (asana (format "tasks/%s" task-id)))

(defn update-task
  "Update an existing task

  :param task: task to update
  :param name: Update task name
  :param assignee: Update assignee
  :param assignee-status: Update status
  :param completed: Update whether the task is completed
  :param due-on: Update due date
  :param notes: Update notes
  "
  [task & {:keys [new-name assignee assignee-status completed due-on notes]
           :or {new-name nil
                assignee nil
                assignee-status nil
                completed false
                due-on nil
                notes nil}}]
  (asana-put (format "tasks/%s" task)
             (conj (if new-name {"name" new-name})
                   (if assignee {"assignee" assignee})
                   (if assignee-status {"assignee_status" assignee-status})
                   (if due-on {"due_on" due-on})
                   (if notes {"notes" notes})
                   (if completed {"completed" completed})
                   (if due-on {"due_on" due-on}))))

(defn rm-task
  "Delete an existing task
  
  :param task-id: id# of task"
  [task-id]
  (asana-delete (format "tasks/%s" task-id)))

(defn list-tasks
  "List tasks

  :param workspace: workspace id
  :param assignee: assignee
  "
  [workspace assignee]
  (asana (format "tasks?workspace=%s&assignee=%s" workspace assignee)))

(defn list-subtasks
  "Get subtasks associated with a given task

  :param task-id: id# of task"
  [task-id]
  (asana (format "tasks/%s/subtasks" task-id)))

(defn create-subtask
  "Creates a task and sets it's parent.
  There is one noticeable distinction between
  creating task and assigning it a parent and
  creating a subtask. Latter doesn't get reflected
  in the project task list. Only in the parent task description.
  So using this method you can avoid polluting task list with subtasks.

  :param parent-id: id# of a task that subtask will be assigned to
  :param name: subtask name
  :param assignee: Optional user id# of subtask assignee
  :param notes: Optional subtask description
  :param followers: Optional followers for subtask"
  [parent-id new-name & {:keys [completed assignee notes followers]
                         :or {completed false
                              assignee "me"
                              notes nil
                              followers nil}}]
  (asana-post (format "tasks/%s/subtasks" parent-id)
              (conj {"name" new-name
                     "assignee" assignee
                     "completed" completed}
                    (if notes { "notes" notes})
                    (if followers (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))))

(defn set-parent
  "Set the parent for an existing task.

  :param task-id: id# of a task
  :param parent-id: id# of a parent task
  "
  [task-id parent-id]
  (asana-post (format "tasks/%s/setParent" task-id) {"parent" parent-id}))

(defn list-task-stories
  "List stories for task

  :param task-id: id# of task
  "
  [task-id]
  (asana (format "tasks/%s/stories" task-id)))

(defn add-task-comment
  "Adds a comment to an object
  
  :param text: Comment to be posted
  "
  [task-id text]
  (asana-post (format "tasks/%s/stories" task-id) {"text" text}))

(defn list-task-projects
  "Lists all projects associated with a task
  
  :param task-id: id# of tas
  "
  [task-id]
  (asana (format "tasks/%s/projects" task-id)))

(defn add-task-project
  "Add project to a task

  :param task-id: id# of task
  :param project-id: id# of project
  "
  [task-id project-id]
  (asana-post (format "tasks/%s/addProject" task-id) {"project" project-id}))

(defn rm-task-project
  "Remove a project from task

  :param task-id: id# of task
  :param project-id: id# of project
  "
  [task-id project-id]
  (asana-post (format "tasks/%s/removeProject" task-id) {"project" project-id}))

(defn list-task-tags
  "List tags that are associated with a task.

  :param task-id: id# of task
  "
  [task-id]
  (asana (format "tasks/%s/tags" task-id)))

(defn add-task-tag
  "Tag a task

  :param task-id: id# of task
  :param tag-id: id# of tag to add
  "
  [task-id tag-id]
  (asana-post (format "tasks/%s/addTag" task-id) {"tag" tag-id}))

(defn rm-task-tag
  "Remove a tag from a task.

  :param task-id: id# of task
  :param tag-id: id# of tag to remove
  "
  [task-id tag-id]
  (asana-post (format "tasks/%s/removeTag" task-id) {"tag" tag-id}))

(defn add-task-followers
  "add followers to a task
  
  :param task-id: id# of task
  :param followers []: id#'s of followers
  "
  [task-id followers]
  (asana-post (format "tasks/%s/addFollowers") (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))

(defn rm-task-followers
  "Remove followers from a task
  
  :param task-id: id# of task
  :param followers []: id#'s of followers
  "
  [task-id followers]
  (asana-post (format "tasks/%s/removeFollowers") (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))

(defn create-project
  "Create a new project

  :param name: Name of project
  :param workspace: Workspace for task
  :param notes: Optional notes to add
  :param archived: Whether or not project is archived (defaults to False)
  "
  [new-name workspace & {:keys [notes archived]
                         :or {notes nil
                              archived nil}}]
  (asana-post "projects" (conj {"name" new-name
                                "workspace" workspace}
                               (if notes {"notes" notes})
                               (if archived {"archived" archived}))))

(defn show-project 
  "Show a single project

  :param project-id: id# of project
  "
  [project-id]
  (asana (format "projects/%s" project-id)))

(defn update-project
  "Update project

  :param project-id: id# of project
  :param name: Update name
  :param notes: Update notes
  :param archived: Update archive status
  "
  [project-id & {:keys [new-name notes archived]
                 :or {new-name nil
                      notes nil
                      archived false}}]
  (asana-put (format "projects/%s" project-id)
             (conj (if new-name {"name" new-name}) (if notes {"notes" notes}) (if archived {"archived" archived}))))

(defn rm-project
  "Delete a project

  :param project-id: id# of project"
  [project-id]
  (asana-delete (format "projects/%s" project-id)))

(defn list-project-tasks
  "List non-archived tasks in this project
  
  :param project-id: id# of project"
  [project-id]
  (asana (format "project/%s/tasks" project-id)))

(defn list-projects
  "List projects in a workspace

  :param workspace: workspace whos projects you want to list"
  ([] (asana "projects"))
  ([workspace] (asana (format "workspaces/%s/projects" workspace))))

(defn create-workspace-tag
  "Create a tag for a workspace

  :param tag-name: name of the tag to be created
  :param workspace: id# of workspace in which tag is to be created
  "
  [tag workspace]
  (asana-post "tags" {"name" tag, "workspace", workspace}))

(defn show-tag
  "Shows info about a tag
  :param tag-id: id# of tag
  "
  [tag-id]
  (asana (format "tags/%s" tag-id)))

(defn list-tags 
  "Shows available tags for workspace

  :param workspace: id# of workspace
  "
  ([] (asana "tags"))
  ([workspace] (asana (format "workspaces/%s/tags" workspace))))


(defn show-project-tasks
  "Get project tasks

  :param project-id: id# of project
  "
  [project-id]
  (asana (format "projects/%s/tasks" project-id)))

(defn show-workspace-tasks
  "Get workspace tasks

  :param workspace:id# of workspace
  "
  [workspace]
  (asana (format "workspace/%s/tasks" workspace)))

(defn list-tag-tasks
  "Get tasks for a tag

  :param tag-id: id# of task
  "
  [tag-id]
  (asana (format "tags/%s/tasks" tag-id)))

;;; PROJECTS

;;; TAGS

;;; STORIES

(defn show-story
  "Get story

  :param story-id: id# of story
  "
  [story-id]
  (asana (format "tasks/%s/stories" story-id)))

;;; WORKSPACES

(defn list-workspaces
  "List workspaces"
  []
  (asana "workspaces"))

(defn update-workspace
  "Update workspace

  :param workspace-id: id# of workspace
  :param name: Update name
  "
  [workspace-id new-name]
  (asana-put (format "workspaces/%s" workspace-id) {"name" new-name}))

;;; TEAMS

(defn show-my-teams
  "Show all teams you're a member of in an organization
  
  :param organization-id: id# of organization
  "
  [organization-id]
  (asana (format "organizations/%s/teams" organization-id)))

;;; ATTACHMENTS

(defn show-attachment
  "Showing a single attachment

  :param attachment-id: id# of attachment
  "
  [attachement-id]
  (asana (format "attachments/%s" attachment-id))

(defn list-task-attachements
  "SHOWING ALL ATTACHMENTS ON A TASK

  :param task-id: id# of task
  "
  [task-id]
  (asana (format "tasks/%s/attachments" task-id)))
