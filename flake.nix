{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.11";
    utils.url = "github:numtide/flake-utils";
    sbtDerivation.url = "github:zaninime/sbt-derivation";
    sbtDerivation.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, utils, sbtDerivation }:
    utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        fs = pkgs.lib.fileset;
        mkSbtDerivation = sbtDerivation.mkSbtDerivation.${system};
        sbt = pkgs.sbt;

        compiledScalaFrontend = (import nix/compiled-frontend.nix) { inherit mkSbtDerivation fs sbt; };
        bundledScalaFrontend = (import nix/bundled-frontend.nix) { inherit fs pkgs compiledScalaFrontend; };
        styles = (import nix/styles.nix) { inherit fs pkgs; };

        assetsWithoutServiceWorker = pkgs.symlinkJoin {
          name = "assets";
          paths = [
            bundledScalaFrontend
            styles
            ./frontend/src/main/static
          ];
        };

        serviceWorker = (import nix/service-worker.nix) { inherit mkSbtDerivation fs sbt; assets = assetsWithoutServiceWorker; };

        assets = pkgs.symlinkJoin {
          name = "assets";
          paths = [
            assetsWithoutServiceWorker
            serviceWorker
          ];
        };

        dockerImage = pkgs.dockerTools.buildImage {
          name = "sudoku";

          config = {
            Cmd = [ "echo 1" ];
          };

#          copyToRoot = pkgs.buildEnv {
#            name = "image-root";
#            paths = [ pkgs.static-web-server assets ];
#            pathsToLink = [ "/bin" ];
#          };
#
#          config = {
#            Cmd = [ "${pkgs.static-web-server} --port 8080 --root $assets keks" ];
#            ExposedPorts = 8080;
#          };
        };


      in
      {
        devShells.default = pkgs.mkShell {
          packages = [ pkgs.sbt pkgs.static-web-server ];
          shellHook = ''
            echo shellhook
          '';
        };

        packages = {
          default = assets;
          inherit assets assetsWithoutServiceWorker  dockerImage;
        };
      }
    );
}
