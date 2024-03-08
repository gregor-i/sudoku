{ pkgs, fs, compiledScalaFrontend }:
pkgs.buildNpmPackage {
  pname = "sudoku-frontend";
  version = "0.1.0";
  npmDepsHash = "sha256-uQ5j3tQVSbFmunfptCKTXuWn1isfDXJTBkAOhu2rLeQ=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions [ ../package-lock.json ];
  };

  dontNpmBuild = true;

  compiledScalaFrontend = compiledScalaFrontend;

  installPhase = ''
    mkdir -p $out
    export NODE_PATH=./node_modules
    ./node_modules/.bin/esbuild $compiledScalaFrontend/main.js --outfile=$out/app.js --bundle --minify
  '';
}
