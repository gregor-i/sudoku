{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";
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

      in {
        devShells.default = pkgs.mkShell {
          packages = [ pkgs.sbt pkgs.static-web-server ];
          shellHook = ''
            echo shellhook
          '';
        };

        packages = {
          default = assets;
          inherit assets assetsWithoutServiceWorker;
        };

        formatter = pkgs.nixfmt;
      });
}
