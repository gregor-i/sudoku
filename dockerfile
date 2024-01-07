# syntax=docker/dockerfile:1

FROM node:18.14.2-alpine3.17 AS node-modules
WORKDIR /app
COPY package.json package-lock.json /app/
RUN npm clean-install --no-audit

FROM sbtscala/scala-sbt:eclipse-temurin-focal-17.0.5_8_1.8.2_3.2.1 AS build-frontend-step
WORKDIR /app
RUN mkdir build
COPY build.sbt build.sbt
COPY .scalafmt.conf .scalafmt.conf
COPY project project
RUN sbt update
COPY service-worker/src service-worker/src
COPY model /app/model
COPY frontend /app/frontend
RUN sbt frontend/fullOptJS

FROM node-modules AS build-frontend
COPY --from=build-frontend-step /app/frontend/target/scala-3.2.1/frontend-opt.js /app/frontend/target/scala-3.2.1/frontend-opt.js
RUN node_modules/.bin/esbuild /app/frontend/target/scala-3.2.1/frontend-opt.js --outfile=build/app.js --bundle --minify

FROM sbtscala/scala-sbt:eclipse-temurin-focal-17.0.5_8_1.8.2_3.2.1 AS build-sw
WORKDIR /app
RUN mkdir build
COPY build.sbt build.sbt
COPY .scalafmt.conf .scalafmt.conf
COPY project project
RUN sbt update
COPY service-worker service-worker
RUN sbt service-worker/fullOptJS
RUN cp service-worker/target/scala-3.2.1/service-worker-opt.js build/sw.js

FROM node-modules AS build-css
RUN mkdir build
COPY frontend/src/main/css frontend/src/main/css
RUN node_modules/.bin/sass frontend/src/main/css/app.sass build/app.css \
     --no-source-map

FROM busybox:1.35
WORKDIR /app
COPY --from=node-modules /app/node_modules/@fortawesome/fontawesome-free/webfonts build
COPY --from=node-modules /app/node_modules/@fortawesome/fontawesome-free/svgs/solid/trash.svg build/trash.svg
COPY frontend/src/main/static build
COPY --from=build-sw /app/build/sw.js /app/build/sw.js
COPY --from=build-css /app/build/app.css /app/build/app.css
COPY --from=build-frontend /app/build/app.js /app/build/app.js
EXPOSE 80
CMD ["busybox", "httpd", "-f", "-v", "-h", "/app/build"]
