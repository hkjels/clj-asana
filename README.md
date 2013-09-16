# clj-asana

Clojure Wrapper for [Asana API](http://developer.asana.com/documentation/).

This project is a work in progress. Here's what's currently available:

### Users
- user-info
- list-users

### Tasks
- create-task
- show-task
- update-task
- delete-task
- list-tasks
- list-task-projects
- add-project-task
- delete-project-task
- show-project-tasks
- show-workspace-tasks
- show-subtasks
- create-subtask
- set-parent

### Projects
- create-project
- show-project
- update-project
- delete-project
- list-project

### Tags
- create-tag
- show-tag
- list-task-tags

### Stories
- list-stories
- show-story
- add-comment

### Teams
- show-my-teams

### Workspaces
- list-workspaces
- update-workspace

#REAL TODO

show-user-info
list-users
create-task
show-task
update-task
rm-task
- list-tasks (IDK)
list-subtasks
create-subtask
set-parent
list-task-stories
add-task-comment
list-task-projects
add-task-project
rm-task-project
list-task-tags
add-task-tag
rm-task-tag
add-task-followers
rm-task-followers
create-project
show-project
update-project
rm-project
list-project-tasks
list-projects
create-workspace-tag
show-tag
- update-tag
- list-tag-tasks
- list-tags




Todo:

- Asana Connect
- Error handling
- Documentation (:P)

## Usage

```clj
[clj-asana "0.0.1-SNAPSHOT"]
```

You need to define your API-KEY. A dummy API-KEY is provided. You may edit the
code or simply bind your own

```clj
(def api-key "YOUR_API_KEY")
```
## Bugs? Feature requests? Pull requests?

All of those are welcome. You can [file issues][issues] or [submit pull requests][pulls] in this repository.

[issues]: https://github.com/decached/clj-asana/issues
[pulls]: https://github.com/decached/clj-asana/pulls

## License

Copyright © 2013 Akash Kothawale

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
