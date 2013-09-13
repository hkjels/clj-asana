# clj-asana

Clojure Wrapper for Asana API.

This project is a work in progress. Here's what's currently available:

- add-project-task
- add-story
- add-tag-task
- create-project
- create-tag
- create-task
- add-parent
- create-subtask
- get-basic-auth
- get-project
- get-project-tasks
- get-story
- get-subtasks
- get-tag-tasks
- rm-tag-task
- get-task-tags
- get-tags
- get-task
- list-projects
- list-stories
- list-tasks
- list-users
- list-workspaces
- rm-project-task
- update-project
- update-task
- update-workspace
- user-info

Todo:

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
