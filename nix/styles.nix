{ pkgs, fs }:
pkgs.buildNpmPackage {
  pname = "sudoku-frontend";
  version = "0.1.0";
  npmDepsHash = "sha256-uQ5j3tQVSbFmunfptCKTXuWn1isfDXJTBkAOhu2rLeQ=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions
      [
        ../package-lock.json
        ../frontend/src/main/css
      ];
  };

  dontNpmBuild = true;

  installPhase = ''
    mkdir -p $out
    ./node_modules/.bin/sass frontend/src/main/css/app.sass $out/app.css --no-source-map --style compressed
    cp -r node_modules/@fortawesome/fontawesome-free/webfonts/* $out/.
    cp node_modules/@fortawesome/fontawesome-free/svgs/solid/trash.svg $out/.
  '';
}
