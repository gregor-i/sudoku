#!/usr/bin/fish

nix build .#dockerImage -L
cat (readlink result) | docker load
