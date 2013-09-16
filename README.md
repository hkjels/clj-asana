# clj-asana

Clojure Wrapper for [Asana API](http://developer.asana.com/documentation/).

Documentaion is available at [clj-asana documentation](http://decached.com/clj-asana/)

This project is a work in progress.

## Usage

```clj
[clj-asana "0.1.0"]
```

You need to define your API-KEY. A dummy API-KEY is provided. You may edit the
code or simply bind your own

```clj
(def api-key "YOUR_API_KEY")
```

And then just shoot queries.

```clj
(list-workspaces)
```

## TODO

- Upload attachment feature
- Error handling

## Bugs? Feature requests? Pull requests?

All of those are welcome. You can [file issues][issues] or [submit pull requests][pulls] in this repository.

[issues]: https://github.com/decached/clj-asana/issues
[pulls]: https://github.com/decached/clj-asana/pulls

## License

Copyright © 2013 Akash Kothawale

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
