{ mkSbtDerivation, fs, sbt, assets }:
mkSbtDerivation {
  pname = "sudoku-service-worker";
  version = "0.1.0";
  depsSha256 = "sha256-d33GS2Smz6iMaDm1BzoCwUqvKozPnnWE82q7cK/d7F0=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions [
      ../build.sbt
      ../project/plugins.sbt
      ../project/build.properties
      ../service-worker/src/main
    ];
  };

  inherit assets;
  depsWarmupCommand = ''
    sbt service-worker/fullLinkJS
  '';

  buildInputs = [ sbt ];
  buildPhase = ''
    export ASSET_FILES=$(ls $assets)
    sbt service-worker/fullLinkJS
  '';

  installPhase = ''
    mkdir -p $out
    cp -r service-worker/target/scala-3.7.3/service-worker-opt/main.js $out/sw.js
  '';
}
