# Sudoku

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/gregor-i/sudoku/website?style=plastic)
![GitHub top language](https://img.shields.io/github/languages/top/gregor-i/sudoku?style=plastic)

A simple Single-page application and Progressive web application to play Sudoku. Powered by `Scala.js`, `snabbdom` and `bulma`.

Deployed at: [https://sudoku.ihmor.com/](https://sudoku.ihmor.com/)

### Generating png icon. Requires inkscape
```
inkscape \
    --export-png=frontend/src/main/static/favicon.png \
    --export-background-opacity=0 --without-gui frontend/src/main/static/favicon.svg
```


### generate docker image:

size nix: 53_667_578

```sh
#!/usr/bin/fish

nix build .#dockerImage -L

cp (readlink result) temp.tar.gz

docker load -i temp.tar.gz

rm -f temp.tar.gz
```