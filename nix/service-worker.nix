{ mkSbtDerivation, fs, sbt, assets }:
mkSbtDerivation {
  pname = "sudoku-service-worker";
  version = "0.1.0";
  depsSha256 = "sha256-7EHJF9cVz7b9Z53oZPh1wywm15SnmYhQlOF9xWVohyY=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions
      [
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
    cp -r service-worker/target/scala-3.2.1/service-worker-opt/main.js $out/sw.js
  '';
}
