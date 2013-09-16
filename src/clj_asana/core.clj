(ns clj-asana.core
  "Clojure Wrapper for Asana API"
  (:require [clj-http.client :as client]))

(def asana-url "https://app.asana.com/api")

(def api-version "1.0")

(def api-url (format "%s/%s" asana-url api-version))

(def api-key "2btZETdu.ELTTmHhqAzw5XCtWmHhnlH0")

;;; Private functions

(defn- asana-post
  "Peforms a POST request

  Args:
  api-target: API URI path for request
  data: POST payload"
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
  "Peforms a PUT request

  Args:
  api-target: API URI path for request
  data: PUT payload"
  [api-target data]
  (let [response (client/put (format "%s/%s" api-url api-target)
                             {:basic-auth [api-key ""]
                              :as :json
                              :content-type :json
                              :form-params {"data" data}
                              :throw-entire-message? true})]
    (if (= 200 (:status response))
      (:body response))))

(defn- asana-delete
  "Peforms a DELETE request

  Args:
  api-target: API URI path for request
  data: PUT payload"
  [api-target]
  (let [response (client/delete (format "%s/%s" api-url api-target)
                                {:basic-auth [api-key ""]
                                 :as :json
                                 :content-type :json
                                 :throw-entire-message? true})]
    (if (= 200 (:status response))
      (:body response))))

(defn- asana
  "Peforms a GET request

  Args:
  api-target: API URI path for request"
  [api-target]
  (let [response (client/get (format "%s/%s" api-url api-target)
                             {:basic-auth [api-key ""]
                              :as :json
                              :content-type :json
                              :throw-entire-message? true})]
    (if (= 200 (:status response))
      (:body response))))

;;; Users

(defn show-user-info
  "Obtains user info on yourself or other users.

  Args:
  user-id: target user or self (default)
  "
  [& [user-id]]
  (asana (format "users/%s" (if user-id user-id "me"))))

(defn list-users
  "Lists users (based on workspaces) and filter the results

  Args:
  workspace: list users in given workspace
  filters: Optional [] of filters you want to apply to listing
  "
  [& {:keys [workspace filters]
      :or {workspace nil
           filters nil}}]
  (asana (format "%s%s"
                 (if workspace (format "workspaces/%s/users" workspace) "users")
                 (if filters (format "?opt_fields=%s" (apply str (interpose "," (map (fn [x] (clojure.string/lower-case (clojure.string/trim x))) filters)))) ""))))

;;; Tasks

(defn create-task
  "Creates a new task

  Args:
  name: Name of task
  workspace: Workspace for task
  assignee: Optional assignee for task
  assignee-status: status
  due-on: Optional due date for task
  followers: Optional followers for task
  notes: Optional notes to add to task
  "
  [new-name workspace & {:keys [assignee assignee-status due-on followers notes]
                         :or {assignee nil
                              assignee-status "upcoming"
                              due-on nil
                              followers nil
                              notes nil}}]
  (asana-post "tasks"
              (conj {"name" new-name
                     "workspace" workspace
                     "assignee_status" assignee-status}
                    (if assignee {"assignee" assignee})
                    (if due-on { "due_on" due-on})
                    (if notes { "notes" notes})
                    (if followers (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))))

(defn show-task
  "Shows all information about a task

  Args:
  task-id: id# of task"
  [task-id]
  (asana (format "tasks/%s" task-id)))

(defn update-task
  "Updates an existing task

  Args:
  task: task to update
  name: Update task name
  assignee: Update assignee
  assignee-status: Update status
  completed: Update whether the task is completed
  due-on: Update due date
  notes: Update notes
  "
  [task & {:keys [new-name assignee assignee-status completed due-on notes]
           :or {new-name nil
                assignee nil
                assignee-status "upcoming"
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
  "Deletes an existing task

  Args:
  task-id: id# of task"
  [task-id]
  (asana-delete (format "tasks/%s" task-id)))

(defn list-tasks
  "Lists tasks

  Args:
  workspace: workspace id
  assignee: assignee
  "
  [workspace assignee]
  (asana (format "tasks?workspace=%s&assignee=%s" workspace assignee)))

(defn list-subtasks
  "Gets subtasks associated with a given task

  Args:
  task-id: id# of task"
  [task-id]
  (asana (format "tasks/%s/subtasks" task-id)))

(defn create-subtask
  "Creates a task and sets it's parent.
  There is one noticeable distinction between
  creating task and assigning it a parent and
  creating a subtask. Latter doesn't get reflected
  in the project task list. Only in the parent task description.
  So using this method you can avoid polluting task list with subtasks.

  Args:
  parent-id: id# of a task that subtask will be assigned to
  name: subtask name
  assignee: Optional user id# of subtask assignee
  notes: Optional subtask description
  followers: Optional followers for subtask"
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
  "Sets the parent for an existing task.

  Args:
  task-id: id# of a task
  parent-id: id# of a parent task
  "
  [task-id parent-id]
  (asana-post (format "tasks/%s/setParent" task-id) {"parent" parent-id}))

(defn list-task-projects
  "Lists all projects associated with a task

  Args:
  task-id: id# of tas
  "
  [task-id]
  (asana (format "tasks/%s/projects" task-id)))

(defn add-task-project
  "Adds project to a task

  Args:
  task-id: id# of task
  project-id: id# of project
  "
  [task-id project-id]
  (asana-post (format "tasks/%s/addProject" task-id) {"project" project-id}))

(defn rm-task-project
  "Removes a project from task

  Args:
  task-id: id# of task
  project-id: id# of project
  "
  [task-id project-id]
  (asana-post (format "tasks/%s/removeProject" task-id) {"project" project-id}))

(defn list-task-tags
  "Lists tags that are associated with a task.

  Args:
  task-id: id# of task
  "
  [task-id]
  (asana (format "tasks/%s/tags" task-id)))

(defn add-task-tag
  "Tags a task

  Args:
  task-id: id# of task
  tag-id: id# of tag to add
  "
  [task-id tag-id]
  (asana-post (format "tasks/%s/addTag" task-id) {"tag" tag-id}))

(defn rm-task-tag
  "Removes a tag from a task.

  Args:
  task-id: id# of task
  tag-id: id# of tag to remove
  "
  [task-id tag-id]
  (asana-post (format "tasks/%s/removeTag" task-id) {"tag" tag-id}))

(defn add-task-followers
  "Adds followers to a task

  Args:
  task-id: id# of task
  followers []: id#'s of followers
  "
  [task-id followers]
  (asana-post (format "tasks/%s/addFollowers") (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))

(defn rm-task-followers
  "Removes followers from a task

  Args:
  task-id: id# of task
  followers []: id#'s of followers
  "
  [task-id followers]
  (asana-post (format "tasks/%s/removeFollowers") (into {} (map-indexed (fn [index value] [(format "followers[%d]" index) value]) followers))))

;;; Projects

(defn create-project
  "Creates a new project

  Args:
  name: Name of project
  workspace: Workspace for task
  notes: Optional notes to add
  archived: Whether or not project is archived (defaults to False)
  "
  [new-name workspace & {:keys [notes archived]
                         :or {notes nil
                              archived nil}}]
  (asana-post "projects" (conj {"name" new-name
                                "workspace" workspace}
                               (if notes {"notes" notes})
                               (if archived {"archived" archived}))))

(defn show-project
  "Shows a single project

  Args:
  project-id: id# of project
  "
  [project-id]
  (asana (format "projects/%s" project-id)))

(defn update-project
  "Updates a project

  Args:
  project-id: id# of project
  name: Update name
  notes: Update notes
  archived: Update archive status
  "
  [project-id & {:keys [new-name notes archived]
                 :or {new-name nil
                      notes nil
                      archived false}}]
  (asana-put (format "projects/%s" project-id)
             (conj (if new-name {"name" new-name}) (if notes {"notes" notes}) (if archived {"archived" archived}))))

(defn rm-project
  "Deletes a project

  Args:
  project-id: id# of project"
  [project-id]
  (asana-delete (format "projects/%s" project-id)))

(defn list-project-tasks
  "Lists non-archived tasks in this project

  Args:
  project-id: id# of project"
  [project-id]
  (asana (format "projects/%s/tasks" project-id)))

(defn list-projects
  "Lists projects in a workspace

  Args:
  workspace: workspace whos projects you want to list"
  ([] (asana "projects"))
  ([workspace] (asana (format "workspaces/%s/projects" workspace))))

;;; Tags

(defn create-workspace-tag
  "Creates a tag for a workspace

  Args:
  tag-name: name of the tag to be created
  workspace: id# of workspace in which tag is to be created
  "
  [tag workspace]
  (asana-post "tags" {"name" tag, "workspace", workspace}))

(defn show-tag
  "Shows info about a tag

  Args:
  tag-id: id# of tag
  "
  [tag-id]
  (asana (format "tags/%s" tag-id)))

(defn update-tag
  "Updates a tag

  Args:
  tag-id: id# of tag
  "
  [tag-id]
  (asana-put (format "tags/%s" tag-id)))

(defn list-tag-tasks
  "Gets tasks for a tag

  Args:
  tag-id: id# of task
  "
  [tag-id]
  (asana (format "tags/%s/tasks" tag-id)))

(defn list-tags
  "Shows available tags for workspace

  Args:
  workspace: id# of workspace
  "
  ([] (asana "tags"))
  ([workspace] (asana (format "workspaces/%s/tags" workspace))))

;;; Stories

(defn list-task-stories
  "Lists stories for task

  Args:
  task-id: id# of task
  "
  [task-id]
  (asana (format "tasks/%s/stories" task-id)))

(defn show-story
  "Shows full story

  Args:
  story-id: id# of a story
  "
  [story-id]
  (asana (format "stories/%s" story-id)))

(defn add-task-comment
  "Adds a comment to an object

  Args:
  text: Comment to be posted
  "
  [task-id text]
  (asana-post (format "tasks/%s/stories" task-id) {"text" text}))

;;; Workspaces

(defn list-workspaces
  "Lists workspaces"
  []
  (asana "workspaces"))

(defn update-workspace
  "Updates workspace

  Args:
  workspace-id: id# of workspace
  name: Update name
  "
  [workspace-id new-name]
  (asana-put (format "workspaces/%s" workspace-id) {"name" new-name}))

;;; Teams

(defn show-teams
  "Shows all teams you're a member of in an organization

  Args:
  organization-id: id# of organization
  "
  [organization-id]
  (asana (format "organizations/%s/teams" organization-id)))

;;; Attachments

(defn show-attachment
  "Shows a single attachment

  Args:
  attachment-id: id# of attachment
  "
  [attachment-id]
  (asana (format "attachments/%s" attachment-id)))

(defn list-task-attachements
  "Shows all attachments on a task

  Args:
  task-id: id# of task
  "
  [task-id]
  (asana (format "tasks/%s/attachments" task-id)))
