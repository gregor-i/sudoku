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

        compiledScalaFrontend = pkgs.callPackage nix/compiled-frontend.nix {
          inherit mkSbtDerivation fs sbt;
        };
        bundledScalaFrontend = pkgs.callPackage nix/bundled-frontend.nix {
          inherit fs pkgs compiledScalaFrontend;
        };
        styles = pkgs.callPackage nix/styles.nix { inherit fs pkgs; };

        assetsWithoutServiceWorker = pkgs.symlinkJoin {
          name = "assets";
          paths = [ bundledScalaFrontend styles ./frontend/src/main/static ];
        };

        serviceWorker = pkgs.callPackage nix/service-worker.nix {
          inherit mkSbtDerivation fs sbt;
          assets = assetsWithoutServiceWorker;
        };

        assets = pkgs.symlinkJoin {
          name = "assets";
          paths = [ assetsWithoutServiceWorker serviceWorker ];
        };

        dockerImage = pkgs.dockerTools.buildLayeredImage {
          name = "gregor23/sudoku";
          tag = self.rev or "dirty";
          config.Cmd = [
            "${pkgs.static-web-server}/bin/static-web-server"
            "-p"
            "8080"
            "-d"
            assets
          ];
        };

      in {
        devShells.default = pkgs.mkShell {
          packages = [ pkgs.sbt pkgs.static-web-server ];
          shellHook = ''
            static-web-server -p 8080 -d ./build --cache-control-headers=false &
          '';
        };

        packages = {
          default = assets;
          inherit assets assetsWithoutServiceWorker dockerImage;
        };

        formatter = pkgs.nixfmt;
      });
}
