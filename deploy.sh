#!/bin/bash

sbtn "clean;fullLinkJS"
cp -L index.html main.js ../fommil.github.io/kerbal
cd ../fommil.github.io
git add kerbal
git commit -m "update kerbal-calculator"
git push
