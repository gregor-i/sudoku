{ mkSbtDerivation, fs, sbt }:
mkSbtDerivation {
  pname = "sudoku-frontend";
  version = "0.1.0";
  depsSha256 = "sha256-ClqDunYzF+mwakvKSemcnWxAs8MS/ydOlMLGC1pXN5c=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions [
      ../build.sbt
      ../project/plugins.sbt
      ../project/build.properties
      ../model/src/main
      ../frontend/src/main
    ];
  };

  depsWarmupCommand = "sbt frontend/fullLinkJS";

  buildInputs = [ sbt ];
  buildPhase = "sbt frontend/fullLinkJS";

  installPhase = ''
    cp -r frontend/target/scala-3.2.1/frontend-opt $out
  '';
}
