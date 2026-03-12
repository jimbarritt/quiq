# quiq - A tui app that runs tests and shows you beautiful results

You can run it in a repo and it will autodetect what kind of project you are in and just start running tests.

By default it will enter watch mode and wait for changes and then run tests.

At any time, you can press `r` and it will generate a beautiful clear report in HTML and open it in an OS browser window.

## Usage

```sh
cargo install quiq
quiq
```

## Development

```sh
just        # build
just run    # run
just publish # publish to crates.io (requires cargo login)
```
